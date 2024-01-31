package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.enums.ProviderStatus;

@Schema(name="Provider", description="An object represents a Provider the user can manage.")
public class AdminProviderDto extends ProviderDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = ProviderStatus.class,
            description = "The status of the Provider.",
            example = "APPROVED"
    )
    @JsonProperty("status")
    public String status;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The ID of the user to who the Provider belongs.",
            example = "7827fbb605a23b0cbd5cb4db292fe6dd6c7734a27057eb163d616a6ecd02d2ec@einfra.grnet.gr"
    )
    @JsonProperty("user_id")
    public String userId;
}
