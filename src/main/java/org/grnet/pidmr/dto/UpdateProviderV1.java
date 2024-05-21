package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="UpdateProviderV1", description="An object represents a request for updating a Provider.")
public class UpdateProviderV1 extends UpdateProvider {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The resolution modes supported by Provider.",
            example = "[\"resource\", \"metadata\"]"
    )
    @JsonProperty("resolution_modes")
    public Set<String> actions = new HashSet<>();
}
