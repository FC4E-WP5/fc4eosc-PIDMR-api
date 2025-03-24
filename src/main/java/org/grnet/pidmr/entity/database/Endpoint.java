package org.grnet.pidmr.entity.database;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@AllArgsConstructor
public class Endpoint {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The endpoint link.",
            example = "https://n2t.net/%s"
    )
    @NotEmpty(message = "link may not be empty.")
    private String link;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The endpoint provider.",
            example = "ark"
    )
    @NotEmpty(message = "provider may not be empty.")
    private String provider;
}
