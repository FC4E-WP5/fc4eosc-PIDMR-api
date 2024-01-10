package org.grnet.pidmr.endpoint;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import io.quarkus.security.Authenticated;
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
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequest;
import org.grnet.pidmr.dto.UpdateProviderDto;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.service.DatabaseProviderService;
import org.grnet.pidmr.util.ServiceUriInfo;
import org.grnet.pidmr.validator.constraints.NotFoundEntity;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

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
            response = providerService.update(request, id);

        } catch (ArcUndeclaredThrowableException e) {

            if(!Objects.isNull(e.getCause()) && !Objects.isNull(e.getCause().getCause()) && !Objects.isNull(e.getCause().getCause().getCause()) && e.getCause().getCause().getCause() instanceof ConstraintViolationException){

                throw new ConflictException(String.format("This Provider type {%s} exists.", request.type));
            } else {
                throw new ServerErrorException("Internal Server Error", 500);
            }
        }

        return Response.ok().entity(response).build();
    }
}
