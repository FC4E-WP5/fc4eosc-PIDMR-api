package gr.grnet.pidmr.dto;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Action", description="An object represents an Action.")
public class ActionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The action mode.",
            example = "landingpage"
    )
    public String mode;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The action name.",
            example = "Landing Page"
    )
    public String name;
}
