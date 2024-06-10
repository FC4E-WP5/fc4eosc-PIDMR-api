package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="Provider", description="An object represents a Provider.")
public class ProviderDto {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Provider id.",
            example = "5"
    )
    @JsonProperty("id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long id;

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
            implementation = ResolutionModeDto.class,
            description = "The resolution modes supported by Provider."
    )
    @JsonProperty("resolution_modes")
    public Set<ResolutionModeDto> actions = new HashSet<>();

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

    @Schema(
            type = SchemaType.BOOLEAN,
            implementation = Boolean.class,
            description = "Indicate whether a provider relies on DOIs.",
            example = "false"
    )
    @JsonProperty("relies_on_dois")
    public boolean reliesOnDois;
}
