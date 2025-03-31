package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.dto.ValidatorResponse;
import org.grnet.pidmr.enums.Validator;
import org.grnet.pidmr.mapper.UsersRoleChangeRequestMapper;
import org.grnet.pidmr.pagination.Page;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.pagination.PageableImpl;
import org.grnet.pidmr.repository.RoleChangeRequestsRepository;
import org.grnet.pidmr.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

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

    public PageResource<ValidatorResponse> getValidatorsByPage(int page, int size, UriInfo uriInfo){

        var allValidators =  Arrays.stream(Validator.values())
                .map(validator -> new ValidatorResponse(validator.name(), validator.getDescription()))
                .collect(Collectors.toList());

        var partition = Utility.partition(new ArrayList<>(allValidators), size);

        var validators = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new PageableImpl<ValidatorResponse>();

        pageable.list = validators;
        pageable.index = page;
        pageable.size = size;
        pageable.count = allValidators.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, uriInfo);
    }
}