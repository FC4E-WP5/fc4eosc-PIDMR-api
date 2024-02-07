package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="PidResolutionResponse", description="Response object containing information about the resolution of a PID.")
public class PidResolutionResponse {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The PID that was resolved.",
            example = "ark:/13030/tf5p30086k"
    )
    public String pid;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The mode of the resolved PID, if applicable.",
            example = "metadata"
    )
    public String mode;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The resolved URL associated with the PID.",
            example = "https://oac.cdlib.org/ark:/13030/tf5p30086k/"
    )
    public String url;

    @Schema(
            type = SchemaType.BOOLEAN,
            implementation = Boolean.class,
            description = "Indicates whether the resolution was successful (true) or not (false).",
            example = "false"
    )
    public Boolean success;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Descriptive message in case of failure, providing more information about the reason for failure.",
            example = "Descriptive message in case of failure."
    )
    public String message;
}
