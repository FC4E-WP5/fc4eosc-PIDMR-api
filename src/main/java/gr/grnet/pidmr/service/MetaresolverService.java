package gr.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.pidmr.dto.MetaResolver;
import gr.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class MetaresolverService {

    @ConfigProperty(name = "list.metaresolvers.file")
    String path;

    @Inject
    ObjectMapper objectMapper;

    public MetaresolverService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * This method returns the Metaresolver location that resolves the PID.
     *
     * @param pid The pid to be resolved.
     * @return The Metaresolver URL, which resolves the PID.
     */
    public String resolve(String pid) {

        // For now we use the EOSC metaresolver
        var eoscMetaresolver = getMetaresolver("FC4EOSC");

        return eoscMetaresolver.getLocation().concat(pid);
    }

    /**
     * This method returns the Metaresolver that corresponds to a particular key.
     *
     * @param key The Metaresolver key as defined in the metaresolvers.conf file.
     * @return The corresponding Metaresolver.
     */
    @CacheResult(cacheName = "metaresolvers")
    public MetaResolver getMetaresolver(String key) {

        var metaResolvers = Utility.toSet(MetaResolver.class, objectMapper, path);

        Map<String, MetaResolver> map = metaResolvers.stream()
                .collect(Collectors.toMap(MetaResolver::getKey, Function.identity()));

        return map.get(key);
    }
}
