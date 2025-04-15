package org.grnet.pidmr.endpoint;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.grnet.pidmr.dto.*;
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
import org.grnet.pidmr.validator.constraints.NotFoundEntity;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v1/providers")
public class ProviderEndpoint {

    @Inject
    DatabaseProviderService providerService;

    @ConfigProperty(name = "api.pidmr.max.identification.pid.list.size")
    int maxPidListSize;

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
                           @Max(value = 100, message = "Page size must be between 1 and 100.") @QueryParam("size") int size, @Context UriInfo uriInfo) {

        return Response.ok().entity(providerService.pagination(page - 1, size, uriInfo)).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Get Provider by ID.",
            description = "Endpoint for retrieving a Provider by its ID.")
    @APIResponse(
            responseCode = "200",
            description = "The requested Provider.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = ProviderDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProviderById(
            @Parameter(
                    description = "The ID of the Provider to retrieve.",
                    required = true,
                    example = "1",
                    schema = @Schema(type = SchemaType.NUMBER))
            @PathParam("id")
            @Valid @NotFoundEntity(repository = ProviderRepository.class, message = "There is no Provider with the following id:") Long id) {

        var response = providerService.getById(id);

        return Response.ok().entity(response).build();
    }


    @Tag(name = "Provider")
    @Operation(
            summary = "Validates PIDs.",
            description = "This operation check the validity of each identifier. " +
                    "Every Provider has a regex based on which the validation is performed. If the pid contains the ampersand (&) character, please replace it with %26.")
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
            description = "When this parameter is used, the API does not search the list of available Providers but directly retrieves the Provider of this type.", schema = @Schema(type = SchemaType.STRING)) @DefaultValue("") @QueryParam("type") String type) throws UnsupportedEncodingException {

        var result = java.net.URLDecoder.decode(pid.trim(), StandardCharsets.UTF_8);

        var validity = providerService.validation(result, type);

        return Response.ok().entity(validity).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "This endpoint identifies PIDs.",
            description = "This endpoint identifies PIDs from the provided text. If the pid contains the ampersand (&) character, please replace it with %26.")
    @APIResponse(
            responseCode = "200",
            description = "The result of text identification.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = Identification.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/identify")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response identify(@Parameter(name = "text", in = QUERY, required = true, example = "ark:/", allowReserved = true,
            description = "Text to be checked for PID.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("text") @NotEmpty(message = "text may not be empty.") String text) {

        var result = java.net.URLDecoder.decode(text.trim(), StandardCharsets.UTF_8);

        var identification = providerService.identify(result);

        return Response.ok().entity(identification).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Identify multiple PIDs.",
            description = "Identify multiple PIDs. If the pid contains the ampersand (&) character, please replace it with %26.")
    @APIResponse(
            responseCode = "200",
            description = "A batch response containing multiple PID identification results.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PidIdentificationBatchResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "406",
            description = "The pid is not supported.",
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
    @Path("/identify/batch")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response identifyBatchPid(@Valid @NotNull(message = "The request body is empty.") PidIdentificationBatchRequest pidIdentificationBatchRequest){

        if(pidIdentificationBatchRequest.data.size() > maxPidListSize){

            throw new NotAcceptableException(String.format("It looks like you've exceeded the limit on the number of process identifiers (PIDs) you can request for identification in a single query. Our system currently allows a maximum of %s PIDs per request", maxPidListSize));
        }

        var response = new PidIdentificationBatchResponse();

        pidIdentificationBatchRequest.data.forEach(entry->{

            var result = java.net.URLDecoder.decode(entry.trim(), StandardCharsets.UTF_8);

            var identification = providerService.identify(result);

            response.data.put(entry, identification);

        });

        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Returns the available resolution modes.",
            description = "Returns the available resolution modes.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Provider.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = String.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/resolution-modes")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getResolutionModes() {

        var response = providerService.getResolutionModes();

        return Response.ok().entity(response).build();
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