package org.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="PidResolutionBatchResponse", description="A batch response containing multiple PID resolution results.")
public class PidResolutionBatchResponse {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = PidResolutionResponse.class,
            description = "Each item in the list corresponds to the result of a single PID resolution request in the batch."
    )
    public Set<PidResolutionResponse> data = new HashSet<>();
}
