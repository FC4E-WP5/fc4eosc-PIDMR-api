package org.grnet.pidmr.endpoint;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
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
import org.grnet.pidmr.dto.DenyAccess;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.PermitAccess;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.ProviderRequestV1;
import org.grnet.pidmr.dto.RoleAssignmentRequest;
import org.grnet.pidmr.dto.RoleChangeRequestDto;
import org.grnet.pidmr.dto.UpdateProviderV1;
import org.grnet.pidmr.dto.UpdateProviderStatus;
import org.grnet.pidmr.dto.UpdateRoleChangeRequestStatus;
import org.grnet.pidmr.dto.UserProfileDto;
import org.grnet.pidmr.dto.ValidatorResponse;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.exception.ConflictException;
import org.grnet.pidmr.pagination.PageResource;
import org.grnet.pidmr.repository.ProviderRepository;
import org.grnet.pidmr.repository.RoleChangeRequestsRepository;
import org.grnet.pidmr.service.AdminService;
import org.grnet.pidmr.service.DatabaseProviderService;
import org.grnet.pidmr.service.UserService;
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

    @ConfigProperty(name = "api.server.url")
    String serverUrl;

    @Inject
    DatabaseProviderService providerService;

    @Inject
    UserService userService;

    @Inject
    AdminService adminService;


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
    public Response create(@Valid @NotNull(message = "The request body is empty.") ProviderRequestV1 request, @Context UriInfo uriInfo) {

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
                    implementation = AdminProviderDto.class)))
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

        if (deleted) {

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
                           @Valid @NotNull(message = "The request body is empty.") UpdateProviderV1 request) {

        ProviderDto response = null;
        try {
            response = providerService.update(id, request);

        } catch (ArcUndeclaredThrowableException e) {

            if (!Objects.isNull(e.getCause()) && !Objects.isNull(e.getCause().getCause()) && !Objects.isNull(e.getCause().getCause().getCause()) && e.getCause().getCause().getCause() instanceof ConstraintViolationException) {

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
                                              @Valid @NotNull(message = "The request body is empty.") UpdateProviderStatus request) {

        var response = providerService.updateProviderStatus(id, request);

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "List role change request",
            description = "Gets a list of users that made a role change request. Only the admin users can access this endpoint.")
    @APIResponse(
            responseCode = "200",
            description = "Users role change requests list was successfully uploaded.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableRoleChangeRequest.class)))
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
    @GET
    @Path("/users/role-change-requests")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getRoleChangeRequestsByPage(
            @Parameter(name = "page", in = QUERY,
                    description = "Indicates the page number. Page number must be between 1 and 100.")
            @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.")
            @QueryParam("page") int page,
            @Parameter(name = "size", in = QUERY,
                    description = "The page size.")
            @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
            @Max(value = 100, message = "Page size must be between 1 and 100.")
            @QueryParam("size") int size, @Context UriInfo uriInfo) {

        var roleChangeRequests = adminService.getRoleChangeRequestsByPage(page - 1, size, uriInfo);

        return Response.ok(roleChangeRequests).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Update a role change request.",
            description = "Update the status of a role change request.")
    @APIResponse(
            responseCode = "200",
            description = "Role change request updated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
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
            description = "Role change request not found.",
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
    @Path("/users/role-change-requests/{id}/update-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoleChangeRequestStatus(@Parameter(
            description = "The ID of the role change request.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER))
                                                  @PathParam("id") @Valid @NotFoundEntity(repository = RoleChangeRequestsRepository.class, message = "There is no Role Change Request with the following id :") Long id,
                                                  @Valid @NotNull(message = "The request body is empty.") UpdateRoleChangeRequestStatus updateRoleChangeRequestStatus) {

        userService.updateRoleChangeRequest(id, updateRoleChangeRequestStatus);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "Role change request updated.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Restrict a user's access.",
            description = "Calling this endpoint results in the specified user being denied access to the PIDMR API.")
    @APIResponse(
            responseCode = "200",
            description = "Successful operation.",
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
            description = "Not found.",
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
    @Path("/users/deny-access")
    @Produces(MediaType.APPLICATION_JSON)
    public Response denyAccess(@Valid @NotNull(message = "The request body is empty.") DenyAccess denyAccess) {

        userService.doesUserExist(denyAccess.userId);
        userService.addDenyAccessRole(denyAccess.userId, denyAccess.reason);

        var informativeResponse = new InformativeResponse();
        informativeResponse.code = 200;
        informativeResponse.message = "deny_access role added successfully to the user. The user is now denied access to the API.";

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Allow Access to previously banned user.",
            description = "Executing this endpoint allows a user who has been previously banned to access the PIDMR Service again.")
    @APIResponse(
            responseCode = "200",
            description = "Successful operation.",
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
            description = "Not found.",
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
    @Path("/users/permit-access")
    @Produces(MediaType.APPLICATION_JSON)
    public Response permitAccess(@Valid @NotNull(message = "The request body is empty.") PermitAccess permitAccess) {

        userService.doesUserExist(permitAccess.userId);
        userService.removeDenyAccessRole(permitAccess.userId, permitAccess.reason);

        var informativeResponse = new InformativeResponse();
        informativeResponse.code = 200;
        informativeResponse.message = "deny_access role removed successfully from the user. The user is now allowed access to the API.";

        return Response.ok().entity(informativeResponse).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Assign new roles to a user.",
            description = "Assigns new roles to a specific user in the PIDMR service.")
    @APIResponse(
            responseCode = "200",
            description = "Successful operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
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
            description = "Entity Not Found.",
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
    @Path("/users/assign-roles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignRolesToUser(@Valid @NotNull(message = "The request body is empty.") RoleAssignmentRequest request) {

        userService.doesUserExist(request.userId);
        userService.assignRolesToUser(request.userId, request.roles);

        var response = new InformativeResponse();
        response.code = 200;
        response.message = "Roles have been successfully assigned to user.";

        return Response.ok().entity(response).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Retrieve a list of available users.",
            description = "This endpoint returns a list of PIDMR users.")
    @APIResponse(
            responseCode = "200",
            description = "List of Users.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableUserProfile.class)))
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
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response usersByPage(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                                @Parameter(name = "size", in = QUERY,
                                        description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                                @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                                @Context UriInfo uriInfo) {


        var userProfile = userService.getUsersByPage(page - 1, size, uriInfo);

        return Response.ok().entity(userProfile).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Get a role change request.",
            description = "Get a role change request.")
    @APIResponse(
            responseCode = "200",
            description = "Role change request retrieved.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = RoleChangeRequestDto.class)))
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
            description = "Role change request not found.",
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
    @Path("/users/role-change-requests/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleChangeRequestById(@Parameter(
            description = "The ID of the role change request.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.NUMBER))
                                             @PathParam("id") @Valid @NotFoundEntity(repository = RoleChangeRequestsRepository.class, message = "There is no Role Change Request with the following id :") Long id) {

        var response = userService.retrieveRoleChangeRequest(id);
        return Response.ok().entity(response).build();
    }

    @Tag(name = "Admin")
    @Operation(
            summary = "Get all PID Validators.",
            description = "Returns a list of all available PID validators, including their names and descriptions.")
    @APIResponse(
            responseCode = "200",
            description = "List of PID validators.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PageableValidators.class)))
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
    @Path("/validators")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getValidators(@Parameter(name = "page", in = QUERY,
            description = "Indicates the page number. Page number must be >= 1.") @DefaultValue("1") @Min(value = 1, message = "Page number must be >= 1.") @QueryParam("page") int page,
                                  @Parameter(name = "size", in = QUERY,
                                          description = "The page size.") @DefaultValue("10") @Min(value = 1, message = "Page size must be between 1 and 100.")
                                      @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size,
                                  @Context UriInfo uriInfo) {

        var response = adminService.getValidatorsByPage(page-1, size, uriInfo);

        return Response.ok().entity(response).build();
    }

    public static class PageableValidators extends PageResource<ValidatorResponse> {

        private List<ValidatorResponse> content;

        @Override
        public List<ValidatorResponse> getContent() {
            return content;
        }

        @Override
        public void setContent(List<ValidatorResponse> content) {
            this.content = content;
        }
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

    public static class PageableRoleChangeRequest extends PageResource<RoleChangeRequestDto> {

        private List<RoleChangeRequestDto> content;

        @Override
        public List<RoleChangeRequestDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<RoleChangeRequestDto> content) {
            this.content = content;
        }
    }

    public static class PageableUserProfile extends PageResource<UserProfileDto> {

        private List<UserProfileDto> content;

        @Override
        public List<UserProfileDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<UserProfileDto> content) {
            this.content = content;
        }
    }
}