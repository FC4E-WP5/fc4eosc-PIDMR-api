package org.grnet.pidmr;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.LocationDto;
import org.grnet.pidmr.endpoint.MetaResolverEndpoint;
import org.grnet.pidmr.entity.Action;
import org.grnet.pidmr.entity.MetaResolver;
import org.grnet.pidmr.entity.Provider;
import org.grnet.pidmr.service.MetaresolverService;
import org.grnet.pidmr.service.ProviderService;
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

    @ConfigProperty(name = "proxy.resolve.mode.url")
    String proxy;

    @ConfigProperty(name = "proxy.resolve.mode.body.attribute")
    String bodyAttribute;

    @ConfigProperty(name = "proxy.resolve.mode.body.attribute.prefix")
    String bodyAttributePrefix;

    @ConfigProperty(name = "proxy.resolve.mode.url.append.param")
    String appendParam;


    @BeforeAll
    public void setup() {

        QuarkusMock.installMockForInstance(new MockableProvider(), providerService);

        var mockableMetaresolverService = new MockableMetaresolver(objectMapper, providerService);
        mockableMetaresolverService.setProxy(proxy);
        mockableMetaresolverService.setBodyAttribute(bodyAttribute);
        mockableMetaresolverService.setBodyAttributePrefix(bodyAttributePrefix);
        mockableMetaresolverService.setAppendParam(appendParam);

        QuarkusMock.installMockForInstance(mockableMetaresolverService, metaresolverService);
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

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/67531/metapth346793")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("http://hdl.handle.net/21.T11973/MR@ark:/67531/metapth346793", location.url);
    }

    @Test
    public void resolvePIDWithModeViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "ark:/67531/metapth346793")
                .queryParam("pidMode", "metadata")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/?", location.url);
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

        assertEquals("http://hdl.handle.net/21.T11973/MR@ark:/67531/metapth346793", location);
    }

    @Test
    public void resolvePIDWithSupportedMode(){

        var location = metaresolverService.resolve("ark:/67531/metapth346793", "metadata");

        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/?", location);
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
                () -> metaresolverService.resolve("doi:10.1186/2041-1480-3-9", "resource"));


        assertEquals("This mode {resource} is not supported.", exception.getMessage());
    }

    @Test
    public void resolveGermanUriViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "urn:nbn:de:hbz:6-85659524771")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("http://hdl.handle.net/21.T11973/MR@urn:nbn:de:hbz:6-85659524771", location.url);
    }

    @Test
    public void resolveFinishUriViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "urn:nbn:fi-fe2021080942632")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("http://hdl.handle.net/21.T11973/MR@urn:nbn:fi-fe2021080942632", location.url);
    }

    @Test
    public void resolveEpicOldViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "11500/ATHENA-0000-0000-2401-6")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("http://hdl.handle.net/11500/ATHENA-0000-0000-2401-6", location.url);
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
