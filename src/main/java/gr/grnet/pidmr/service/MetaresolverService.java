package gr.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.grnet.pidmr.entity.MetaResolver;
import gr.grnet.pidmr.exception.FailedToStartException;
import gr.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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
        var eoscMetaresolver = getMetaresolverByKey("FC4EOSC");

        return eoscMetaresolver.getLocation().concat(pid);
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

        // try to read the metaresolvers file. If the file doesn't exist, the application cannot start.
        try {
            Files.lines(Paths.get(path)).collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new FailedToStartException("The file containing the Metaresolvers has not been found.");
        }
    }
}
