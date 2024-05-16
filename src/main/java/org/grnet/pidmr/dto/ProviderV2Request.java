package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="ProviderV2Request", description="Request to create/modify a Provider.")
public class ProviderV2Request {

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
            implementation = ResolutionModeRequest.class,
            description = "The resolution modes supported by Provider"
    )
    @JsonProperty("resolution_modes")
    @NotEmpty(message = "resolution_modes should have at least one entry.")
    public Set<@Valid ResolutionModeRequest> actions;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A PID example.",
            example = "ark:/13030/tf5p30086k"
    )
    @JsonProperty("example")
    @NotEmpty(message = "example may not be empty.")
    public String example;

}
