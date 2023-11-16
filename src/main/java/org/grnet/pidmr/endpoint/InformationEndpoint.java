package org.grnet.pidmr.endpoint;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.pidmr.dto.InformativeResponse;
import org.grnet.pidmr.dto.VersionDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1")
public class InformationEndpoint {

    @ConfigProperty(name = "version")
    String version;

    @Tag(name = "Information")
    @Operation(
            summary = "Returns the version of the PID Metaresolver API.",
            description = "Returns the version of the PID Metaresolver API.")
    @APIResponse(
            responseCode = "200",
            description = "The PID Metaresolver API version.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = VersionDto.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(
                    type = SchemaType.OBJECT,
                    implementation = InformativeResponse.class)))
    @GET
    @Path("/version")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response version() {

        var versionDto = new VersionDto();
        versionDto.version = version;

        return Response.status(Response.Status.OK).entity(versionDto).build();
    }
}
