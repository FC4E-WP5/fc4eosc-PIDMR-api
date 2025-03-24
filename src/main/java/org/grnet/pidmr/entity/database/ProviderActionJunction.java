package org.grnet.pidmr.entity.database;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Objects;

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

    @Type(JsonType.class)
    @Column(name = "endpoints", columnDefinition = "jsonb")
    private List<Endpoint> endpoints;

    public ProviderActionJunction(Provider provider, Action action, List<Endpoint> endpoints) {
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

