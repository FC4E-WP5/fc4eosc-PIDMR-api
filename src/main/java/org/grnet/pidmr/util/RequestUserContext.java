package org.grnet.pidmr.util;

import io.quarkus.oidc.TokenIntrospection;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonString;
import jakarta.ws.rs.BadRequestException;
import lombok.Getter;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestScoped
public class RequestUserContext {

    @Getter
    private final String vopersonID;

    @Getter
    private final String userEmail;
    @Getter
    private final String username;

    @Inject
    private TokenIntrospection tokenIntrospection;

    @Inject
    private KeycloakAdminService keycloakAdminService;

    public RequestUserContext(TokenIntrospection tokenIntrospection, KeycloakAdminService keycloakAdminService) {

        try {

            this.vopersonID = tokenIntrospection.getJsonObject().getString("voperson_id");
            this.userEmail = keycloakAdminService.getUserEmail(vopersonID);
            this.username= tokenIntrospection.getJsonObject().getString("username");
            this.tokenIntrospection = tokenIntrospection;
        } catch (Exception e) {

            String message = "The User's unique identifier voperson_id is missing from the access token.";
            throw new BadRequestException(message);
        }
    }


    public List<String> getRoles(String clientID){

        if(Objects.isNull(tokenIntrospection.getObject("resource_access"))){

            return Collections.EMPTY_LIST;
        } else {

            var jsonArray =  tokenIntrospection
                    .getObject("resource_access")
                    .getJsonObject(clientID)
                    .getJsonArray("roles");

            return IntStream
                    .range(0,jsonArray.size())
                    .mapToObj(jsonArray::getJsonString)
                    .map(JsonString::getString)
                    .collect(Collectors.toList());
        }
    }
}
