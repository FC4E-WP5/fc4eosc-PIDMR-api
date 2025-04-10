package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="Identification", description="An object representing the result of text identification.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Identification {

    @Schema(
            type = SchemaType.STRING,
            implementation = Identification.Status.class,
            description = "Indicates the identification status (VALID, INVALID, AMBIGUOUS).",
            example = "VALID"
    )
    @JsonProperty("status")
    @EqualsAndHashCode.Include
    public Status status;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Indicates the possible type of PID (e.g., ark, arXiv, etc.).",
            example = "ark"
    )
    @EqualsAndHashCode.Include
    public String type;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "Provides examples of a valid PID.",
            example = "[\"ark:/13030/tf5p30086k\", \"ark:/12148/btv1b8449691v\", \"ark:/53355/cl010066723\"]"
    )
    public String[] examples;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ResolutionModeDto.class,
            description = "The resolution modes supported by Provider."
    )
    @JsonProperty("resolution_modes")
    public Set<ResolutionModeDto> actions = new HashSet<>();

    public enum Status { VALID, INVALID, AMBIGUOUS }
}

