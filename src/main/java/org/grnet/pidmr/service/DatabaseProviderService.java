package org.grnet.pidmr.service;

import org.apache.commons.lang3.StringUtils;
import org.grnet.pidmr.dto.Identification;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.UpdateProviderDto;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.entity.database.Action;
import org.grnet.pidmr.entity.database.Provider;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.mapper.ProviderMapper;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ActionRepository;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.repository.RegexRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This ProviderService initializes the available Providers provided in the providers.conf file.
 */
@ApplicationScoped
@Named("database-provider-service")
public class DatabaseProviderService implements ProviderServiceI{

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ActionRepository actionRepository;

    @Inject
    RegexRepository regexRepository;

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

        var optionalProvider = providerRepository.find("from Provider p where p.type= ?1", type).firstResultOptional();

        if(optionalProvider.isEmpty()){
            throw new NotAcceptableException(String.format("This type {%s} is not supported.", type));
        }

        var validity = new Validity();
        validity.valid = providerRepository.valid(pid, optionalProvider.get()).isPresent();
        validity.type = type;

        return validity;
    }

    @Override
    public Provider getProviderByPid(String pid, String mode) {

        var optional = providerRepository.valid(pid);

        var regex = optional.orElseThrow(()->new NotAcceptableException(String.format("%s doesn't belong to any of the available types.", pid)));

        regex
                .getProvider()
                .getActions()
                .stream()
                .filter(action->action.getMode().equals(mode))
                .findAny()
                .orElseThrow(()->new BadRequestException(String.format("This mode {%s} is not supported.", mode)));

        return regex.getProvider();
    }

    @Override
    public PageResource<ProviderDto> pagination(int page, int size, UriInfo uriInfo) {

        var providers = providerRepository.fetchProvidersByPage(page, size);

        return new PageResource<>(providers, ProviderMapper.INSTANCE.databaseProvidersToDto(providers.list()), uriInfo);
    }

    @Override
    public Identification identify(String text) {

        var regexes = regexRepository
                .findAll()
                .list();

        var identification = new Identification();
        identification.status = Identification.Status.INVALID;
        identification.type = "";
        identification.example = "";

        for(Regex regex: regexes){

            var identified = check(text, Pattern.compile(regex.getRegex()), regex.getProvider(), identification);

            if(identified.status.equals(Identification.Status.VALID) || identified.status.equals(Identification.Status.INCOMPLETE)){

                break;
            }
        }

        return identification;
    }

    private Identification check(CharSequence cs, Pattern pattern, Provider provider, Identification identification) {

        Matcher matcher = pattern.matcher(cs);

        if(matcher.matches()){

            identification.status = Identification.Status.VALID;
            identification.type = provider.getType();
            identification.example = provider.getExample();
            return identification;
        }

        if (matcher.hitEnd()) {

            identification.status = Identification.Status.INCOMPLETE;
            identification.type = provider.getType();
            identification.example = provider.getExample();
        }

        return identification;
    }


    /**
     * This method stores a new Provider in the database.
     * @param request The Provider to be created.
     * @return The created Provider.
     * @throws
     */
    @Transactional
    public ProviderDto create(ProviderRequest request){

        checkIfTypeExists(request.type);
        checkIfActionsSupported(request.actions);

        var newProvider = new Provider();
        newProvider.setName(request.name);
        newProvider.setType(request.type);
        newProvider.setDescription(request.description);
        newProvider.setExample(request.example);
        request
                .actions
                .forEach(action->newProvider.addAction(actionRepository.findById(action)));

        request.
                regexes
                .forEach(regex->{
                    var regexp = new Regex();
                    regexp.setRegex(regex);
                    newProvider.addRegex(regexp);
                });

        providerRepository.persist(newProvider);

        return ProviderMapper.INSTANCE.databaseProviderToDto(newProvider);
    }

    /**
     * This method deletes from database a Provider by its ID.
     * @param id The Provider to be deleted.
     * @return Whether the Provider is successfully deleted or not.
     */
    @Transactional
    public boolean deleteProviderById(Long id){

        return providerRepository.deleteById(id);
    }

    /**
     * Retrieves a specific Provider by its ID.
     *
     * @param providerId The ID of the Provider to retrieve.
     * @return The Provider stored in the database.
     */
    public ProviderDto getProviderById(Long providerId) {

        var provider = providerRepository.findById(providerId);

        return ProviderMapper.INSTANCE.databaseProviderToDto(provider);
    }

    /**
     * This method updates one or more attributes of a Provider.
     * @param request The Provider attributes to be updated.
     * @param id The Provider to be updated.
     * @return The updated Provider.
     */
    @Transactional
    public ProviderDto update(UpdateProviderDto request, Long id){

        var provider = providerRepository.findById(id);

        if(StringUtils.isNotEmpty(request.type)){

            checkIfTypeExists(request.type);
            provider.setType(request.type);
        }

        if(!request.actions.isEmpty()){
            checkIfActionsSupported(request.actions);

            var actions = provider.getActions();
            new ArrayList<>(actions).forEach(provider::removeAction);
            request.actions.forEach(newAction-> provider.addAction(actionRepository.findById(newAction)));
        }

        if(!request.regexes.isEmpty()){

            var regexes = provider.getRegexes();
            new ArrayList<>(regexes).forEach(provider::removeRegex);
            request.
                    regexes
                    .forEach(regex->{
                        var regexp = new Regex();
                        regexp.setRegex(regex);
                        provider.addRegex(regexp);
                    });

        }

        if(StringUtils.isNotEmpty(request.name)){

            provider.setName(request.name);
        }

        if(StringUtils.isNotEmpty(request.description)){

            provider.setDescription(request.description);
        }

        if(StringUtils.isNotEmpty(request.example)){
            provider.setExample(request.example);
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
     * @param type The Provider type.
     * @throws ConflictException If type exists.
     */
    private void checkIfTypeExists(String type){

        var optionalType = providerRepository.find("from Provider p where p.type = ?1", type)
                .stream()
                .findFirst();

        if(optionalType.isPresent()){

            throw new ConflictException(String.format("This Provider type {%s} exists.", type));
        }

    }

    /**
     * This method checks if the given actions are supported. If there is one that is not supported, it throws a NotFoundException.
     * @param actions The Provider actions;
     * @throws NotFoundException If there is an action that is not supported.
     */
    private void checkIfActionsSupported(Set<String> actions) {

        actions
                .forEach(action -> actionRepository.findByIdOptional(action).orElseThrow(() -> new NotFoundException("There is an action that is not supported.")));

    }
}
