package org.grnet.pidmr.mapper;

import jakarta.enterprise.inject.spi.CDI;
import org.grnet.pidmr.dto.AdminProviderDto;
import org.grnet.pidmr.dto.ResolutionModeDto;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.entity.Provider;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.service.ProviderService;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

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
    @Mapping(source = "regex", target = "regexes")
    @Mapping(target = "id", ignore = true)
    @Named("providerToDto")
    ProviderDto providerToDto(Provider provider);

    @Named("databaseProviderToDto")
    @Mapping(source = "regexes", target = "regexes", qualifiedByName = "database-regexes")
    ProviderDto databaseProviderToDto(org.grnet.pidmr.entity.database.Provider provider);

    @IterableMapping(qualifiedByName = "databaseProviderToDto")
    List<ProviderDto> databaseProvidersToDto(List<org.grnet.pidmr.entity.database.Provider> providers);

    @IterableMapping(qualifiedByName = "databaseAdminProviderToDto")
    List<AdminProviderDto> databaseAdminProvidersToDto(List<org.grnet.pidmr.entity.database.Provider> providers);

    @Named("databaseAdminProviderToDto")
    @Mapping(source = "regexes", target = "regexes", qualifiedByName = "database-regexes")
    @Mapping(source = "createdBy", target = "userId")
    AdminProviderDto databaseAdminProviderToDto(org.grnet.pidmr.entity.database.Provider provider);

    @Named("database-regexes")
    default Set<String> databaseRegexes(List<Regex> regexes) {

        return regexes
                    .stream()
                    .map(Regex::getRegex)
                    .collect(Collectors.toSet());
    }

    @Named("actions")
    default Set<ResolutionModeDto> actions(Set<String> actions) {

        if (actions.isEmpty()){
            return Collections.EMPTY_SET;
        } else {

            ProviderService providerService = CDI.current().select(ProviderService.class).get();

            var providerActions = providerService.actionsToMap();

            return actions
                    .stream()
                    .map(providerActions::get)
                    .map(providerAction->{

                        var dto = new ResolutionModeDto();
                        dto.mode = providerAction.getMode();
                        dto.name = providerAction.getName();

                        return dto;
                    })
                    .collect(Collectors.toSet());
        }
    }
}