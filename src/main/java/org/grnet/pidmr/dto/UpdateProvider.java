package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

public class UpdateProvider {

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
            description = "The regexes supported by Provider.",
            example = "[\"^(a|A)(r|R)(k|K):(?:/d{5,9})+/[a-zA-Zd]+(-[a-zA-Zd]+)*$.\"]"
    )
    @JsonProperty("regexes")
    public Set<String> regexes = new HashSet<>();

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "A PID example.",
            example = "[\"ark:/13030/tf5p30086k\", \"ark:/12148/btv1b8449691v\", \"ark:/53355/cl010066723\"]"
    )
    @JsonProperty("examples")
    public String[] examples;

    @Schema(
            type = SchemaType.BOOLEAN,
            implementation = Boolean.class,
            description = "Indicate whether a provider relies on DOIs.",
            example = "true",
            defaultValue = "false"
    )
    @JsonProperty("relies_on_dois")
    public boolean reliesOnDois;
}
