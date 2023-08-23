package org.grnet.pidmr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.grnet.pidmr.entity.MetaResolver;
import org.grnet.pidmr.entity.Provider;
import org.grnet.pidmr.exception.FailedToStartException;
import org.grnet.pidmr.mapper.ProviderMapper;
import org.grnet.pidmr.util.Utility;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This MetaresolverService initializes the available Metaresolvers provided in the metaresolvers.conf file.
 */
@ApplicationScoped
public class MetaresolverService implements MetaresolverServiceI {

    @ConfigProperty(name = "list.metaresolvers.file")
    String path;

    @ConfigProperty(name = "proxy.resolve.mode.url")
    String proxy;

    @ConfigProperty(name = "proxy.resolve.mode.body.attribute")
    String bodyAttribute;

    @ConfigProperty(name = "proxy.resolve.mode.body.attribute.prefix")
    String bodyAttributePrefix;

    @ConfigProperty(name = "proxy.resolve.mode.url.append.param")
    String appendParam;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ProviderService providerService;

    public final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();

    public MetaresolverService(ObjectMapper objectMapper, ProviderService providerService) {
        this.objectMapper = objectMapper;
        this.providerService = providerService;
    }

    @Override
    public String resolve(Provider provider, String pid) {

        var eoscMetaresolver = getMetaresolverByKey(provider.getMetaresolver());
        return provider.resolve(eoscMetaresolver, pid);
    }

    @Override
    @SneakyThrows
    //@CacheResult(cacheName = "pidMode")
    public String resolve(Provider provider, String pid, String mode) {

        ProviderMapper.INSTANCE.actions(provider.getActions())
                .stream()
                .filter(action->action.mode.equals(mode))
                .findAny()
                .orElseThrow(()->new BadRequestException(String.format("This mode {%s} is not supported.", mode)));

        var body = provider.getRequestBody(pid, mode, bodyAttribute, bodyAttributePrefix, appendParam);

        var request = new Request
                .Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(proxy)
                .post(body)
                .build();

        try (var response = client.newCall(request).execute()) {

            return response.request().url().toString();
        } catch (Exception e) {

            throw new InternalServerErrorException("Cannot communicate with metaresolver: "+e.getMessage());
        }
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

        if(mode.isEmpty()){
            return resolve(provider, pid);
        } else {
            return resolve(provider, pid, mode);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getBodyAttribute() {
        return bodyAttribute;
    }

    public void setBodyAttribute(String bodyAttribute) {
        this.bodyAttribute = bodyAttribute;
    }

    public String getBodyAttributePrefix() {
        return bodyAttributePrefix;
    }

    public void setBodyAttributePrefix(String bodyAttributePrefix) {
        this.bodyAttributePrefix = bodyAttributePrefix;
    }

    public String getAppendParam() {
        return appendParam;
    }

    public void setAppendParam(String appendParam) {
        this.appendParam = appendParam;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ProviderService getProviderService() {
        return providerService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }
}
