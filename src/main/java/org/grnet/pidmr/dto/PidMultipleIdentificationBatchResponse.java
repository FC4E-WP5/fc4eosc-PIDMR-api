package org.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Schema(name="PidMultipleIdentificationBatchResponse", description="A batch response containing multiple PID identification results.")
public class PidMultipleIdentificationBatchResponse {

    @Schema(
            type = SchemaType.OBJECT,
            additionalProperties = Set.class,
            description = "Each item in the list corresponds to the result of a single PID identification request in the batch."
    )
    public Map<String, Set<Identification>> data = new HashMap<>();
}
