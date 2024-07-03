package org.grnet.pidmr;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.grnet.pidmr.repository.ActionRepository;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.repository.RegexRepository;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
public class KeycloakTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();


    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
