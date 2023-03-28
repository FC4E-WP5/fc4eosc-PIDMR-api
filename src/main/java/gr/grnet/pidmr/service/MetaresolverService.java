package gr.grnet.pidmr.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import gr.grnet.pidmr.entity.MetaResolver;
import gr.grnet.pidmr.entity.Provider;
import gr.grnet.pidmr.exception.FailedToStartException;
import gr.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.NotAcceptableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class MetaresolverService {

    @ConfigProperty(name = "list.metaresolvers.file")
    String path;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ProviderService providerService;

    public MetaresolverService(ObjectMapper objectMapper, ProviderService providerService) {
        this.objectMapper = objectMapper;
        this.providerService = providerService;
    }

    /**
     * This method returns the Metaresolver location that resolves the PID.
     *
     * @param pid The pid to be resolved.
     * @return The Metaresolver URL, which resolves the PID.
     */
    public String resolve(String pid) {

        var type = providerService.getPidType(pid);

        var candidateType = type.orElseThrow(()->new NotAcceptableException(String.format("%s doesn't belong to any of the available types.", pid)));

        var provider = providerService.getProviderByType(candidateType);

        var calibratedPid = provider.calibratePid(pid);

        var eoscMetaresolver = getMetaresolverByKey(provider.getMetaresolver());

        return eoscMetaresolver.getLocation().concat(calibratedPid);
    }

    /**
     * This method returns the Metaresolver that corresponds to a particular key.
     *
     * @param key The Metaresolver key as defined in the metaresolvers.conf file.
     * @return The corresponding Metaresolver.
     */
    public MetaResolver getMetaresolverByKey(String key) {

        var metaResolvers = getMetaresolvers();

        Map<String, MetaResolver> map = metaResolvers.stream()
                .collect(Collectors.toMap(MetaResolver::getKey, Function.identity()));

        return map.get(key);
    }

    /**
     * This method returns the available Metaresolvers.
     *
     * @return A set of Metaresolvers.
     */
    @CacheResult(cacheName = "metaresolvers")
    public Set<MetaResolver> getMetaresolvers(){

        return Utility.toSet(MetaResolver.class, objectMapper, path);
    }

    void onStart(@Observes StartupEvent ev) {

        var mapper = JsonMapper
                .builder()
                .build();

        // try to read the metaresolvers file. If the file cannot be read, the application cannot start.
        try {
            Utility.toSet(MetaResolver.class, mapper, path);
        } catch (Exception e) {
            throw new FailedToStartException("The file containing the Metaresolvers cannot be read.");
        }
    }
}
