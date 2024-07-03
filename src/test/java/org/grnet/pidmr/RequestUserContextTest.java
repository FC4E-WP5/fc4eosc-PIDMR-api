package org.grnet.pidmr;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.dockerjava.api.exception.BadRequestException;
import io.quarkus.oidc.TokenIntrospection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.grnet.pidmr.util.RequestUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.StringReader;

public class RequestUserContextTest {

    @Mock
    private TokenIntrospection tokenIntrospectionMock;

    @Mock
    private KeycloakAdminService keycloakAdminServiceMock;

    @InjectMocks
    private RequestUserContext requestUserContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock behavior for token introspection
        when(tokenIntrospectionMock.getJsonObject()).thenReturn(createMockJsonObject("fake_voperson_id"));

        // Mock behavior for KeycloakAdminService
        when(keycloakAdminServiceMock.getUserEmail("fake_voperson_id")).thenReturn("fakeuser@example.com");
    }

    @Test
    public void testValidContextInitialization() {
        // Verify that the context initializes correctly with mocked values
        assertEquals("fake_voperson_id", requestUserContext.getVopersonID());
        assertEquals("fakeuser@example.com", requestUserContext.getUserEmail());
    }

    @Test
    public void testMissingVopersonId() {
        // Simulate a scenario where voperson_id is missing in token introspection
        when(tokenIntrospectionMock.getJsonObject()).thenThrow(new BadRequestException(""));

        // Verify that a BadRequestException is thrown
        assertThrows(BadRequestException.class, () -> new RequestUserContext(tokenIntrospectionMock, keycloakAdminServiceMock));
    }

    private jakarta.json.JsonObject createMockJsonObject(String vopersonId) {

        JsonReader jsonReader = Json.createReader(new StringReader(vopersonId));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
    }
}

