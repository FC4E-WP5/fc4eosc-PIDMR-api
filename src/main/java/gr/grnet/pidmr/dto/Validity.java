package gr.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Validity", description="An object representing whether a PID is valid.")
public class Validity {

    @Schema(
            type = SchemaType.BOOLEAN,
            implementation = Boolean.class,
            description = "Whether the PID is valid or not.",
            example = "true"
    )
    @JsonProperty("valid")
    public boolean valid;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The type of PID.",
            example = "ark"
    )
    public String type;
}
