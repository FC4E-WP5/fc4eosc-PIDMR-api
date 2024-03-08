package org.grnet.pidmr.entity.database;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * The {@code ManageableEntity} class is a base class that represents a manageable entity.
 * A manageable entity is an object that can be managed by the user who created it.
 * It stores information about the creator user.
 * <p>
 * To use this class, extend it in your own classes and provide additional properties as needed.
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="entity_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class ManageableEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "entity_type", insertable=false, updatable=false)
    @EqualsAndHashCode.Include
    private String entityType;
}
