package org.grnet.pidmr.service;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.oidc.TokenIntrospection;
import io.vavr.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.grnet.pidmr.dto.*;
import org.grnet.pidmr.entity.database.Action;
import org.grnet.pidmr.entity.database.Provider;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.enums.MailType;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.interceptors.ManageEntity;
import org.grnet.pidmr.mapper.ProviderMapper;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ActionRepository;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.repository.RegexRepository;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.grnet.pidmr.util.RequestUserContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This ProviderService initializes the available Providers provided in the providers.conf file.
 */
@ApplicationScoped
@Named("database-provider-service")
public class DatabaseProviderService implements ProviderServiceI {

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ActionRepository actionRepository;

    @Inject
    RegexRepository regexRepository;

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    MailerService mailerService;

    @Inject
    KeycloakAdminService keycloakAdminService;

    @Override
    public Validity valid(String pid) {

        var regex = providerRepository.valid(pid);

        var validity = new Validity();
        validity.valid = regex.isPresent();
        validity.type = regex.isPresent() ? regex.get().getProvider().getType() : "";

        return validity;
    }

    @Override
    public Validity valid(String pid, String type) {

        var optionalProvider = providerRepository.find("from Provider p where p.type = ?1 and p.status = ?2", type, ProviderStatus.APPROVED).firstResultOptional();

        if (optionalProvider.isEmpty()) {
            throw new NotAcceptableException(String.format("This type {%s} is not supported.", type));
        }

        var validity = new Validity();
        validity.valid = providerRepository.valid(pid, optionalProvider.get()).isPresent();
        validity.type = type;

        return validity;
    }

    @Override
    public Provider getProviderByPid(String pid) {

        var optional = providerRepository.valid(pid);
        var regex = optional.orElseThrow(() -> new NotAcceptableException(String.format("%s doesn't belong to any of the available types.", pid)));

        return regex.getProvider();
    }

    @Override
    public PageResource<ProviderDto> pagination(int page, int size, UriInfo uriInfo) {

        var providers = providerRepository.fetchProvidersByPage(page, size);

        return new PageResource<>(providers, ProviderMapper.INSTANCE.databaseProvidersToDto(providers.list()), uriInfo);
    }

    public PageResource<AdminProviderDto> adminPagination(int page, int size, UriInfo uriInfo) {

        var providers = providerRepository.fetchAdminProvidersByPage(page, size);

        return new PageResource<>(providers, ProviderMapper.INSTANCE.databaseAdminProvidersToDto(providers.list()), uriInfo);
    }

    @Override
    public Set<Identification> multipleIdentification(String text) {

        var regexes = regexRepository.findAllRegexesBelongsToApprovedProviders();

        var identifications = new HashSet<Identification>();

        for (Regex regex : regexes) {

            check(text, Pattern.compile(regex.getRegex()), regex.getProvider(), identifications);
        }

        if (identifications.isEmpty()) {

            var identification = new Identification();
            identification.status = Identification.Status.INVALID;
            identification.type = "";
            identification.example = "";
            identifications.add(identification);
        }

        return identifications;
    }

    private void check(CharSequence cs, Pattern pattern, Provider provider, Set<Identification> identifications) {

        Matcher matcher = pattern.matcher(cs);

        var dto = ProviderMapper.INSTANCE.databaseProviderToDto(provider);

        if (matcher.matches()) {

            var identification = new Identification();
            identification.status = Identification.Status.VALID;
            identification.type = provider.getType();
            identification.example = provider.getExample();
            identification.actions = dto.actions;
            identifications.add(identification);
        }

        if (matcher.hitEnd()) {

            var identification = new Identification();
            identification.status = Identification.Status.AMBIGUOUS;
            identification.type = provider.getType();
            identification.example = provider.getExample();
            identification.actions = dto.actions;
            identifications.add(identification);
        }
    }

    private Identification check(CharSequence cs, Pattern pattern, Provider provider, Identification identification) {

        Matcher matcher = pattern.matcher(cs);

        var dto = ProviderMapper.INSTANCE.databaseProviderToDto(provider);

        if (matcher.matches()) {

            identification.status = Identification.Status.VALID;
            identification.type = provider.getType();
            identification.example = provider.getExample();
            identification.actions = dto.actions;
            return identification;
        }

        if (matcher.hitEnd()) {

            identification.status = Identification.Status.AMBIGUOUS;
            identification.type = provider.getType();
            identification.example = provider.getExample();
            identification.actions = dto.actions;
        }

        return identification;
    }

    private Provider setProviderForCreation(ProviderRequest request) {

        checkIfTypeExists(request.type);

        var newProvider = new Provider();
        newProvider.setName(request.name);
        newProvider.setType(request.type);
        newProvider.setDescription(request.description);
        newProvider.setExample(request.example);
        newProvider.setCreatedBy(requestUserContext.getVopersonID());
        newProvider.setStatus(ProviderStatus.PENDING);
        newProvider.setReliesOnDois(request.reliesOnDois);

        return newProvider;
    }

    /**
     * This method stores a new Provider in the database.
     *
     * @param request The Provider to be created.
     * @return The created Provider.
     * @throws
     */
    @Transactional
    public ProviderDto create(ProviderRequestV1 request) {

        checkIfActionsSupported(request.actions);

        var newProvider = setProviderForCreation(request);

        request
                .actions
                .forEach(action -> newProvider.addAction(actionRepository.findById(action), null));

        request.
                regexes
                .forEach(regex -> {
                    var regexp = new Regex();
                    regexp.setRegex(regex);
                    newProvider.addRegex(regexp);
                });
        providerRepository.persist(newProvider);

        var userID = newProvider.getCreatedBy();
        var emailContext = new EmailContextForStatusUpdate(userID, keycloakAdminService.getUserEmail(userID), newProvider.getId(), String.valueOf(newProvider.getStatus()));

        mailerService.sendEmailsWithContext(emailContext, MailType.ADMIN_ALERT_NEW_PID_TYPE_ENTRY_CREATION, MailType.PROVIDER_ADMIN_NEW_PID_TYPE_ENTRY_CREATION);

        return ProviderMapper.INSTANCE.databaseProviderToDto(newProvider);
    }

    /**
     * This method stores a new Provider in the database.
     *
     * @param request The Provider to be created.
     * @return The created Provider.
     * @throws
     */
    @Transactional
    public ProviderDto createV2(ProviderRequestV2 request) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        // Set the formatter's time zone to UTC
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        checkIfActionsSupported(request.actions.stream().map(action -> action.mode).collect(Collectors.toSet()));

        var newProvider = setProviderForCreation(request);

        request
                .actions
                .forEach
                        (action -> {
                            newProvider.addAction(actionRepository.findById(action.mode), action.endpoints);
                        });

        request.
                regexes
                .forEach(regex -> {
                    var regexp = new Regex();
                    regexp.setRegex(regex);
                    newProvider.addRegex(regexp);
                });


        providerRepository.persist(newProvider);
        var timestamp = formatter.format(Timestamp.from(Instant.now()));

        var userID = newProvider.getCreatedBy();
        var pidtype = newProvider.getType();

        var emailContext = new EmailContextForStatusUpdate(userID, keycloakAdminService.getUserEmail(userID), newProvider.getId(), String.valueOf(newProvider.getStatus()), pidtype,timestamp);
        mailerService.sendEmailsWithContext(emailContext, MailType.ADMIN_ALERT_NEW_PID_TYPE_ENTRY_CREATION, MailType.PROVIDER_ADMIN_NEW_PID_TYPE_ENTRY_CREATION);

        return ProviderMapper.INSTANCE.databaseProviderToDto(newProvider);
    }


    /**
     * This method deletes from database a Provider by its ID.
     *
     * @param id The Provider to be deleted.
     * @return Whether the Provider is successfully deleted or not.
     */
    @ManageEntity(entityType = "Provider")
    @Transactional
    public boolean deleteProviderById(Long id) {

        return providerRepository.deleteById(id);
    }

    @Transactional
    public boolean deleteProviderByIdWithoutCheckingPermissions(Long id) {

        return providerRepository.deleteById(id);
    }

    /**
     * Retrieves a specific Provider by its ID.
     *
     * @param providerId The ID of the Provider to retrieve.
     * @return The Provider stored in the database.
     */
    @ManageEntity(entityType = "Provider")
    public AdminProviderDto getProviderById(Long providerId) {

        var provider = providerRepository.findById(providerId);

        return ProviderMapper.INSTANCE.databaseAdminProviderToDto(provider);
    }

    private Provider setProviderForUpdating(Long id, UpdateProvider request) {

        var provider = providerRepository.findById(id);

        if (StringUtils.isNotEmpty(request.type)) {
            provider.setType(request.type);
        }

        if (!request.regexes.isEmpty()) {
            var regexes = provider.getRegexes();
            new ArrayList<>(regexes).forEach(provider::removeRegex);
            request.
                    regexes
                    .forEach(regex -> {
                        var regexp = new Regex();
                        regexp.setRegex(regex);
                        provider.addRegex(regexp);
                    });
        }

        if (StringUtils.isNotEmpty(request.name)) {
            provider.setName(request.name);
        }
        if (StringUtils.isNotEmpty(request.description)) {
            provider.setDescription(request.description);
        }
        if (StringUtils.isNotEmpty(request.example)) {
            provider.setExample(request.example);
        }
        provider.setReliesOnDois(request.reliesOnDois);
        provider.setStatus(ProviderStatus.PENDING);

        return provider;
    }

    /**
     * This method updates one or more attributes of a Provider.
     *
     * @param request The Provider attributes to be updated.
     * @param id      The Provider to be updated.
     * @return The updated Provider.
     */
    @ManageEntity(entityType = "Provider")
    @Transactional
    public ProviderDto update(Long id, UpdateProviderV1 request) {

        var provider = setProviderForUpdating(id, request);

        if (!request.actions.isEmpty()) {
            checkIfActionsSupported(request.actions);

            var actions = provider.getActions();
            var tuples = actions.stream().map(act -> Tuple.of(act.getAction(), act.getEndpoints())).collect(Collectors.toList());
            new ArrayList<>(actions).forEach(action -> provider.removeAction(action.getAction()));
            Panache.getEntityManager().flush();
            request.actions.forEach(newAction -> {

                var dbAction = actionRepository.findById(newAction);
                var optional = tuples.stream().filter(tuple -> tuple._1.equals(dbAction)).findFirst();
                optional.ifPresentOrElse(tpl -> provider.addAction(dbAction, tpl._2), () -> provider.addAction(dbAction, null));
            });
        }
        return ProviderMapper.INSTANCE.databaseProviderToDto(provider);
    }

    /**
     * This method updates one or more attributes of a Provider.
     *
     * @param request The Provider attributes to be updated.
     * @param id      The Provider to be updated.
     * @return The updated Provider.
     */
    @ManageEntity(entityType = "Provider")
    @Transactional
    public ProviderDto updateV2(Long id, UpdateProviderV2 request) {

        var provider = setProviderForUpdating(id, request);

        if (!request.actions.isEmpty()) {

            checkIfActionsSupported(request.actions.stream().map(action -> action.mode).collect(Collectors.toSet()));
            var actions = provider.getActions();
            new ArrayList<>(actions).forEach(action -> provider.removeAction(action.getAction()));
            Panache.getEntityManager().flush();
            request.actions.forEach(newAction -> provider.addAction(actionRepository.findById(newAction.mode), newAction.endpoints));
        }
        return ProviderMapper.INSTANCE.databaseProviderToDto(provider);
    }

    /**
     * Retrieves the available resolution modes.
     *
     * @return A list containing the available resolution modes.
     */
    public Set<String> getResolutionModes() {

        var actions = actionRepository.findAll().list();

        return actions.
                stream()
                .map(Action::getMode)
                .collect(Collectors.toSet());
    }

    /**
     * This method checks if the given provider type exists in the database. If not, it throws a ConflictException.
     *
     * @param type The Provider type.
     * @throws ConflictException If type exists.
     */
    private void checkIfTypeExists(String type) {

        var optionalType = providerRepository.find("from Provider p where p.type = ?1", type)
                .stream()
                .findFirst();

        if (optionalType.isPresent()) {

            throw new ConflictException(String.format("This Provider type {%s} exists.", type));
        }

    }

    /**
     * This method checks if the given actions are supported. If there is one that is not supported, it throws a NotFoundException.
     *
     * @param actions The Provider actions;
     * @throws NotFoundException If there is an action that is not supported.
     */
    private void checkIfActionsSupported(Set<String> actions) {

        actions
                .forEach(action -> actionRepository.findByIdOptional(action).orElseThrow(() -> new NotFoundException("There is an action that is not supported.")));

    }

    /**
     * Updates the status of a Provider with the provided status.
     *
     * @param id     The ID of the Provider to update.
     * @param status The new status to set for the Provider.
     * @return The updated Provider.
     */
    @Transactional
    public AdminProviderDto updateProviderStatus(Long id, ProviderStatus status) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        // Set the formatter's time zone to UTC
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        var newProvider = providerRepository.findById(id);
        newProvider.setStatus(status);
        newProvider.setStatusUpdatedBy(requestUserContext.getVopersonID());

        var pidtype=newProvider.getType();
        var userID = newProvider.getCreatedBy();
        var timestamp = formatter.format(Timestamp.from(Instant.now()));
        var emailContext = new EmailContextForStatusUpdate(userID, keycloakAdminService.getUserEmail(userID), newProvider.getId(), String.valueOf(status), pidtype,timestamp);
        mailerService.sendEmailsWithContext(emailContext, MailType.PROVIDER_ADMIN_ALERT_CHANGE_PID_TYPE_ENTRY_REQUEST_STATUS, MailType.PROVIDER_ADMIN_ALERT_CHANGE_PID_TYPE_ENTRY_REQUEST_STATUS);


        return ProviderMapper.INSTANCE.databaseAdminProviderToDto(newProvider);
    }

    @Override
    public Identification identify(String text) {

        var regexes = regexRepository.findAllRegexesBelongsToApprovedProviders();

        var identification = new Identification();
        identification.status = Identification.Status.INVALID;
        identification.type = "";
        identification.example = "";

        for (Regex regex : regexes) {

            var identified = check(text, Pattern.compile(regex.getRegex()), regex.getProvider(), identification);

            if (identified.status.equals(Identification.Status.VALID) || identified.status.equals(Identification.Status.AMBIGUOUS)) {

                break;
            }
        }

        return identification;
    }

}