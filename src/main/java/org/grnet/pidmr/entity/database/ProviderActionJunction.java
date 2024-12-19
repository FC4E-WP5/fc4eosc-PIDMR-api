package org.grnet.pidmr.entity.database;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity(name = "ProviderActionJunction")
@Table(name = "Provider_Action_Junction")
@Getter
@Setter
public class ProviderActionJunction {

    @EmbeddedId
    private ProviderActionId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("providerId")
    private Provider provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("actionId")
    private Action action;

    @Column(name = "endpoints")
    private String[] endpoints;

    public ProviderActionJunction(Provider provider, Action action,String[] endpoints) {
        this.provider = provider;
        this.action = action;
        this.endpoints = endpoints;
        this.id = new ProviderActionId(provider.getId(), action.getId());
    }

    public ProviderActionJunction() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ProviderActionJunction that = (ProviderActionJunction) o;
        return Objects.equals(provider, that.provider) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, action);
    }

}

