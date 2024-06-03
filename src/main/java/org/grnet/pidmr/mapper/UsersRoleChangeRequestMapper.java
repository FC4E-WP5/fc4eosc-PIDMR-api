package org.grnet.pidmr.mapper;

import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UsersRoleChangeRequestMapper {

    UsersRoleChangeRequestMapper INSTANCE = Mappers.getMapper(UsersRoleChangeRequestMapper.class);

    @IterableMapping(qualifiedByName = "roleChangeRequestToDto")
    List<RoleChangeRequestDto> roleChangeRequestsToDto(List<RoleChangeRequest> roleChangeRequestList);
    @Named("roleChangeRequestToDto")
    RoleChangeRequestDto roleChangeRequestToDto(RoleChangeRequest roleChangeRequest);
}
