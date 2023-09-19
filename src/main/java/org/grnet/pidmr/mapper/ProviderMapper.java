package org.grnet.pidmr.mapper;

import org.grnet.pidmr.dto.ActionDto;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.entity.Provider;
import org.grnet.pidmr.service.ProviderService;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This mapper turns the Providers into Data Transform Objects.
 */
@Mapper
public interface ProviderMapper {

    ProviderMapper INSTANCE = Mappers.getMapper(ProviderMapper.class);

    @IterableMapping(qualifiedByName = "providerToDto")
    List<ProviderDto> providersToDto(List<Provider> providers);

    @Mapping(source = "actions", target = "actions", qualifiedByName = "actions")
    @Named("providerToDto")
    ProviderDto providerToDto(Provider provider);

    @Named("databaseProviderToDto")
    ProviderDto databaseProviderToDto(org.grnet.pidmr.entity.database.Provider provider);

    @IterableMapping(qualifiedByName = "databaseProviderToDto")
    List<ProviderDto> databaseProvidersToDto(List<org.grnet.pidmr.entity.database.Provider> providers);


    @Named("actions")
    default Set<ActionDto> actions(Set<String> actions) {

        if (actions.isEmpty()){
            return Collections.EMPTY_SET;
        } else {

            ProviderService providerService = CDI.current().select(ProviderService.class).get();

            var providerActions = providerService.actionsToMap();

            return actions
                    .stream()
                    .map(providerActions::get)
                    .map(providerAction->{

                        var dto = new ActionDto();
                        dto.mode = providerAction.getMode();
                        dto.name = providerAction.getName();

                        return dto;
                    })
                    .collect(Collectors.toSet());
        }
    }
}