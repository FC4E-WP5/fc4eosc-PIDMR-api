package gr.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import gr.grnet.pidmr.entity.MetaResolver;
import gr.grnet.pidmr.exception.FailedToStartException;
import gr.grnet.pidmr.mapper.ProviderMapper;
import gr.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import java.util.Set;

/**
 * This MetaresolverService initializes the available Metaresolvers provided in the metaresolvers.conf file.
 */
@ApplicationScoped
public class MetaresolverService implements MetaresolverServiceI {

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

    @Override
    public String resolve(MetaResolver metaResolver, String pid) {
        return metaResolver.getLocation().concat(pid);
    }

    @Override
    public String resolve(MetaResolver metaResolver, String pid, String mode) {
        return metaResolver.getLocation().concat(pid).concat("?").concat(mode);
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

    /**
     * This method returns the available Metaresolvers.
     *
     * @return A set of Metaresolvers.
     */
    @CacheResult(cacheName = "metaresolvers")
    public Set<MetaResolver> getMetaresolvers(){

        return Utility.toSet(MetaResolver.class, objectMapper, path);
    }

    /**
     * This method returns the Metaresolver location that resolves the PID.
     *
     * @param pid The pid to be resolved.
     * @param mode The display mode.
     * @return The Metaresolver URL, which resolves the PID.
     */
    public String resolve(String pid, String mode) {

        var type = providerService.getPidType(pid);

        var candidateType = type.orElseThrow(()->new NotAcceptableException(String.format("%s doesn't belong to any of the available types.", pid)));

        var provider = providerService.getProviderByType(candidateType);

        var calibratedPid = provider.calibratePid(pid);

        var eoscMetaresolver = getMetaresolverByKey(provider.getMetaresolver());

        if(mode.isEmpty()){
            return resolve(eoscMetaresolver, calibratedPid);
        } else {
            ProviderMapper.INSTANCE.actions(provider.getActions())
                    .stream()
                    .filter(action->action.mode.equals(mode))
                    .findAny()
                    .orElseThrow(()->new BadRequestException(String.format("This mode {%s} is not supported.", mode)));
            return resolve(eoscMetaresolver, calibratedPid, mode);
        }
    }
}
