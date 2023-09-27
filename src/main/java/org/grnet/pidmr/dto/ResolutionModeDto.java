package org.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
}
