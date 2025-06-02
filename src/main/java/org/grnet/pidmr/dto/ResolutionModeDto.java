package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.entity.database.Endpoint;

import java.util.Set;

@Schema(name="ResolutionMode", description="An object represents a resolution mode.")
public class ResolutionModeDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The resolution mode.",
            example = "landingpage"
    )
    public String mode;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of resolution mode.",
            example = "Landing Page"
    )
    public String name;
    @Schema(
            type = SchemaType.ARRAY,
            implementation = Endpoint.class,
            description = "The set of resolution mode endpoints."
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Set<Endpoint> endpoints;
}
