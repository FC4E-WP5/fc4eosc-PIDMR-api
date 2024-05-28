package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.UserPromotionRequest;
import org.grnet.pidmr.dto.UserProfileDto;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.grnet.pidmr.enums.PromotionRequestStatus;
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
}
