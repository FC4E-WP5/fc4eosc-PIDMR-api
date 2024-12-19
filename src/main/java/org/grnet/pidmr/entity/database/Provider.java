package org.grnet.pidmr.entity.database;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.grnet.pidmr.entity.AbstractProvider;
import org.grnet.pidmr.entity.database.converters.ProviderStatusAttributeConverter;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.exception.ModeIsNotSupported;

import java.util.*;

/**
 * This entity represents the Provider table in database.
 */
@Entity
@DiscriminatorValue("Provider")
@Getter
@Setter
public class Provider extends ManageableEntity implements AbstractProvider {

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

    @Convert(converter = ProviderStatusAttributeConverter.class)
    @NotNull
    private ProviderStatus status;

    @OneToMany(
            mappedBy = "provider",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @NotEmpty
    private List<Regex> regexes = new ArrayList<>();

    @OneToMany(
            mappedBy = "provider",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<ProviderActionJunction> actions = new HashSet<>();

    @Column(name = "direct_resolution")
    @NotNull
    private boolean directResolution;

    /**
     * A PID example.
     */
    @Column
    @NotNull
    private String example;

    @Column(name = "relies_on_dois")
    private boolean reliesOnDois;

    public void addAction(Action action, String[] endpoints ) {
        var providerAction = new ProviderActionJunction(this, action, endpoints);
        actions.add(providerAction);
        action.getProviders().add(providerAction);
    }

    public void removeAction(Action action) {
        for (Iterator<ProviderActionJunction> iterator = actions.iterator();
             iterator.hasNext(); ) {
            var providerAction = iterator.next();

            if (providerAction.getProvider().equals(this) &&
                    providerAction.getAction().equals(action)) {
                iterator.remove();
                providerAction.getAction().getProviders().remove(providerAction);
                providerAction.setProvider(null);
                providerAction.setAction(null);
            }
        }
    }

    public void addRegex(Regex regex) {

        regexes.add(regex);
        regex.setProvider(this);
    }

    public void removeRegex(Regex regex) {

        regexes.remove(regex);
        regex.setProvider(null);
    }
    @Override
    public boolean directResolution() {
        return isDirectResolution();
    }

    @Override
    public void isModeSupported(String mode) {

        getActions()
                .stream()
                .filter(action -> action.getAction().getMode().equals(mode))
                .findAny()
                .orElseThrow(() -> new ModeIsNotSupported(String.format("This mode {%s} is not supported.", mode)));
    }
}


