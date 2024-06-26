package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="RoleAssignment", description="This object represents a role assignment request.")
public class RoleAssignmentRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            required = true,
            description = "The unique identifier of the user.",
            example = "7827fbb605a23b0cbd5cb4db292fe6dd6c7734a27057eb163d616a6ecd02d2ec@einfra.grnet.gr"
    )
    @JsonProperty("user_id")
    @NotEmpty(message = "user_id may not be empty.")
    public String userId;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "Array of role names to be assigned to the user.",
            example = "[\"admin\"]"
    )
    @JsonProperty("roles")
    @NotEmpty(message = "roles may not be empty.")
    public List<String> roles;
}
