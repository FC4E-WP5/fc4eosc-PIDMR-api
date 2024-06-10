package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.dto.UpdateRoleChangeRequestStatus;
import org.grnet.pidmr.dto.UserRoleChangeRequest;
import org.grnet.pidmr.dto.UserProfileDto;
import org.grnet.pidmr.entity.database.History;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.grnet.pidmr.enums.MailType;
import org.grnet.pidmr.enums.RoleChangeRequestStatus;
import org.grnet.pidmr.mapper.UsersRoleChangeRequestMapper;
import org.grnet.pidmr.pagination.Page;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.pagination.PageableImpl;
import org.grnet.pidmr.repository.HistoryRepository;
import org.grnet.pidmr.repository.RoleChangeRequestsRepository;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.grnet.pidmr.service.keycloak.KeycloakRole;
import org.grnet.pidmr.util.RequestUserContext;
import org.grnet.pidmr.util.Utility;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@ApplicationScoped
public class UserService {

    @ConfigProperty(name = "quarkus.oidc.client-id")
    @Getter
    @Setter
    private String clientID;

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    RoleChangeRequestsRepository roleChangeRequestsRepository;

    @Inject
    KeycloakAdminService keycloakAdminService;

    @Inject
    MailerService mailerService;

    @ConfigProperty(name = "api.keycloak.user.id")
    String attribute;

    /**
     * Injection point for the history repository
     */
    @Inject
    HistoryRepository historyRepository;

    public UserProfileDto getUserProfile(){

        var dto = new UserProfileDto();
        dto.id = requestUserContext.getVopersonID();
        dto.roles = requestUserContext.getRoles(clientID);

        return dto;
    }

    @Transactional
    public void persistRoleChangeRequest(UserRoleChangeRequest userRoleChangeRequest){

        keycloakAdminService.doRolesExist(List.of(userRoleChangeRequest.role));

        var roleChangeRequest = new RoleChangeRequest();

        roleChangeRequest.setUserId(requestUserContext.getVopersonID());
        roleChangeRequest.setName(userRoleChangeRequest.name);
        roleChangeRequest.setSurname(userRoleChangeRequest.surname);
        roleChangeRequest.setRole(userRoleChangeRequest.role);
        roleChangeRequest.setDescription(userRoleChangeRequest.description);
        roleChangeRequest.setEmail(userRoleChangeRequest.email);
        roleChangeRequest.setRequestedOn(Timestamp.from(Instant.now()));
        roleChangeRequest.setStatus(RoleChangeRequestStatus.PENDING);

        roleChangeRequestsRepository.persist(roleChangeRequest);


        CompletableFuture.supplyAsync(() ->
                mailerService.retrieveAdminEmails()
        ).thenAccept(addrs -> {
            mailerService.sendMails(roleChangeRequest, MailType.ADMIN_ALERT_NEW_CHANGE_ROLE_REQUEST, addrs);
            if (!addrs.contains(roleChangeRequest)) {
                mailerService.sendMails(roleChangeRequest, MailType.USER_ROLE_CHANGE_REQUEST_CREATION, Arrays.asList(roleChangeRequest.getEmail()));
            }
        });

    }

    @Transactional
    public void updateRoleChangeRequest(Long id, UpdateRoleChangeRequestStatus updateRoleChangeRequestStatus){

        var request = roleChangeRequestsRepository.findById(id);
        request.setUpdatedOn(Timestamp.from(Instant.now()));
        request.setUpdatedBy(requestUserContext.getVopersonID());
        request.setStatus(RoleChangeRequestStatus.valueOf(updateRoleChangeRequestStatus.status));

        if(RoleChangeRequestStatus.valueOf(updateRoleChangeRequestStatus.status).equals(RoleChangeRequestStatus.APPROVED)){

            keycloakAdminService.assignRoles(request.getUserId(), List.of(request.getRole()));
        } else if(RoleChangeRequestStatus.valueOf(updateRoleChangeRequestStatus.status).equals(RoleChangeRequestStatus.REJECTED)){

            keycloakAdminService.removeRoles(request.getUserId(), List.of(request.getRole()));
        }

        MailerService.CustomCompletableFuture.runAsync(() -> mailerService.sendMails(request, MailType.USER_ALERT_CHANGE_ROLE_REQUEST_STATUS, Arrays.asList(request.getEmail())));
    }

    /**
     * Adds the deny_access role to the specified user in Keycloak, denying access to the API.
     *
     * @param userId  The unique identifier of the user to whom the access will be restricted.
     * @param reason  The reason for denying access to the user.
     */
    @Transactional
    public void addDenyAccessRole(String userId, String reason) {

        var history = new History();
        history.setAction(reason);
        history.setUserId(requestUserContext.getVopersonID());
        history.setPerformedOn(Timestamp.from(Instant.now()));

        historyRepository.persist(history);
        keycloakAdminService.assignRoles(userId, List.of("deny_access"));
    }

    /**
     * Removes the deny_access role from the specified user in Keycloak, allowing access to the API.
     *
     * @param userId  The unique identifier of the user to whom the access will be allowed.
     * @param reason  The reason for allowing access to the user.
     */
    @Transactional
    public void removeDenyAccessRole(String userId, String reason) {

        var history = new History();
        history.setAction(reason);
        history.setUserId(requestUserContext.getVopersonID());
        history.setPerformedOn(Timestamp.from(Instant.now()));

        historyRepository.persist(history);
        keycloakAdminService.removeRoles(userId, List.of("deny_access"));
    }

    /**
     * Checks if the specified user exists.
     * @param userId the ID of the user to check.
     * @return
     */
    public void doesUserExist(String userId){

        var optional = getUsers()
                .stream()
                .filter(userProfileDto -> userProfileDto.id.equals(userId))
                        .findAny();

        optional.orElseThrow(()-> new NotFoundException(String.format("Not Found User %s", userId)));
    }

    /**
     * Assigns new roles to a specific user.
     *
     * @param userId The unique identifier of the user.
     * @param roles  List of role names to be assigned to the user.
     */
    public void assignRolesToUser(String userId, List<String> roles) {

        keycloakAdminService.doRolesExist(roles);
        keycloakAdminService.assignRoles(userId, roles);
    }

    /**
     * Retrieves a page of users.
     *
     * @param page The index of the page to retrieve (starting from 0).
     * @param size The maximum number of users to include in a page.
     * @param uriInfo The Uri Info.
     * @return A list of UserProfileDto objects representing the users in the requested page.
     */
    public PageResource<UserProfileDto> getUsersByPage(int page, int size, UriInfo uriInfo) {

        var users = getUsers();

        var partition = Utility.partition(new ArrayList<>(users), size);

        var partitionedUsers = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new PageableImpl<UserProfileDto>();

        pageable.list = partitionedUsers;
        pageable.index = page;
        pageable.size = size;
        pageable.count = users.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, uriInfo);
    }

    public Set<UserProfileDto> getUsers(){

        var roles = keycloakAdminService.fetchRoles();

        var keycloakUsers = roles.stream()
                .map(role->keycloakAdminService.fetchRolesMembers(role.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return keycloakUsers
                .stream()
                .map(keycloakUser->{

                    var user = new UserProfileDto();
                    user.email = keycloakUser.getEmail();
                    user.surname = keycloakUser.getLastName();
                    user.name = keycloakUser.getFirstName();
                    user.id = keycloakUser.getAttributes().get(attribute).get(0);
                    user.roles = keycloakAdminService.fetchUserRoles(user.id).stream().map(KeycloakRole::getName).collect(Collectors.toList());

                    return user;
                }).collect(Collectors.toSet());
    }

    /**
     * Retrieves all role change requests created by a specific user.
     *
     * @param page the page number for pagination
     * @param size the number of requests per page
     * @return a paginated list of role change requests
     */
    public PageResource<RoleChangeRequestDto> getRoleChangeRequestsByUser(int page, int size, UriInfo uriInfo) {

        // Retrieve pageable data from the repository
        var pageable = roleChangeRequestsRepository.fetchRoleChangeRequestByUser(page, size, requestUserContext.getVopersonID());
        return new PageResource<>(pageable, UsersRoleChangeRequestMapper.INSTANCE.roleChangeRequestsToDto(pageable.list()), uriInfo);
    }
}
