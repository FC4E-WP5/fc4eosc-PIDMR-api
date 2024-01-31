package org.grnet.pidmr;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.UpdateProviderDto;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.endpoint.AdminEndpoint;
import org.grnet.pidmr.service.DatabaseProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(AdminEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminEndpointTest extends KeycloakTest{

    @Inject
    DatabaseProviderService providerService;

    @Test
    public void createProviderNotValidAction(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-not-valid";
        request.description = "Test Provider.";
        request.regexes = Set.of("regexp");
        request.actions = Set.of("not_valid_action");
        request.example = "example";

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is an action that is not supported.", informativeResponse.message);
    }

    @Test
    public void createProvider(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-create-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("regexp");
        request.actions = Set.of("resource");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        assertEquals("test-create-provider", provider.type);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void createProviderExists(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-exist-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("regexp");
        request.actions = Set.of("resource");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var informativeResponse = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(409)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This Provider type {test-exist-provider} exists.", informativeResponse.message);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void createProviderWithRegex(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-regex-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource", "metadata");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var validity = given()
                .basePath("/v1/providers")
                .contentType(ContentType.JSON)
                .queryParam("type", provider.type)
                .queryParam("pid", "regexps")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void createProviderWithoutActions(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-regex-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.example = "example";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);


        assertEquals("resolution_modes should have at least one entry.", response.message);
    }

    @Test
    public void createProviderWithoutRegexes(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-regex-provider";
        request.description = "Test Provider.";
        request.actions = Set.of("resource", "metadata");
        request.example = "example";

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);


        assertEquals("regexes should have at least one entry.", response.message);
    }

    @Test
    public void getProvider(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-get-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource", "metadata");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .get("/providers/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProviderDto.class);

        assertEquals(request.name, response.name);
        assertEquals(request.type, response.type);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void getProviderNotFound(){

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .get("/providers/{id}", 1000L)
                .then()
                .assertThat()
                .statusCode(404)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("There is no Provider with the following id: "+1000L, response.message);
    }

    @Test
    public void deleteProvider(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-delete-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource", "metadata");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var response = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .contentType(ContentType.JSON)
                .delete("/providers/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("The Provider has been successfully deleted.", response.message);
    }

    @Test
    public void updateBasicProviderInformation(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-change-basic-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var updateRequest = new UpdateProviderDto();
        updateRequest.name = "Update Test Provider.";
        updateRequest.type = "test-update-provider";
        updateRequest.description = "Update Test Provider.";
        updateRequest.example = "updated example";

        var updatedProvider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateRequest)
                .contentType(ContentType.JSON)
                .patch("/providers/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProviderDto.class);

        assertEquals(updateRequest.type, updatedProvider.type);
        assertEquals(updateRequest.name, updatedProvider.name);
        assertEquals(updateRequest.description, updatedProvider.description);
        assertEquals(updateRequest.example, updatedProvider.example);
        assertEquals(request.regexes.stream().findFirst().get(), updatedProvider.regexes.stream().findFirst().get());
        assertEquals(request.actions.stream().findFirst().get(), updatedProvider.actions.stream().findFirst().get().mode);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void updateProviderRegexesAndActions(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-change-regex-action-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        var updateRequest = new UpdateProviderDto();
        updateRequest.regexes = Set.of("this is text");
        updateRequest.actions = Set.of("metadata");

        var updatedProvider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(updateRequest)
                .contentType(ContentType.JSON)
                .patch("/providers/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(ProviderDto.class);

        assertEquals(request.type, updatedProvider.type);
        assertEquals(request.name, updatedProvider.name);
        assertEquals(request.description, updatedProvider.description);
        assertEquals(request.example, updatedProvider.example);
        assertEquals(updateRequest.regexes.stream().findFirst().get(), updatedProvider.regexes.stream().findFirst().get());
        assertEquals(updateRequest.actions.stream().findFirst().get(), updatedProvider.actions.stream().findFirst().get().mode);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }

    @Test
    public void providerForbidden(){

        var createForbidden = given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .body(new ProviderRequest())
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You do not have permission to access this resource.", createForbidden.message);

        var updateForbidden = given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .body(new UpdateProviderDto())
                .contentType(ContentType.JSON)
                .patch("/providers/{id}", 3)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You do not have permission to access this resource.", updateForbidden.message);

        var deleteForbidden = given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .contentType(ContentType.JSON)
                .delete("/providers/{id}", 2)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You do not have permission to access this resource.", deleteForbidden.message);

        var readForbidden = given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .contentType(ContentType.JSON)
                .get("/providers/{id}", 1)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You do not have permission to access this resource.", readForbidden.message);
    }

    @Test
    public void providerForbiddenPermissionsOnEntity(){

        var request = new ProviderRequest();
        request.name = "Test Provider.";
        request.type = "test-change-regex-action-provider";
        request.description = "Test Provider.";
        request.regexes = Set.of("rege(x(es)?|xps?)");
        request.actions = Set.of("resource");
        request.example = "example";

        var provider = given()
                .auth()
                .oauth2(getAccessToken("admin"))
                .body(request)
                .contentType(ContentType.JSON)
                .post("/providers")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(ProviderDto.class);

        //alice is a provider_admin and can only manage the providers she creates.
        var updateForbidden = given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .body(new UpdateProviderDto())
                .contentType(ContentType.JSON)
                .patch("/providers/{id}", provider.id)
                .then()
                .assertThat()
                .statusCode(403)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("You do not have permission to access this resource.", updateForbidden.message);

        providerService.deleteProviderByIdWithoutCheckingPermissions(provider.id);
    }
}
