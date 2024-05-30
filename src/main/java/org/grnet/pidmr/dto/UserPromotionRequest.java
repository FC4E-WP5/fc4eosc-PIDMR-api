package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.constraints.Email;


@Schema(name = "UserPromotionRequest", description = "This object represents the user's Role Change request.")
public class UserPromotionRequest {

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's name",
            example         = "James"
    )
    @JsonProperty("name")
    @NotEmpty(message  = "Surname is mandatory")
    public String name;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's surname",
            example         = "Smith"
    )
    @JsonProperty("surname")
    @NotEmpty(message = "Surname is mandatory")
    public String surname;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's email",
            example         = "username@domain.com"
    )
    @JsonProperty("email")
    @Email(regexp = ".+[@].+[\\.].+", message = "Please provide a valid email address.")
    @NotEmpty(message = "Email address is mandatory")
    public String email;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's role.",
            example         = "provider_admin"
    )
    @JsonProperty("role")
    @NotEmpty(message = "Role is mandatory")
    public String role;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "User's request description.",
            example         = "Change my user role"
    )
    @JsonProperty("description")
    @NotEmpty(message = "Description is mandatory")
    public String description;
}

