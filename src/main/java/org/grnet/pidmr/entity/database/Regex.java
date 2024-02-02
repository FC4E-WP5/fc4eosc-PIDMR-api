package org.grnet.pidmr.entity.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;


/**
 * This entity represents the Regex table in database.
 * This table encapsulates the regex patterns the Provider uses to check whether a pid is valid.
 *
 */
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Regex {

    /**
     * As id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The regex pattern.
     */
    @Column
    private String regex;

    @ManyToOne(fetch = FetchType.EAGER)
    private Provider provider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
