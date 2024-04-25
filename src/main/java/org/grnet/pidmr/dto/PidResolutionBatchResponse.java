package org.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(name="PidResolutionBatchResponse", description="A batch response containing multiple PID resolution results.")
public class PidResolutionBatchResponse {

    @Schema(
            type = SchemaType.OBJECT,
            additionalProperties = List.class,
            description = "Each item in the list corresponds to the result of a single PID resolution request in the batch."
    )
    public Map<String, List<PidResolutionResponse>> data = new HashMap<>();
}
