package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
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
            implementation = String.class,
            description = "The set of resolution mode endpoints.",
            example = "[\"https://orcid.org/%s\", \"https://demo.orcid.org/%s\"]"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String[] endpoints;
}
