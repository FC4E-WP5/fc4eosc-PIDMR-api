package org.grnet.pidmr.entity.database;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

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
