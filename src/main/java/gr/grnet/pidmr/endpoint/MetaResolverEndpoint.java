package gr.grnet.pidmr.endpoint;

import gr.grnet.pidmr.dto.InformativeResponse;
import gr.grnet.pidmr.service.MetaresolverService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.QUERY;

@Path("v1/metaresolvers")
public class MetaResolverEndpoint {

    @Inject
    MetaresolverService metaresolverService;

    public MetaResolverEndpoint(MetaresolverService metaresolverService) {
        this.metaresolverService = metaresolverService;
    }

    @Tag(name = "Metaresolver")
    @Operation(
            summary = "Resolves different types of PIDs.",
            description = "This operation can be used to resolve the different types of PIDs. Using a Metaresolver, it resolves the incoming PID. " +
                    "The 301 redirect status response code" +
                    " indicates the Metaresolver URL, which resolves the PID. The Location header contains that URL.")
    @APIResponse(
            responseCode = "302",
            description = "The Metaresolver location that resolves the PID.",
            headers = @Header(name = "Location", description = "The Metaresolver location that resolves the PID.", schema = @Schema(
                    type = SchemaType.STRING,
                    example = "http://hdl.handle.net/21.T11999/METARESOLVER@ark:/13030/tf5p30086k",
                    implementation = String.class)))
    @APIResponse(
            responseCode = "406",
            description = "The pid is not supported.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/resolve/{pid : .+}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resolve(@Parameter(
            description = "The PID to be resolved.",
            required = true,
            example = "ark:/13030/tf5p30086k",
            schema = @Schema(type = SchemaType.STRING))
                                @PathParam("pid") String pid,  @Parameter(name = "mode", in = QUERY,
            description = "When this parameter is used, the API also appends to PID the display mode.", schema = @Schema(type = SchemaType.STRING)) @DefaultValue("") @QueryParam("mode") String mode) {

        var resolvable = metaresolverService.resolve(pid, mode);

        return Response.status(Response.Status.FOUND).header("Location", URI.create(resolvable).toString()).build();
    }
}