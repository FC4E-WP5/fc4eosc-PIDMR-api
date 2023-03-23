package gr.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.pidmr.pagination.Page;
import gr.grnet.pidmr.pagination.PageResource;
import gr.grnet.pidmr.pagination.PageableImpl;
import gr.grnet.pidmr.dto.Provider;
import gr.grnet.pidmr.util.Utility;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collections;

@ApplicationScoped
public class ProviderService {

    @ConfigProperty(name = "list.providers.file")
    String path;

    @Inject
    ObjectMapper objectMapper;

    public ProviderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * This method is responsible for paginating the available Providers.
     * It returns from a specific page as many Providers as the client requests.
     *
     * @param page Indicates the page number.
     * @param size The total number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<Provider> pagination(int page, int size, UriInfo uriInfo) {

        var allProviders = Utility.toSet(Provider.class, objectMapper, path);

        var partition = Utility.partition(new ArrayList<>(allProviders), size);

        var providers = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new PageableImpl<Provider>();

        pageable.list = providers;
        pageable.index = page;
        pageable.size = size;
        pageable.count = allProviders.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, uriInfo);
    }
}