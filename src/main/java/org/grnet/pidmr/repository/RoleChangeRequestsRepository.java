package org.grnet.pidmr.repository;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.RoleChangeRequest;
import org.grnet.pidmr.pagination.Page;
import org.grnet.pidmr.pagination.Pageable;
import org.grnet.pidmr.pagination.PageableImpl;

import java.util.Optional;

@ApplicationScoped
public class RoleChangeRequestsRepository implements Repository<RoleChangeRequest, Long>{

    /**
     * Checks if the specified user has executed a role change request.
     * <p>
     * This method queries the database to determine if the given user has performed
     * any role change request.
     * </p>
     *
     * @param userId the ID of the user to check
     * @return {@code true} if the user has executed a role change request, {@code false} otherwise
     */
    public Optional<RoleChangeRequest> hasUserExecutedRoleChangeRequest(String userId){

        return find("userId = : userId", Parameters.with("userId", userId)).stream().findAny();
    }
    /**
     * Retrieves a page of from the database.
     *
     * @param page The index of the page to retrieve (starting from 0).
     * @param size The maximum number of Providers to include in a page.
     * @return A list of Provider objects representing the Providers in the requested page.
     */
    public Pageable<RoleChangeRequest> fetchRoleChangeRequestByPage(int page, int size){

        var panache = findAll(Sort.by("status", Sort.Direction.Ascending).and("requestedOn", Sort.Direction.Descending)).page(page, size);
        var pageable = new PageableImpl<RoleChangeRequest>();
        pageable.list = panache.list();
        pageable.index = page;
        pageable.size = size;
        pageable.count = panache.count();
        pageable.page = Page.of(page, size);

        return pageable;
    }


}
