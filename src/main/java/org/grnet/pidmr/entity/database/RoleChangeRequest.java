package org.grnet.pidmr.entity.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.grnet.pidmr.entity.database.converters.RoleChangeStatusConverter;
import org.grnet.pidmr.enums.RoleChangeRequestStatus;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class RoleChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    @NotNull
    private String userId;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private String surname;

    @Column
    @NotNull
    private String role;

    @Column
    @NotNull
    private String description;

    @Column
    @NotNull
    private String email;

    @Column(name = "requested_on")
    @NotNull
    private Timestamp requestedOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column
    @NotNull
    @Convert(converter = RoleChangeStatusConverter.class)
    private RoleChangeRequestStatus status;
}
