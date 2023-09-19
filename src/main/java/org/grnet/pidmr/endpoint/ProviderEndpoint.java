package org.grnet.pidmr.endpoint;

import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.pagination.PageResource;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.pidmr.service.ProviderServiceI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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
    @Named("database-provider-service")
    ProviderServiceI providerService;

    @Tag(name = "Provider")
    @Operation(
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

    @Tag(name = "Provider")
    @Operation(
            summary = "Validates PIDs.",
            description = "This operation check the validity of each identifier. " +
                    "Every Provider has a regex based on which the validation is performed.")
    @APIResponse(
            responseCode = "200",
            description = "The result of the validation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Validity.class)))
    @APIResponse(
            responseCode = "406",
            description = "The pid or type is not supported.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/validate")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response validate(@Parameter(name = "pid", in = QUERY, required = true, example = "ark:/13030/tf5p30086k", allowReserved = true,
            description = "The PID to be validated.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("pid") @NotEmpty(message = "pid may not be empty.") String pid, @Parameter(name = "type", in = QUERY,
            description = "When this parameter is used, the API does not search the list of available Providers but directly retrieves the Provider of this type.", schema = @Schema(type = SchemaType.STRING)) @DefaultValue("") @QueryParam("type") String type) {

        var validity = providerService.validation(pid, type);

        return Response.ok().entity(validity).build();
    }

    public static class PageableProvider extends PageResource<ProviderDto> {

        private List<ProviderDto> content;

        @Override
        public List<ProviderDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ProviderDto> content) {
            this.content = content;
        }
    }
}