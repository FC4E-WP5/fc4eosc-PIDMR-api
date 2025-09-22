package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.entity.database.Endpoint;

import java.util.Set;

@Schema(name="ResolutionModeRequestV2", description="Request to create a resolution mode.")
public class ResolutionModeRequestV2 {

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
            implementation = Endpoint.class,
            description = "The set of resolution mode endpoints."
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Set<@Valid Endpoint> endpoints;
}
