package gr.grnet.pidmr.mapper;

import gr.grnet.pidmr.dto.ProviderDto;
import gr.grnet.pidmr.entity.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This mapper turns the Providers into Data Transform Objects.
 */
@Mapper
public interface ProviderMapper {

    ProviderMapper INSTANCE = Mappers.getMapper(ProviderMapper.class);

    List<ProviderDto> providersToDto(List<Provider> providers);
}
