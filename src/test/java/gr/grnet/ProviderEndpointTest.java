package gr.grnet;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import gr.grnet.pidmr.dto.InformativeResponse;
import gr.grnet.pidmr.dto.Validity;
import gr.grnet.pidmr.endpoint.ProviderEndpoint;
import gr.grnet.pidmr.entity.Action;
import gr.grnet.pidmr.entity.Provider;
import gr.grnet.pidmr.pagination.PageResource;
import gr.grnet.pidmr.service.ProviderService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(MetaResolverTestProfile.class)
@TestHTTPEndpoint(ProviderEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProviderEndpointTest {

    @Inject
    ProviderService providerService;

    @ConfigProperty(name = "list.providers.file")
    String providersPath;

    @ConfigProperty(name = "list.actions.file")
    String actionsPath;


    @BeforeAll
    public void setup() {

        QuarkusMock.installMockForInstance(new MockableProvider(), providerService);
    }

    @Test
    public void fetchAllProviders() {

        var response = given()
                .contentType(ContentType.JSON)
                .get()
                .thenReturn();

        assertEquals(200, response.statusCode());
        assertEquals(8, response.body().as(PageResource.class).getContent().size());
        assertEquals(8, response.body().as(PageResource.class).getTotalElements());
        assertEquals(1, response.body().as(PageResource.class).getNumberOfPage());
    }

    @Test
    public void pidNotEmpty(){

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("pid may not be empty.", informativeResponse.message);
    }

    @Test
    public void validArk(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/13030/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("ark", validity.type);
    }

    @Test
    public void validArkLower(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "aRk:/13030/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("ark", validity.type);
    }

    @Test
    public void validArkUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ARK:/12345/141e86dc-d396-4e59-bbc2-4c3bf5326152")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("ark", validity.type);
    }

    @Test
    public void notValidArk(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/?eee/<Ccc")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertFalse(validity.valid);
        assertEquals("ark", validity.type);
    }

    @Test
    public void validArxiv(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "arXiv:1501.00001")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("arXiv", validity.type);
    }

    @Test
    public void validArxivLower(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "arxIV:1501.00001")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("arXiv", validity.type);
    }

    @Test
    public void validArxivUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ARXIV:2207.14689v2")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("arXiv", validity.type);
    }

    @Test
    public void validArxivBefore2007(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "arXiv:math.RT/0309136")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("arXiv", validity.type);
    }

    @Test
    public void notValidArxiv(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "arXiv:2207.1468$9v2")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertFalse(validity.valid);
        assertEquals("arXiv", validity.type);
    }

    @Test
    public void validSwh(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "swh:1:cnt:94a9ed024d3859793618152ea559a168bbcbb5e2")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("swh", validity.type);
    }

    @Test
    public void validSwhLower(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "SwH:1:cnt:94a9ed024d3859793618152ea559a168bbcbb5e2")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("swh", validity.type);
    }

    @Test
    public void validSwhUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "SWH:1:rev:309cf2674ee7a0749978cf8265ab91a60aea0f7d")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("swh", validity.type);
    }

    @Test
    public void notValidSwh(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "swh:1:rel:22ece559cc7cc2%364edc5e5593d63ae8bd229f9f")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertFalse(validity.valid);
        assertEquals("swh", validity.type);
    }

    @Test
    public void validDoi(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "doi:10.3352/jeehp.2013.10.3")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("doi", validity.type);
    }

    @Test
    public void validDoiLower(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "dOi:10.1111/dome.12082")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("doi", validity.type);
    }

    @Test
    public void validDoiUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "doi:10.1016/j.jpsychires.2017.11.014")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("doi", validity.type);
    }


    @Test
    public void notValidDoi(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "doi:12.1111/dome.12082")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertFalse(validity.valid);
        assertEquals("doi", validity.type);
    }

    @Test
    public void validEpic(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "21.T15999/TEST04")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("21", validity.type);
    }


    @Test
    public void notValidEpic(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "21.R11148/7317d72eb37156ced029")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertFalse(validity.valid);
        assertEquals("21", validity.type);
    }

    @Test
    public void validEpicOld(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "11500/ATHENA-0000-0000-258B-A")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("epic old", validity.type);
    }

    @Test
    public void validArkWithType(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("type", "ark")
                .queryParam("pid", "ark:/13030/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("ark", validity.type);
    }

    @Test
    public void validArkWithTypeError(){

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .queryParam("type", "doi")
                .queryParam("pid", "ark:/13030/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(406)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("ark:/13030/tf5p30086k doesn't belong to this type : doi.", informativeResponse.message);
    }

    @Test
    public void validAGermanUriWithType(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("type", "urn:nbn:de")
                .queryParam("pid", "urn:nbn:de:hbz:6-85659524771")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("urn:nbn:de", validity.type);
    }

    @Test
    public void validAFinishUriWithType(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("type", "urn:nbn:fi")
                .queryParam("pid", "urn:nbn:fi-fe2021080942632")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
        assertEquals("urn:nbn:fi", validity.type);
    }

    @Test
    public void validGermanUriUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "uRn:nBn:DE:hbz:6-85659524771")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
    }

    @Test
    public void validFinishUriUpper(){

        var validity = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "Urn:nbN:Fi-fe2021080942632")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(Validity.class);

        assertTrue(validity.valid);
    }

    @Test
    public void notSupportedType(){

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .queryParam("type", "not_supported")
                .queryParam("pid", "ark:/13030/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(406)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("This type {not_supported} is not supported.", informativeResponse.message);
    }

    @Test
    public void notSupportedPid(){

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "lalala/tf5p30086k")
                .get("/validate")
                .then()
                .assertThat()
                .statusCode(406)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("lalala/tf5p30086k doesn't belong to any of the available types.", informativeResponse.message);
    }

    public class MockableProvider extends ProviderService {

        @Override
        @SneakyThrows(IOException.class)
        public Set<Provider> getProviders()  {

            var mapper = JsonMapper.builder()
                    .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                    .build();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(providersPath).getFile());
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(Set.class, Provider.class));
        }

        @Override
        @SneakyThrows(IOException.class)
        public Set<Action> getActions()  {

            var mapper = JsonMapper.builder()
                    .build();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(actionsPath).getFile());
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(Set.class, Action.class));
        }
    }
}
