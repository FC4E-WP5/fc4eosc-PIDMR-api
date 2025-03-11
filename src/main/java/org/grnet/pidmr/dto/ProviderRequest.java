package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.constraints.StringEnumeration;
import org.grnet.pidmr.enums.Validator;

import java.util.Set;

public class ProviderRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Type of PID.",
            example = "ark"
    )
    @JsonProperty("type")
    @NotEmpty(message = "type may not be empty.")
    public String type;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Provider name.",
            example = "ARK alliance"
    )
    @JsonProperty("name")
    @NotEmpty(message = "name may not be empty.")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short Provider description.",
            example = "Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects."
    )
    @JsonProperty("description")
    @NotEmpty(message = "description may not be empty.")
    public String description;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "List of regular expressions that validate all the PIDs (Product Identifiers) associated with this Provider.",
            example = "[\"^(a|A)(r|R)(k|K):(?:/d{5,9})+/[a-zA-Zd]+(-[a-zA-Zd]+)*$.\"]"
    )
    @JsonProperty("regexes")
    @NotEmpty(message = "regexes should have at least one entry.")
    public Set<String> regexes;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "A PID examples.",
            example = "[\"ark:/13030/tf5p30086k\", \"ark:/12148/btv1b8449691v\", \"ark:/53355/cl010066723\"]"
    )
    @JsonProperty("examples")
    @NotEmpty(message = "examples should have at least one entry.")
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

    @Schema(
            type = SchemaType.STRING,
            implementation = Validator.class,
            description = "The Validator (e.g. NONE, ISBN).",
            example = "ISBN"
    )
    @JsonProperty("validator")
    @StringEnumeration(enumClass = Validator.class, message = "validator")
    public String validator = "NONE";
}
