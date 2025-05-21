package org.grnet.pidmr.entity.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class MetadataPathEntry {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider.",
            example = "ark"
    )
    private String provider;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The metadata path.",
            example = "https://n2t.net/%s/?"
    )
    private String path;
}
