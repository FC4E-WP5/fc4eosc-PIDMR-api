package org.grnet.pidmr.dto;

import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="PidIdentificationBatchRequest", description="A batch request containing multiple PID identification requests.")
public class PidIdentificationBatchRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "Each item in the list corresponds to a single PID identification request.",
            example = "ark:",
            minItems = 1
    )
    @NotEmpty(message = "data should have at least one entry.")
    public Set<@NotEmpty(message = "text to be identified may not be empty.")  String> data = new HashSet<>();
}
