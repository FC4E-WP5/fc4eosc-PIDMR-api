package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="UpdateProviderV2", description="An object represents a request for updating a Provider.")
public class UpdateProviderV2 extends UpdateProvider {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ResolutionModeRequest.class,
            description = "The resolution modes supported by Provider."
    )
    @JsonProperty("resolution_modes")
    public Set<@Valid ResolutionModeRequest> actions = new HashSet<>();
}
