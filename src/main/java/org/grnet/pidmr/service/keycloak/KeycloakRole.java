package org.grnet.pidmr.service.keycloak;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakRole {

    private String id;
    private String name;
    private String description;

    public KeycloakRole(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
