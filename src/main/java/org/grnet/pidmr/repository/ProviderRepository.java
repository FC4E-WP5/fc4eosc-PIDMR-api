package org.grnet.pidmr.repository;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.entity.database.Provider;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.enums.ProviderStatus;
import org.grnet.pidmr.pagination.Page;
import org.grnet.pidmr.pagination.Pageable;
import org.grnet.pidmr.pagination.PageableImpl;
import org.grnet.pidmr.util.RequestUserContext;

import java.util.Optional;

/**
 * The ProviderRepository interface provides data access methods for the Provider entity.
 */
@ApplicationScoped
public class ProviderRepository implements Repository<Provider, Long> {

    @Inject
    RequestUserContext requestUserContext;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    @Getter
    @Setter
    private String clientID;

    /**
     * Retrieves a page of from the database.
     *
     * @param page The index of the page to retrieve (starting from 0).
     * @param size The maximum number of Providers to include in a page.
     * @return A list of Provider objects representing the Providers in the requested page.
     */
    public Pageable<Provider> fetchProvidersByPage(int page, int size){

        var panache = find("status = : status", Parameters.with("status", ProviderStatus.APPROVED)).page(page, size);

        var pageable = new PageableImpl<Provider>();
        pageable.list = panache.list();
        pageable.index = page;
        pageable.size = size;
        pageable.count = panache.count();
        pageable.page = Page.of(page, size);

        return pageable;
    }

    public Pageable<Provider> fetchAdminProvidersByPage(int page, int size){

        PanacheQuery<Provider> panache = null;

        if(requestUserContext.getRoles(clientID).contains("admin")){

            panache = findAll().page(page, size);
        } else {

            panache = find("createdBy = : createdBy", Parameters.with("createdBy", requestUserContext.getVopersonID())).page(page, size);
        }

        var pageable = new PageableImpl<Provider>();
        pageable.list = panache.list();
        pageable.index = page;
        pageable.size = size;
        pageable.count = panache.count();
        pageable.page = Page.of(page, size);

        return pageable;
    }

    public Optional<Regex> valid(String pid) {

        var em = Panache.getEntityManager();
        var regex = em.createNativeQuery("SELECT r.* FROM Regex r INNER JOIN Provider p on r.provider_id=p.id WHERE '" +pid+ "' ~ regex AND p.status = 1", Regex.class);
        return regex.getResultList().stream().findFirst();
    }

    public Optional<Regex> valid(String pid, Provider provider) {

        var em = Panache.getEntityManager();

        var regex = em
                .createNativeQuery("SELECT r.* FROM Regex r INNER JOIN Provider p on r.provider_id=p.id WHERE '" +pid+ "' ~ regex AND provider_id = :providerId AND p.status = 1", Regex.class)
                .setParameter("providerId", provider.getId());

        return regex.getResultList().stream().findFirst();
    }
}