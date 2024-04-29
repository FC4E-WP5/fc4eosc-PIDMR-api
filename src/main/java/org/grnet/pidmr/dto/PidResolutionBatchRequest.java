package org.grnet.pidmr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="PidResolutionBatchRequest", description="A batch request containing multiple PID resolution requests.")
public class PidResolutionBatchRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = PidResolutionRequest.class,
            required = true,
            description = "Each item in the list corresponds to a single PID resolution request.",
            minItems = 1
    )
    @NotEmpty(message = "data should have at least one entry.")
    public Set<@Valid PidResolutionRequest> data = new HashSet<>();
}
