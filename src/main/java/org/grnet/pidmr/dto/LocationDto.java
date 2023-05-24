package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Location", description="This object encapsulates the URL where the PID is resolved.")
public class LocationDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The URL where the PID is resolved.",
            example = "http://hdl.handle.net/21.T11973/MR@ark:/67531/metapth346793"
    )
    @JsonProperty("url")
    public String url;
}
