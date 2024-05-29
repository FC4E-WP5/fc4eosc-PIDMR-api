package org.grnet.pidmr.repository;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.grnet.pidmr.enums.ProviderStatus;

import java.util.Optional;

@ApplicationScoped
public class RoleChangeRequestsRepository implements Repository<RoleChangeRequest, Long>{

    /**
     * Checks if the specified user has executed a promotion request.
     * <p>
     * This method queries the database to determine if the given user has performed
     * any promotion request.
     * </p>
     *
     * @param userId the ID of the user to check
     * @return {@code true} if the user has executed a promotion request, {@code false} otherwise
     */
    public Optional<RoleChangeRequest> hasUserExecutedPromotionRequest(String userId){

        return find("userId = : userId", Parameters.with("userId", userId)).stream().findAny();
    }
}
