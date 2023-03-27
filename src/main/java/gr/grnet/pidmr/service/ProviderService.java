package gr.grnet.pidmr.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import gr.grnet.pidmr.dto.Validity;
import gr.grnet.pidmr.entity.Provider;
import gr.grnet.pidmr.exception.FailedToStartException;
import gr.grnet.pidmr.mapper.ProviderMapper;
import gr.grnet.pidmr.pagination.Page;
import gr.grnet.pidmr.pagination.PageResource;
import gr.grnet.pidmr.pagination.PageableImpl;
import gr.grnet.pidmr.dto.ProviderDto;
import gr.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class ProviderService {

    @ConfigProperty(name = "list.providers.file")
    String path;

    /**
     * This method is responsible for paginating the available Providers.
     * It returns from a specific page as many Providers as the client requests.
     *
     * @param page Indicates the page number.
     * @param size The total number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<ProviderDto> pagination(int page, int size, UriInfo uriInfo) {

        var allProviders = getProviders();

        var partition = Utility.partition(new ArrayList<>(allProviders), size);

        var providers = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new PageableImpl<ProviderDto>();

        pageable.list = ProviderMapper.INSTANCE.providersToDto(providers);
        pageable.index = page;
        pageable.size = size;
        pageable.count = allProviders.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, uriInfo);
    }

    /**
     * This method returns the available Providers.
     * @return A set of Providers.
     */
    @CacheResult(cacheName = "providers")
    public Set<Provider> getProviders(){

        var mapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .build();

       return Utility.toSet(Provider.class, mapper, path);
    }

    /**
     * This method returns the Provider that corresponds to a particular type.
     *
     * @param type The type of Provider.
     * @return The corresponding Provider.
     */

    @CacheResult(cacheName = "providersToMap")
    public Provider getProviderByType(String type){

        var map = getProviders()
                .stream()
                .collect(Collectors.toMap(Provider::getType, Function.identity()));

        return map.get(type);
    }

    /**
     * Each identifier should match the regular expression provided by its Provider.
     * Getting the appropriate regex corresponding to the incoming identifier,
     * this method checks whether it's valid.
     *
     * @param pid The pid to be validated.
     * @return An object representing whether a PID is valid.
     */
    public Validity valid(String pid){

        var providers = getProviders();

        var type = providers
                .stream()
                .map(Provider::getType)
                .filter(tp->belongsTo(pid, tp))
                .findAny();

        var candidateType = type.orElseThrow(()->new NotAcceptableException(String.format("%s doesn't belong to any of the available types.", pid)));

        var provider = getProviderByType(candidateType);

        return valid(provider, pid);
    }

    /**
     * Each identifier should match the regular expression provided by its Provider.
     * Given the type of pid, this method checks the incoming pid validity.
     * @param pid The pid to be validated.
     * @param type The type that pid belongs to.
     * @return An object representing whether a PID is valid.
     */
    public Validity valid(String pid, String type){

        var provider = getProviderByType(type);

        if(Objects.isNull(provider)){
            throw new NotAcceptableException(String.format("This type {%s} is not supported.", type));
        }

        if(!belongsTo(pid, type)){
            throw new NotAcceptableException(String.format("%s doesn't belong to this type : %s.", pid, type));
        }

        return valid(provider, pid);
    }

    /**
     * Each identifier should match the regular expression provided by its Provider.
     *
     * @param pid The pid to be validated.
     * @param provider The provider that pid belongs to.
     * @return An object representing whether a PID is valid.
     */
    public Validity valid(Provider provider, String pid){

        var regex = provider.getRegex();

        var valid = regex
                .stream()
                .anyMatch(pid::matches);

        var validity = new Validity();
        validity.valid = valid;
        validity.type = provider.getType();

        return validity;
    }

    /**
     * This method checks if the incoming pid belongs to a particular type.
     * @param pid The incoming pid.
     * @param type The type of pid to which the incoming pid may belong.
     * @return If the incoming pid belongs to the given type.
     */
    private boolean belongsTo(String pid, String type){

        return IntStream
                .range(0, type.length())
                .allMatch(index->Character.toLowerCase(type.charAt(index)) == Character.toLowerCase(pid.charAt(index)));
    }

    void onStart(@Observes StartupEvent ev) {

        // try to read the providers file. If the file doesn't exist, the application cannot start.
        try {
            Files.lines(Paths.get(path)).collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new FailedToStartException("The file containing the Providers has not been found.");
        }
    }
}