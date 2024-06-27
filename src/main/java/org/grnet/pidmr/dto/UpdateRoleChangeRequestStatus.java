package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.constraints.StringEnumeration;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.enums.RoleChangeRequestStatus;


@Schema(name="UpdateRoleChangeRequestStatus", description="Request to update the Role Change Request status.")
public class UpdateRoleChangeRequestStatus {

    @Schema(
            type = SchemaType.STRING,
            implementation = RoleChangeRequestStatus.class,
            required = true,
            description = "The Role Change Request status (e.g. APPROVED, PENDING, REJECTED).",
            example = "APPROVED"
    )
    @JsonProperty("status")
    @StringEnumeration(enumClass = RoleChangeRequestStatus.class, message = "status")
    @NotEmpty(message = "status may not be empty.")
    public String status;
}
