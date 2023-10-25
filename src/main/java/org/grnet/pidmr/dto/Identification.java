package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="Identification", description="An object representing the result of text identification.")
public class Identification {

    @Schema(
            type = SchemaType.STRING,
            implementation = Identification.Status.class,
            description = "Indicates the identification status (VALID, INVALID, INCOMPLETE).",
            example = "VALID"
    )
    @JsonProperty("status")
    public Status status;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Indicates the possible type of PID (e.g., ark, arXiv, etc.).",
            example = "ark"
    )
    public String type;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Provides an example of a valid PID.",
            example = "ark:/13030/tf5p30086k"
    )
    public String example;

   public enum Status { VALID, INVALID, INCOMPLETE }
}

