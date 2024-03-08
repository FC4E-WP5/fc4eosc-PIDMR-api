package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.UserProfileDto;
import org.grnet.pidmr.util.RequestUserContext;


@ApplicationScoped
public class UserService {

    @ConfigProperty(name = "quarkus.oidc.client-id")
    @Getter
    @Setter
    private String clientID;

    @Inject
    RequestUserContext requestUserContext;

    public UserProfileDto getUserProfile(){

        var dto = new UserProfileDto();
        dto.id = requestUserContext.getVopersonID();
        dto.roles = requestUserContext.getRoles(clientID);

        return dto;
    }
}
