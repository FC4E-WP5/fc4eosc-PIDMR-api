package org.grnet.pidmr.endpoint;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
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
import org.grnet.pidmr.dto.*;
import org.grnet.pidmr.service.UserService;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;


@Path("/v1/users")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class UserEndpoint {

    @Inject
    UserService userService;

    @Tag(name = "User")
    @Operation(
            summary = "Get User Profile.",
            description = "This endpoint retrieves the user profile information.")
    @APIResponse(
            responseCode = "200",
            description = "User's Profile.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = UserProfileDto.class)))
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
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @SecurityRequirement(name = "Authentication")
    @Path("/profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response profile() {

        var dto = userService.getUserProfile();

        return Response.ok().entity(dto).build();
    }

    /*
     * Informing about the API responses
     */
    @Tag(name = "User")
    @Operation(
            summary         = "User requests a role change.",
            description     = "Endpoint for requesting a new role.")
    @APIResponse(
            responseCode    = "200",
            description     = "The User has been successfully made the request.",
            content         = @Content(schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode    = "400",
            description     = "Invalid request payload.",
            content         = @Content(schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = InformativeResponse.class)))

    @APIResponse(
            responseCode    = "401",
            description     = "User has not been authenticated.",
            content         = @Content(schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = InformativeResponse.class)))

    @APIResponse(
            responseCode    = "404",
            description     = "Role not found.",
            content         = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @APIResponse(
            responseCode    = "500",
            description     = "Internal Server Error.",
            content         = @Content(schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = InformativeResponse.class)))

    @SecurityRequirement(name = "Authentication")
    @Path("/role-change-request")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response roleChangeRequest(@Valid @NotNull(message = "The request body is empty.") UserRoleChangeRequest userRoleChangeRequest) {

        userService.persistRoleChangeRequest(userRoleChangeRequest);

        var response = new InformativeResponse();

        response.code = 200;
        response.message = "We have received your request and are processing it.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "User")
    @Operation(
            summary         = "Retrieve all role change requests by a user.",
            description     = "Endpoint to retrieve all role change requests created by a user.")
    @APIResponse(
            responseCode    = "200",
            description     = "A list of role change requests.",
            content         = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = AdminEndpoint.PageableRoleChangeRequest.class)))

    @APIResponse(
            responseCode    = "401",
            description     = "User has not been authenticated.",
            content         = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @APIResponse(
            responseCode    = "500",
            description     = "Internal Server Error.",
            content         = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))

    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/role-change-request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleChangeRequests( @Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be between 1 and 100.")
                                               @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.")
                                               @QueryParam("page") int page,
                                           @Parameter(name = "size", in = QUERY,
                                                   description = "The page size.")
                                               @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                                               @Max(value = 100, message = "Page size must be between 1 and 100.")
                                               @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var roleChangeRequests = userService.getRoleChangeRequestsByUser(page-1, size, uriInfo);

        return Response.ok(roleChangeRequests).build();
    }
}