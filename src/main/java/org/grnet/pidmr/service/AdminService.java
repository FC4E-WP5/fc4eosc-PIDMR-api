package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.mapper.UsersRoleChangeRequestMapper;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.RoleChangeRequestsRepository;

@ApplicationScoped
public class AdminService {

    @Inject
    RoleChangeRequestsRepository roleChangeRequestsRepository;

    /**
     * Retrieves a page of RoleChangeRequestDto objects.
     *
     * @param page The index of the page to retrieve (starting from 0).
     * @param size The maximum number of RoleChangeRequestDto objects to include in a page.
     * @return A list of RoleChangeRequestDto objects representing the RoleChangeRequests in the requested page.
     */
    public PageResource<RoleChangeRequestDto> getRoleChangeRequestsByPage(int page, int size, UriInfo uriInfo) {

        // Retrieve pageable data from the repository
        var pageable = roleChangeRequestsRepository.fetchRoleChangeRequestByPage(page, size);
        return new PageResource<>(pageable, UsersRoleChangeRequestMapper.INSTANCE.roleChangeRequestsToDto(pageable.list()), uriInfo);
    }
}