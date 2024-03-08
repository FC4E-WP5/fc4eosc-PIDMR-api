package org.grnet.pidmr.dto;

import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="PidResolutionRequest", description="Request object for resolving PIDs with optional modes.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PidResolutionRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The PID to resolve.",
            example = "ark:/13030/tf5p30086k"
    )
    @EqualsAndHashCode.Include
    public String pid;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The mode of the PID to resolve.",
            example = "metadata"
    )
    @EqualsAndHashCode.Include
    public String mode;
}
