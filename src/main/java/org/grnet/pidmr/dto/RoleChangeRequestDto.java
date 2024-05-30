package org.grnet.pidmr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.grnet.pidmr.enums.PromotionRequestStatus;


@Schema(name = "RoleChangeRequestDto", description = "This object represents the user's Role Change request.")
public class RoleChangeRequestDto {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Role change request id",
            example = "5"
    )
    public Long id;

    @Schema(
            type = SchemaType.STRING,
            implementation = Long.class,
            description = "User's voperson_id.",
            example = "admin_voperson_id"
    )
    @JsonProperty("user_id")
    public String userId;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's name",
            example         = "James"
    )
    public String name;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's surname",
            example         = "Smith"
    )
    public String surname;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's email",
            example         = "username@domain.com"
    )
    public String email;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "The user's role.",
            example         = "provider_admin"
    )
    public String role;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "User's request description.",
            example         = "Change my user role"
    )
    public String description;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "Datetime of request.",
            example         = "2024-05-30T09:40:22.813+03:00"
    )
    @JsonProperty("requested_on")
    public String requestedOn;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "Datetime of update",
            example         = "2024-10-30T09:40:22.813+03:00"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("updated_on")
    public String updatedOn;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = String.class,
            description     = "Id of admin approving the request",
            example         = "admin_voperson_id"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("updated_by")
    public String updatedBy;

    @Schema(
            type            = SchemaType.STRING,
            implementation  = PromotionRequestStatus.class,
            description     = "Status of the request",
            example         = "[PENDING, APPROVED]"
    )
    public String status;

}

