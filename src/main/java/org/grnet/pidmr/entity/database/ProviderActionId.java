package org.grnet.pidmr.entity.database;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ProviderActionId {

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "action_id")
    private String actionId;

    public ProviderActionId(Long providerId, String actionId) {
        this.providerId = providerId;
        this.actionId = actionId;
    }
    public ProviderActionId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ProviderActionId that = (ProviderActionId) o;
        return Objects.equals(providerId, that.providerId) &&
                Objects.equals(actionId, that.actionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, actionId);
    }
}
