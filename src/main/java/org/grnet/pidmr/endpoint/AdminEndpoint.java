package org.grnet.pidmr.endpoint;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.pidmr.dto.AdminProviderDto;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.UpdateProviderDto;
import org.grnet.pidmr.dto.UpdateProviderStatus;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.service.DatabaseProviderService;
import org.grnet.pidmr.util.ServiceUriInfo;
import org.grnet.pidmr.validator.constraints.NotFoundEntity;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Objects;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v1/admin")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class AdminEndpoint {

    @ConfigProperty(name = "server.url")
    String serverUrl;

    @Inject
    DatabaseProviderService providerService;

    @Tag(name = "Admin")
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
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
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
    @SecurityRequirement(name = "Authentication")
    @POST
    @Path("/providers")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Valid @NotNull(message = "The request body is empty.") ProviderRequest request, @Context UriInfo uriInfo) {

        var response = providerService.create(request);

        var serverInfo = new ServiceUriInfo(serverUrl.concat(uriInfo.getPath()));

        return Response.created(serverInfo.getAbsolutePathBuilder().path(String.valueOf(response.id)).build()).entity(response).build();
    }

    @Tag(name = "Admin")
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
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
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
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/providers/{id}")
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

    @Tag(name = "Admin")
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
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
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
    @SecurityRequirement(name = "Authentication")
    @DELETE
    @Path("/providers/{id}")
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

    @Tag(name = "Admin")
    @Operation(
            summary = "Update an existing Provider.",
            description = "Endpoint for updating an existing Provider by ID. You can update a part or all attributes of Provider. The empty or null values are ignored.")
    @APIResponse(
            responseCode = "200",
            description = "Updated.",
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
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
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
    @SecurityRequirement(name = "Authentication")
    @PATCH
    @Path("/providers/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Parameter(
            description = "The ID of the Provider to update.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER)) @PathParam("id")
                           @Valid @NotFoundEntity(repository = ProviderRepository.class, message = "There is no Provider with the following id:") Long id,
                           @Valid @NotNull(message = "The request body is empty.") UpdateProviderDto request) {

        ProviderDto response = null;
        try {
            response = providerService.update(id, request);

        } catch (ArcUndeclaredThrowableException e) {

            if(!Objects.isNull(e.getCause()) && !Objects.isNull(e.getCause().getCause()) && !Objects.isNull(e.getCause().getCause().getCause()) && e.getCause().getCause().getCause() instanceof ConstraintViolationException){

                throw new ConflictException(String.format("This Provider type {%s} exists.", request.type));
            } else {
                throw new ServerErrorException("Internal Server Error", 500);
            }
        }

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "List of the Providers the user can manage.",
            description = "Endpoint for retrieving the Providers the user can manage.")
    @APIResponse(
            responseCode = "200",
            description = "List of the Providers the user can manage.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableAdminProvider.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
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
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/providers")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProviders(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be between 1 and 100.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                                 @Parameter(name = "size", in = QUERY,
                                         description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                                     @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        return Response.ok().entity(providerService.adminPagination(page - 1, size, uriInfo)).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Update the status of a Provider.",
            description = "Updates the status of a Provider with the provided status. Only the admin users can access this endpoint.")
    @APIResponse(
            responseCode = "200",
            description = "The status of a Provider was successfully updated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = AdminProviderDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid request payload.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "403",
            description = "Not permitted.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Validation request not found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @PUT
    @Path("/providers/{id}/update-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTheStatusOfProvider(@Parameter(
            description = "The Provider to be updated.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER))
                                                @PathParam("id") @Valid @NotFoundEntity(repository = ProviderRepository.class, message = "There is no Provider with the following id :") Long id,
                                                @Valid @NotNull(message = "The request body is empty.") UpdateProviderStatus updateProviderStatus) {

        var response  = providerService.updateProviderStatus(id, ProviderStatus.valueOf(updateProviderStatus.status));

        return Response.ok().entity(response).build();
    }

    public static class PageableAdminProvider extends PageResource<AdminProviderDto> {

        private List<AdminProviderDto> content;

        @Override
        public List<AdminProviderDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<AdminProviderDto> content) {
            this.content = content;
        }
    }
}
