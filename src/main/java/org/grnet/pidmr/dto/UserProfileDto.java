package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.Set;

@Schema(name = "UserProfile", description = "This object represents the User Profile.")
public class UserProfileDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique identifier for the user.",
            example = "24329fb1b49c7fc0aa668d07410d4857a685c1d365976e42823368faa27442e7@aai.eosc-portal.eu"
    )
    public String id;
    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "The user's roles.",
            example = "[admin, provider_admin]"
    )
    @JsonProperty("roles")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Set<String> roles;
}
