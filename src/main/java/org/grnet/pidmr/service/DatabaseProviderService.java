package org.grnet.pidmr.service;

import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.entity.database.Provider;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.mapper.ProviderMapper;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ActionRepository;
import org.grnet.pidmr.repository.ProviderRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

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

    /**
     * This method stores a new Provider in the database.
     * @param request The Provider to be created.
     * @return The created Provider.
     */
    @Transactional
    public ProviderDto create(ProviderRequest request){

        var optionalType = providerRepository.find("from Provider p where p.type = ?1", request.type)
                .stream()
                .findFirst();

        if(optionalType.isPresent()){

            throw new ConflictException(String.format("This Provider type {%s} exists.", request.type));
        }

        request
                .actions
                .forEach(action-> actionRepository.findByIdOptional(action).orElseThrow(()-> new NotFoundException("There is an action that is not supported.")));

        var newProvider = new Provider();
        newProvider.setName(request.name);
        newProvider.setType(request.type);
        newProvider.setDescription(request.description);
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
}
