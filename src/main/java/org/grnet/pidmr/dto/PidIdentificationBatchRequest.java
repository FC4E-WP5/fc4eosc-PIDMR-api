package org.grnet.pidmr.dto;

import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

@Schema(name="PidIdentificationBatchRequest", description="A batch request containing multiple PID identification requests.")
public class PidIdentificationBatchRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "Each item in the list corresponds to a single PID identification request.",
            example = "[\n" +
                    "        \"ark:/13030\",\n" +
                    "         \"arXiv:\",\n" +
                    "        \"swh:1:\",\n" +
                    "        \"urn\",\n" +
                    "        \"urn:nbn:fi\",\n" +
                    "       \"0000-0001-9547-1582\",\n" +
                    "       \"10.5281\",\n" +
                    "       \"21.T11148\",\n" +
                    "       \"10.5281/zenodo.8056361\",\n" +
                    "       \"10.15167/tomasi-federico_phd2019-03-14\",\n" +
                    "       \"00cd95c65\"\n" +
                    "    ]",
            minItems = 1
    )
    @NotEmpty(message = "data should have at least one entry.")
    public Set<@NotEmpty(message = "text to be identified may not be empty.")  String> data = new HashSet<>();
}
