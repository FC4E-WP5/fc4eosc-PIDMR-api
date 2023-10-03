package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="ProviderRequest", description="Request to create/modify a Provider.")
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
            description = "The resolution modes supported by Provider. The available resolution modes are : landingpage, metadata, resource.",
            example = "[\"resource\", \"metadata\"]"
    )
    @JsonProperty("resolution_modes")
    @NotEmpty(message = "resolution_modes should have at least one entry.")
    public Set<String> actions;
}
