package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="ProviderRequestV3", description="Request to create a Provider.")
public class ProviderRequestV3 extends ProviderRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ResolutionModeRequestV2.class,
            description = "The resolution modes supported by Provider"
    )
    @JsonProperty("resolution_modes")
    @NotEmpty(message = "resolution_modes should have at least one entry.")
    public Set<@Valid ResolutionModeRequestV2> actions;
}
