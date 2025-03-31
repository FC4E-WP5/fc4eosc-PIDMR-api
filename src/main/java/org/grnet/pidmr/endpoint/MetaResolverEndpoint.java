package org.grnet.pidmr.endpoint;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.LocationDto;
import org.grnet.pidmr.dto.PidResolutionBatchRequest;
import org.grnet.pidmr.dto.PidResolutionBatchResponse;
import org.grnet.pidmr.service.MetaresolverService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v1/metaresolvers")
public class MetaResolverEndpoint {

    @Inject
    MetaresolverService metaresolverService;

    @ConfigProperty(name = "api.pidmr.max.resolution.pid.list.size")
    int maxPidListSize;

    public MetaResolverEndpoint(MetaresolverService metaresolverService) {
        this.metaresolverService = metaresolverService;
    }

    @Tag(name = "Metaresolver")
    @Operation(
            summary = "Resolves different types of PIDs.",
            description = "By default, this operation returns a JSON response containing the URL resolving the PID. " +
                    "You can immediately resolve the PID by using the 'redirect' parameter since the API redirects you to the resolving page. If the pid contains the ampersand (&) character, please replace it with %26.")
    @APIResponse(
            responseCode = "200",
            description = "The Metaresolver location that resolves the PID.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = LocationDto.class)))
    @APIResponse(
            responseCode = "302",
            description = "The Metaresolver location that resolves the PID.",
            headers = {@Header(name = "location", description = "The Metaresolver location that resolves the PID.", schema = @Schema(
                    type = SchemaType.STRING,
                    example = "http://hdl.handle.net/21.T11999/METARESOLVER@ark:/13030/tf5p30086k",
                    implementation = String.class))})
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
    @GET
    @Path("/resolve")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resolve(@Parameter(name = "pid", in = QUERY, required = true, example = "ark:/13030/tf5p30086k", allowReserved = true,
            description = "The PID to be resolved.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("pid") @NotEmpty(message = "pid may not be empty.") String pid, @Parameter(name = "pidMode", in = QUERY,
            description = "The display mode of PID.", examples = {@ExampleObject(name = "Landing Page", value = "landingpage"), @ExampleObject(name = "Metadata", value = "metadata"), @ExampleObject(name = "Resource", value = "resource")}, schema = @Schema(type = SchemaType.STRING, defaultValue = "landingpage")) @DefaultValue("landingpage") @QueryParam("pidMode") String pidMode, @Parameter(name = "redirect", in = QUERY, example = "false",
                                    description = "Redirects the request to the URL resolving the PID.", schema = @Schema(type = SchemaType.BOOLEAN, defaultValue = "false")) @DefaultValue("false") @QueryParam("redirect") boolean redirect) {

        var result = java.net.URLDecoder.decode(pid.trim(), StandardCharsets.UTF_8);

        var resolvable = metaresolverService.resolve(result, pidMode);

        if(redirect){

            return Response.status(Response.Status.FOUND).header("location", URI.create(resolvable).toString()).build();
        } else {

            var location = new LocationDto();
            location.url = resolvable;

            return Response.status(Response.Status.OK).entity(location).build();
        }
    }

    @Tag(name = "Metaresolver")
    @Operation(
            summary = "Resolve multiple PIDs.",
            description = "Resolve multiple PIDs with specified modes. The mode property is only included for PIDs that require modes other than the default landing page mode. If the pid contains the ampersand (&) character, please replace it with %26.")
    @APIResponse(
            responseCode = "200",
            description = "A batch response containing multiple PID resolution results.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PidResolutionBatchResponse.class)))
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
    @Path("/resolve/batch")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resolveBatchPid(@Valid @NotNull(message = "The request body is empty.") PidResolutionBatchRequest pidResolutionBatchRequest) throws InterruptedException {

        if(pidResolutionBatchRequest.data.size() > maxPidListSize){

            throw new NotAcceptableException(String.format("It looks like you've exceeded the limit on the number of process identifiers (PIDs) you can request for resolution in a single query. Our system currently allows a maximum of %s PIDs per request", maxPidListSize));
        }

        var response = metaresolverService.asynchronousResolution(pidResolutionBatchRequest.data);

        return Response.status(Response.Status.OK).entity(response).build();
    }
}