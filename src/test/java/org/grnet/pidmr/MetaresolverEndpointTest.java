package org.grnet.pidmr;

import jakarta.inject.Inject;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.LocationDto;
import org.grnet.pidmr.endpoint.MetaResolverEndpoint;
import org.grnet.pidmr.exception.ModeIsNotSupported;
import org.grnet.pidmr.service.MetaresolverService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


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

//    @Test
//    public void resolvePIDViaAPI(){
//
//        var location = given()
//                .contentType(ContentType.JSON)
//                .queryParam("pid", "ark:/67531/metapth346793")
//                .get("/resolve")
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(LocationDto.class);
//
//        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/", location.url);
//    }

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

        assertEquals("https://n2t.net/ark:/67531/metapth346793/%3F", location.url);
    }

//    @Test
//    public void resolvePID(){
//
//       var location = metaresolverService.resolve("ark:/67531/metapth346793", "landingpage");
//
//        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/", location);
//    }

//    @Test
//    public void resolvePIDWithSupportedMode(){
//
//        var location = metaresolverService.resolve("ark:/67531/metapth346793", "metadata");
//
//        assertEquals("https://digital.library.unt.edu/ark:/67531/metapth346793/", location);
//    }

    @Test
    public void resolvePIDWithNotSupportedMode(){

        Exception exception = assertThrows(
                ModeIsNotSupported.class,
                () -> metaresolverService.resolve("ark:/67531/metapth346793", "resource"));


        assertEquals("This mode {resource} is not supported.", exception.getMessage());
    }

    @Test
    public void resolvePIDWithNoModes(){

        Exception exception = assertThrows(
                ModeIsNotSupported.class,
                () -> metaresolverService.resolve("doi:10.1186/2041-1480-3-9", "resource"));


        assertEquals("This mode {resource} is not supported.", exception.getMessage());
    }

//    @Test
//    public void resolveZenodoMetadataModeViaAPI(){
//
//        var location = given()
//                .contentType(ContentType.JSON)
//                .queryParam("pid", "10.5281/zenodo.8056361")
//                .queryParam("pidMode", "metadata")
//                .get("/resolve")
//                .then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .as(LocationDto.class);
//
//        assertEquals("https://zenodo.org/api/records/8056361", location.url);
//    }
}
