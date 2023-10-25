package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="UpdateProvider", description="An object represents a request for updating a Provider.")
public class UpdateProviderDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Type of PID.",
            example = "ark"
    )
    @JsonProperty("type")
    public String type;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Provider name.",
            example = "ARK alliance"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short Provider description.",
            example = "Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects."
    )
    @JsonProperty("description")
    public String description;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The resolution modes supported by Provider."
    )
    @JsonProperty("resolution_modes")
    public Set<String> actions = new HashSet<>();

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The regexes supported by Provider."
    )
    @JsonProperty("regexes")
    public Set<String> regexes = new HashSet<>();

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A PID example.",
            example = "ark:/13030/tf5p30086k"
    )
    @JsonProperty("example")
    public String example;
}
