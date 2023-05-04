package gr.grnet.pidmr.endpoint;

import gr.grnet.pidmr.dto.InformativeResponse;
import gr.grnet.pidmr.service.MetaresolverService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.mapstruct.ap.internal.util.Strings;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
            responseCode = "200",
            description = "The Metaresolver location that resolves the PID.",
            headers = {@Header(name = "location", description = "The Metaresolver location that resolves the PID.", schema = @Schema(
                    type = SchemaType.STRING,
                    example = "http://hdl.handle.net/21.T11999/METARESOLVER@ark:/13030/tf5p30086k",
                    implementation = String.class)),
                    @Header(name = "http-method", description = "The http method resolving the PID.", schema = @Schema(
                            type = SchemaType.STRING,
                            example = "GET",
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
    @GET
    @Path("/resolve")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resolve(@Parameter(name = "pid", in = QUERY, required = true, example = "ark:/13030/tf5p30086k", allowReserved = true,
            description = "The PID to be resolved.", schema = @Schema(type = SchemaType.STRING)) @QueryParam("pid") @NotEmpty(message = "pid may not be empty.") String pid, @Parameter(name = "pidMode", in = QUERY,
            description = "When this parameter is used, the API also appends to PID the display mode.", examples = {@ExampleObject(name = "Landing Page", value = "landingpage"), @ExampleObject(name = "Metadata", value = "metadata"), @ExampleObject(name = "Resource", value = "resource")}, schema = @Schema(type = SchemaType.STRING)) @DefaultValue("") @QueryParam("pidMode") String pidMode) {

        var resolvable = metaresolverService.resolve(pid, pidMode);

        if(Strings.isEmpty(pidMode)){
            return Response.status(Response.Status.OK).header("location", URI.create(resolvable).toString()).header("http-method", "GET").build();
        }else {
            return Response.status(Response.Status.OK).header("location", URI.create(resolvable).toString()).header("http-method", "POST").build();
        }
    }
}