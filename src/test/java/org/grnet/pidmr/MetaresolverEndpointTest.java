package org.grnet.pidmr;

import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.LocationDto;
import org.grnet.pidmr.endpoint.MetaResolverEndpoint;
import org.grnet.pidmr.service.MetaresolverService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@TestHTTPEndpoint(MetaResolverEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetaresolverEndpointTest {

    @Inject
    MetaresolverService metaresolverService;

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

        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/", location.url);
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
    public void resolvePID(){

       var location = metaresolverService.resolve("ark:/67531/metapth346793", "landingpage");

        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/", location);
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

        assertEquals("https://miami.uni-muenster.de/Record/2a2bf27d-5d02-4695-a35f-89e22f88a8ee", location.url);
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

        assertEquals("https://www.doria.fi/handle/10024/181584", location.url);
    }

    @Test
    public void resolveZenodoMetadataModeViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "10.5281/zenodo.8056361")
                .queryParam("pidMode", "metadata")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("https://zenodo.org/api/records/8056361", location.url);
    }

    @Test
    public void resolveZenodoLandingPageModeViaAPI(){

        var location = given()
                .contentType(ContentType.JSON)
                .queryParam("pid", "10.5281/zenodo.8056361")
                .queryParam("pidMode", "landingpage")
                .get("/resolve")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(LocationDto.class);

        assertEquals("https://zenodo.org/record/8056361", location.url);
    }
}
