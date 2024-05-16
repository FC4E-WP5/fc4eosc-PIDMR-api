package org.grnet.pidmr.entity.database;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * This entity represents the Action table in database.
 *
 */
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Action {

    /**
     * As id, we use the pid mode.
     */
    @Id
    private String id;

    /**
     * The pid mode.
     */
    @Column
    @NotNull
    @EqualsAndHashCode.Include
    private String mode;

    /**
     * The pid name.
     */
    @Column
    @NotNull
    private String name;

    @OneToMany(
            mappedBy = "action",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ProviderActionJunction> providers = new HashSet<>();
}
