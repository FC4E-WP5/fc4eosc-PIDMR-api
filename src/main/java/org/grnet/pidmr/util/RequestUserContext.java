package org.grnet.pidmr.util;

import io.quarkus.oidc.TokenIntrospection;
import lombok.Getter;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.BadRequestException;

@RequestScoped
public class RequestUserContext {

    @Getter
    private final String vopersonID;

    public RequestUserContext(TokenIntrospection tokenIntrospection) {

        try {

            this.vopersonID = tokenIntrospection.getJsonObject().getString("voperson_id");
        } catch (Exception e) {

            String message = "The User's unique identifier voperson_id is missing from the access token.";
            throw new BadRequestException(message);
        }
    }
}
