package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Set;

@Schema(name="ResolutionModeRequest", description="Request to create a resolution mode.")
public class ResolutionModeRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The resolution mode.",
            example = "landingpage"
    )
    @NotEmpty(message = "mode may not be empty.")
    public String mode;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The resolution mode set of endpoints.",
            example = "[\"https://orcid.org/%s\", \"https://demo.orcid.org/%s\"]"
    )
    @NotEmpty(message = "endpoints may not be empty.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String[] endpoints;
}
