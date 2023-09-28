package org.grnet.pidmr.service;

import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.entity.database.Provider;
import org.grnet.pidmr.mapper.ProviderMapper;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ProviderRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.UriInfo;

/**
 * This ProviderService initializes the available Providers provided in the providers.conf file.
 */
@ApplicationScoped
@Named("database-provider-service")
public class DatabaseProviderService implements ProviderServiceI{

    @Inject
    ProviderRepository providerRepository;

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
}
