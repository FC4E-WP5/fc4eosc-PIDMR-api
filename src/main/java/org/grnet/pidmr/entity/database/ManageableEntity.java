package org.grnet.pidmr.entity.database;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * The {@code ManageableEntity} class is a base class that represents a manageable entity.
 * A manageable entity is an object that can be managed by the user who created it.
 * It stores information about the creator user.
 * <p>
 * To use this class, extend it in your own classes and provide additional properties as needed.
 */
@MappedSuperclass
@Setter
@Getter
public abstract class ManageableEntity<T> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private T id;

    @Column(name = "created_by")
    private String createdBy;
}
