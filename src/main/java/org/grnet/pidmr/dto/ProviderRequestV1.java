package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="ProviderRequestV1", description="Request to create/modify a Provider.")
public class ProviderRequestV1 extends ProviderRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The resolution modes supported by Provider. The available resolution modes are : landingpage, metadata, resource.",
            example = "[\"resource\", \"metadata\"]"
    )
    @JsonProperty("resolution_modes")
    @NotEmpty(message = "resolution_modes should have at least one entry.")
    public Set<String> actions;
}
