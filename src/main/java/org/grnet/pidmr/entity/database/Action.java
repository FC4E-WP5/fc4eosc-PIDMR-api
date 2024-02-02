package org.grnet.pidmr.entity.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * This entity represents the Action table in database.
 *
 */
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    @ManyToMany(mappedBy = "actions")
    private Set<Provider> providers = new HashSet<>();

    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
