package org.grnet.pidmr.mapper;

import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UsersPromotionMapper {

    UsersPromotionMapper INSTANCE = Mappers.getMapper(UsersPromotionMapper.class);

    @IterableMapping(qualifiedByName = "roleChangeRequestToDto")
    List<RoleChangeRequestDto> roleChangeRequestsToDto(List<RoleChangeRequest> promotions);
    @Named("roleChangeRequestToDto")
    RoleChangeRequestDto roleChangeRequestToDto(RoleChangeRequest roleChangeRequest);
}
