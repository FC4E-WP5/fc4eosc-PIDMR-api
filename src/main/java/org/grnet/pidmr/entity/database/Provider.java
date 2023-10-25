package org.grnet.pidmr.entity.database;

import org.grnet.pidmr.entity.AbstractProvider;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This entity represents the Provider table in database.
 */
@Entity
public class Provider extends AbstractProvider {

    /**
     * As id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Provider type.
     */
    @Column(unique = true)
    @NotNull
    private String type;

    /**
     * The Provider name.
     */
    @Column
    @NotNull
    private String name;

    /**
     * The Provider description.
     */
    @Column
    @NotNull
    private String description;

    /**
     * To be able to resolve some pids, it is necessary to remove some of its first characters.
     */
    @Column(name = "characters_to_be_removed")
    private int charactersToBeRemoved;

    @OneToOne
    @JoinColumn(name = "metaresolver_id", referencedColumnName = "id")
    private Metaresolver metaresolver;

    @OneToMany(
            mappedBy = "provider",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @NotEmpty
    private List<Regex> regexes = new ArrayList<>();

    @ManyToMany(fetch= FetchType.EAGER,
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "Provider_Action_Junction",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    @NotEmpty
    private Set<Action> actions = new HashSet<>();

    /**
     * A PID example.
     */
    @Column
    @NotNull
    private String example;

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }

    public void addAction(Action action) {
        actions.add(action);
        action.getProviders().add(this);
    }

    public void removeAction(Action action) {
        actions.remove(action);
        action.getProviders().remove(this);
    }

    public List<Regex> getRegexes() {
        return regexes;
    }

    public void setRegexes(List<Regex> regexes) {
        this.regexes = regexes;
    }

    public void addRegex(Regex regex) {
        regexes.add(regex);
        regex.setProvider(this);
    }

    public void removeRegex(Regex regex) {
        regexes.remove(regex);
        regex.setId(null);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCharactersToBeRemoved() {
        return charactersToBeRemoved;
    }

    public void setCharactersToBeRemoved(int charactersToBeRemoved) {
        this.charactersToBeRemoved = charactersToBeRemoved;
    }

    public Metaresolver getMetaresolver() {
        return metaresolver;
    }

    public void setMetaresolver(Metaresolver metaresolver) {
        this.metaresolver = metaresolver;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
