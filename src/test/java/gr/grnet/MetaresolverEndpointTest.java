package gr.grnet;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import gr.grnet.pidmr.dto.InformativeResponse;
import gr.grnet.pidmr.endpoint.MetaResolverEndpoint;
import gr.grnet.pidmr.entity.Action;
import gr.grnet.pidmr.entity.MetaResolver;
import gr.grnet.pidmr.entity.Provider;
import gr.grnet.pidmr.service.MetaresolverService;
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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@TestProfile(MetaResolverTestProfile.class)
@TestHTTPEndpoint(MetaResolverEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetaresolverEndpointTest {

    @Inject
    ProviderService providerService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    MetaresolverService metaresolverService;

    @ConfigProperty(name = "list.providers.file")
    String providersPath;

    @ConfigProperty(name = "list.metaresolvers.file")
    String metaresolversPath;

    @ConfigProperty(name = "list.actions.file")
    String actionsPath;



    @BeforeAll
    public void setup() {

        QuarkusMock.installMockForInstance(new MockableProvider(), providerService);
        QuarkusMock.installMockForInstance(new MockableMetaresolver(objectMapper, providerService), metaresolverService);

    }

    @Test
    public void pidNotEmpty(){

        var informativeResponse = given()
                .contentType(ContentType.JSON)
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .as(InformativeResponse.class);

        assertEquals("pid may not be empty.", informativeResponse.message);
    }

    @Test
    public void resolvePIDViaAPI(){

        var headers = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/67531/metapth346793")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .getHeaders();

        assertEquals("GET", headers.get("http-method").getValue());
        assertEquals("http://hdl.handle.net/21.T11999/METARESOLVER@ark:/67531/metapth346793", headers.get("location").getValue());
    }

    @Test
    public void resolvePIDWithModeViaAPI(){

        var headers = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/67531/metapth346793")
                .queryParam("pidMode", "metadata")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .getHeaders();

        assertEquals("POST", headers.get("http-method").getValue());
        assertEquals("http://hdl.handle.net/21.T11999/METARESOLVER@ark:/67531/metapth346793?metadata", headers.get("location").getValue());
    }

    @Test
    public void notAcceptablePID(){

        Exception exception = assertThrows(
                NotAcceptableException.class,
                () -> metaresolverService.resolve("not_acceptable_pid", ""));

        assertEquals("not_acceptable_pid doesn't belong to any of the available types.", exception.getMessage());
    }

    @Test
    public void resolvePID(){

       var location = metaresolverService.resolve("ark:/67531/metapth346793", "");

        assertEquals("http://hdl.handle.net/21.T11999/METARESOLVER@ark:/67531/metapth346793", location);
    }

    @Test
    public void resolvePIDWithSupportedMode(){

        var location = metaresolverService.resolve("ark:/67531/metapth346793", "metadata");

        assertEquals("http://hdl.handle.net/21.T11999/METARESOLVER@ark:/67531/metapth346793?metadata", location);
    }

    @Test
    public void resolvePIDWithNotSupportedMode(){

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> metaresolverService.resolve("ark:/67531/metapth346793", "resource"));


        assertEquals("This mode {resource} is not supported.", exception.getMessage());
    }

    @Test
    public void resolvePIDWithNoModes(){

        Exception exception = assertThrows(
                BadRequestException.class,
                () -> metaresolverService.resolve("arXiv:1501.00001", "resource"));


        assertEquals("This mode {resource} is not supported.", exception.getMessage());
    }

    public class MockableProvider extends ProviderService {

        @Override
        @SneakyThrows(IOException.class)
        public Set<Provider> getProviders() {

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

    public class MockableMetaresolver extends MetaresolverService {

        public MockableMetaresolver(ObjectMapper objectMapper, ProviderService providerService) {
            super(objectMapper, providerService);
        }

        @Override
        @SneakyThrows(IOException.class)
        public Set<MetaResolver> getMetaresolvers(){

            var mapper = JsonMapper.builder()
                    .build();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(metaresolversPath).getFile());
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(Set.class, MetaResolver.class));
        }
    }
}
