package org.grnet.pidmr;

import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.*;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.service.keycloak.KeycloakAdminService;
import org.grnet.pidmr.service.keycloak.KeycloakRole;
import org.grnet.pidmr.util.RequestUserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.grnet.pidmr.endpoint.UserEndpoint;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(UserEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserEndpointTest extends KeycloakTest {
    @ConfigProperty(name = "quarkus.oidc.client-id")
    @Getter
    @Setter
    private String clientID;
    @InjectMock
    RequestUserContext requestUserContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(requestUserContext.getUserEmail()).thenReturn("admin@example.com");
        when(requestUserContext.getUserEmail()).thenReturn("alice@example.com");
    }
    @ParameterizedTest
    @MethodSource("userProfileTestData")
    public void testUserProfileEndpoint(String id, String userName, List<String> roles) {
        var request     = new UserProfileDto();
        request.id      = id;
        request.roles   = roles;
        request.name    = "Foo";
        request.surname = "Foo";
        request.email   = "foo@email.org";

        when(requestUserContext.getVopersonID()).thenReturn(request.id);
        when(requestUserContext.getUserEmail()).thenReturn(request.email);
        when(requestUserContext.getRoles(clientID)).thenReturn(request.roles);

        var userProfile = given()
                .auth()
                .oauth2(getAccessToken(userName))
                .body(request)
                .contentType(ContentType.JSON)
                .get("/profile")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(UserProfileDto.class);

        assertEquals(request.id, userProfile.id);
        assertEquals(request.roles, userProfile.roles);
    }

    static Stream<Object[]> userProfileTestData() {
        return Stream.of(
                new Object[]{"admin_voperson_id", "admin", Collections.singletonList("admin")},
                new Object[]{"alice_voperson_id", "alice", Collections.singletonList("provider_admin")},
                new Object[]{"bob_voperson_id", "bob", Collections.emptyList()}
        );
    }
    @Test
    public void testNoDataRoleChangeRequestEndpoint() {
        // Prepare the UserRoleChangeRequest
        var request         = new UserRoleChangeRequest();
        request.name        = "Jame";
        request.surname     = "Smith";
        request.email       = "";
        request.role        = "provider_admin";
        request.description = "Change my user role";

        // Send a POST request to /promote-user-role with invalid role
        given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/role-change-request")
                .then()
                .statusCode(400); // Bad Request status code
    }

    @Test
    public void testUnauthorizedAccess() {
        // Send a GET request to /profile without authentication
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/role-change-request")
                .then()
                .statusCode(401); // Unauthorized status code
    }

    @Test
    public void testInternalServerErrorRoleChangeRequestEndpoint() {
        // Send a POST request to /promote-user-role with invalid data
        given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .body("invalidRequestBody")
                .contentType(ContentType.JSON)
                .when()
                .post("/role-change-request")
                .then()
                .statusCode(500); // Internal Server Error status code
    }
}

