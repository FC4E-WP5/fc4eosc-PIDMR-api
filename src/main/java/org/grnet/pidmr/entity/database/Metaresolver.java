package org.grnet.pidmr.entity.database;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 * This entity represents the Metaresolver table in database.
 *
 */
@Entity
public class Metaresolver {

    /**
     * As id, we use a key described the Metaresolver.
     */
    @Id
    private String id;

    /**
     * The Metaresolver location where a pid is resolved.
     */
    @Column
    @NotNull
    private String location;

    /**
     * The Metaresolver description.
     */
    @Column
    @NotNull
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
