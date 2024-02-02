package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.constraints.StringEnumeration;
import org.grnet.pidmr.enums.ProviderStatus;


@Schema(name="UpdateProviderStatus", description="Request to update the Provider status.")
public class UpdateProviderStatus {

    @Schema(
            type = SchemaType.STRING,
            implementation = ProviderStatus.class,
            required = true,
            description = "The Provider status (e.g. APPROVED, PENDING).",
            example = "APPROVED"
    )
    @JsonProperty("status")
    @StringEnumeration(enumClass = ProviderStatus.class, message = "status")
    @NotEmpty(message = "status may not be empty.")
    public String status;
}
