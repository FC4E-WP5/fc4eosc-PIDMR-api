package gr.grnet;

import gr.grnet.pidmr.dto.Provider;
import gr.grnet.pidmr.endpoint.ProviderEndpoint;
import gr.grnet.pidmr.pagination.PageResource;
import gr.grnet.pidmr.service.ProviderService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@QuarkusTest
@TestHTTPEndpoint(ProviderEndpoint.class)
public class ProviderEndpointTest {

    @InjectMock
    ProviderService providerService;

    @Test
    public void fetchAllProviders() throws IOException {

        var pageResource = new PageResource<Provider>();

        var provider = new Provider();
        provider.pid = "ark";
        provider.name = "ARK alliance";
        provider.description = "Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects";
        pageResource.setContent(List.of(provider));
        pageResource.setNumberOfPage(1);
        pageResource.setTotalElements(1);

        Mockito.when(providerService.pagination(anyInt(), anyInt(), any())).thenReturn(pageResource);

        var response = given()
                .contentType(ContentType.JSON)
                .get()
                .thenReturn();

        assertEquals(200, response.statusCode());
        assertEquals(1, response.body().as(PageResource.class).getContent().size());
        assertEquals(1, response.body().as(PageResource.class).getTotalElements());
        assertEquals(1, response.body().as(PageResource.class).getNumberOfPage());
    }
}
