package gr.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Provider", description="An object represents a Provider.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Provider {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Provider pid.",
            example = "ark"
    )
    @JsonProperty("pid")
    @EqualsAndHashCode.Include
    public String pid;

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
            example = "Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects"
    )
    @JsonProperty("description")
    public String description;
}
