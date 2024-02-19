package org.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(name="PidIdentificationBatchResponse", description="A batch response containing multiple PID identification results.")
public class PidIdentificationBatchResponse {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = PidResolutionResponse.class,
            description = "Each item in the list corresponds to the result of a single PID identification request in the batch."
    )
    public Map<String, Identification> data = new HashMap<>();
}
