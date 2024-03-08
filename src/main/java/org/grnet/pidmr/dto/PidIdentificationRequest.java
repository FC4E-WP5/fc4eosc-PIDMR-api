package org.grnet.pidmr.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="PidIdentificationRequest", description="Request object for PID identification.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PidIdentificationRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Text to be identified.",
            example = "ark:"
    )
    @NotEmpty(message = "text may not be empty.")
    @EqualsAndHashCode.Include
    public String text;
}
