package org.grnet.pidmr.endpoint;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.pidmr.dto.Identification;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.PidIdentificationBatchRequest;
import org.grnet.pidmr.dto.PidMultipleIdentificationBatchResponse;
import org.grnet.pidmr.service.DatabaseProviderService;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v2/providers")
public class ProviderV2Endpoint {

    @Inject
    DatabaseProviderService providerService;

    @ConfigProperty(name = "api.pidmr.max.identification.pid.list.size")
    int maxPidListSize;

    @Tag(name = "Provider")
    @Operation(
            summary = "This endpoint identifies PIDs.",
            description = "This endpoint identifies PIDs from the provided text.")
    @APIResponse(
            responseCode = "200",
            description = "The result of text identification.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
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

        var identification = providerService.multipleIdentification(text.trim());

        return Response.ok().entity(identification).build();
    }

    @Tag(name = "Provider")
    @Operation(
            summary = "Identify multiple PIDs.",
            description = "Identify multiple PIDs.")
    @APIResponse(
            responseCode = "200",
            description = "A batch response containing multiple PID identification results.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = PidMultipleIdentificationBatchResponse.class)))
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

        var response = new PidMultipleIdentificationBatchResponse();

        pidIdentificationBatchRequest.data.forEach(entry->{

            var identification = providerService.multipleIdentification(entry.trim());

            response.data.put(entry, identification);

        });

        return Response.status(Response.Status.OK).entity(response).build();
    }
}
