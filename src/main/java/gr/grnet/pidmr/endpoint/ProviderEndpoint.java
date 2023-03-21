package gr.grnet.pidmr.endpoint;

import gr.grnet.pidmr.dto.InformativeResponse;
import gr.grnet.pidmr.pagination.PageResource;
import gr.grnet.pidmr.dto.Provider;
import gr.grnet.pidmr.service.ProviderService;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v1/providers")
public class ProviderEndpoint {

    @Inject
    ProviderService providerService;

    public ProviderEndpoint(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Tag(name = "Provider")
    @org.eclipse.microprofile.openapi.annotations.Operation(
            summary = "Returns all the available Providers.",
            description = "This operation returns the list of Providers that the API supports. By default, the first page of 10 Providers will be returned. " +
                    "You can tune the default values by using the query parameters page and size.")
    @APIResponse(
            responseCode = "200",
            description = "List of the available Providers.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableProvider.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be between 1 and 100.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                           @Parameter(name = "size", in = QUERY,
                                   description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                           @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,  @Context UriInfo uriInfo) {


        return Response.ok().entity(providerService.pagination(page - 1, size, uriInfo)).build();
    }

    public static class PageableProvider extends PageResource<Provider> {

        private List<Provider> content;

        @Override
        public List<Provider> getContent() {
            return content;
        }

        @Override
        public void setContent(List<Provider> content) {
            this.content = content;
        }
    }
}