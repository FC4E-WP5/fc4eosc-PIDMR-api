package org.grnet.pidmr.endpoint;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.pagination.PageResource;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.service.DatabaseProviderService;
import org.grnet.pidmr.util.ServiceUriInfo;
import org.grnet.pidmr.validator.constraints.NotFoundEntity;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    DatabaseProviderService providerService;

    @ConfigProperty(name = "server.url")
    String serverUrl;

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

    @Tag(name = "Provider")
    @Operation(
            summary = "Create a new Provider.",
            description = "Endpoint for creating a new Provider.")
    @APIResponse(
            responseCode = "201",
            description = "Created.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid request payload.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Not Found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "409",
            description = "Provider already exists.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Valid @NotNull(message = "The request body is empty.") ProviderRequest request, @Context UriInfo uriInfo) {

        var response = providerService.create(request);

        var serverInfo = new ServiceUriInfo(serverUrl.concat(uriInfo.getPath()));

        return Response.created(serverInfo.getAbsolutePathBuilder().path(String.valueOf(response.id)).build()).entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider by ID.",
            description = "Endpoint for retrieving a Provider by its ID.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Provider.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "Not Found.",
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
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProviderById(@Parameter(
            description = "The ID of the Provider to retrieve.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER)) @PathParam("id")
                                        @Valid @NotFoundEntity(repository = ProviderRepository.class, message = "There is no Provider with the following id:") Long id) {

        var response = providerService.getProviderById(id);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Delete a Provider by ID.",
            description = "Endpoint for deleting a Provider by its ID.")
    @APIResponse(
            responseCode = "200",
            description = "The Provider has been successfully deleted.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Not Found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @DELETE
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response deleteProviderById(@Parameter(
            description = "The ID of the Provider to delete.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER)) @PathParam("id")
                                    @Valid @NotFoundEntity(repository = ProviderRepository.class, message = "There is no Provider with the following id:") Long id) {

        var deleted = providerService.deleteProviderById(id);

        var response = new InformativeResponse();

        if(deleted){

            response.code = 200;
            response.message = "The Provider has been successfully deleted.";
            return Response.ok().entity(response).build();
        } else {

            response.code = 500;
            response.message = "A problem occurred during the Provider deletion.";
            return Response.serverError().entity(response).build();
        }
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