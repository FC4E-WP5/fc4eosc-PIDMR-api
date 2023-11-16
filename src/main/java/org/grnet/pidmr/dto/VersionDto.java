package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Version", description="This object encapsulates the PID Metaresolver version.")
public class VersionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The PID Metaresolver version.",
            example = "1.1.0"
    )
    @JsonProperty("version")
    public String version;
}
