package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.UserPromotionRequest;
import org.grnet.pidmr.dto.UserProfileDto;
import org.grnet.pidmr.entity.database.History;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.grnet.pidmr.enums.PromotionRequestStatus;
import org.grnet.pidmr.repository.HistoryRepository;
import org.grnet.pidmr.repository.RoleChangeRequestsRepository;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.grnet.pidmr.util.RequestUserContext;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;


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
    public void persistRoleChangeRequest(UserPromotionRequest userPromotionRequest){

        keycloakAdminService.doRolesExist(List.of(userPromotionRequest.role));

        var roleChangeRequest = new RoleChangeRequest();

        roleChangeRequest.setUserId(requestUserContext.getVopersonID());
        roleChangeRequest.setName(userPromotionRequest.name);
        roleChangeRequest.setSurname(userPromotionRequest.surname);
        roleChangeRequest.setRole(userPromotionRequest.role);
        roleChangeRequest.setDescription(userPromotionRequest.description);
        roleChangeRequest.setEmail(userPromotionRequest.email);
        roleChangeRequest.setRequestedOn(Timestamp.from(Instant.now()));
        roleChangeRequest.setStatus(PromotionRequestStatus.PENDING);

        roleChangeRequestsRepository.persist(roleChangeRequest);
    }

    @Transactional
    public void approvePromotionRequest(Long id){

        var request = roleChangeRequestsRepository.findById(id);

        request.setStatus(PromotionRequestStatus.APPROVED);
        request.setUpdatedOn(Timestamp.from(Instant.now()));
        request.setUpdatedBy(requestUserContext.getVopersonID());
        keycloakAdminService.assignRoles(request.getUserId(), List.of(request.getRole()));
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
     * Checks if the specified user has executed a promotion request.
     * @param userId the ID of the user to check.
     * @return
     */
    public void hasUserExecutedPromotionRequest(String userId){

       roleChangeRequestsRepository.hasUserExecutedPromotionRequest(userId).orElseThrow(()-> new NotFoundException(String.format("Not Found User %s", userId)));
    }
}
