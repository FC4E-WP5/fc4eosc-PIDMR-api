package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ServerErrorException;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.grnet.pidmr.entity.AbstractProvider;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.TimeUnit;

/**
 * This MetaresolverService initializes the available Metaresolvers provided in the metaresolvers.conf file.
 */
@ApplicationScoped
public class MetaresolverService implements MetaresolverServiceI {

    @ConfigProperty(name = "list.metaresolvers.file")
    String path;

    @ConfigProperty(name = "api.pidmr.proxy.resolve.mode.url")
    String proxy;

    @ConfigProperty(name = "api.pidmr.proxy.resolve.mode.body.attribute")
    String bodyAttribute;

    @ConfigProperty(name = "api.pidmr.proxy.resolve.mode.body.attribute.prefix")
    String bodyAttributePrefix;

    @ConfigProperty(name = "api.pidmr.proxy.resolve.mode.url.append.param")
    String appendParam;

    @Inject
    @Named("database-provider-service")
    ProviderServiceI providerService;

    public final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();


    @SneakyThrows
    public String resolve(AbstractProvider provider, String pid, String mode) {

        if(provider.directResolution() && mode.equals("metadata")){

            return proxy.concat(pid).concat("?noredirect");
        }

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

            throw new ServerErrorException("Cannot communicate with metaresolver: "+e.getMessage(), 500);
        }
    }

    /**
     * This method returns the Metaresolver location that resolves the PID.
     *
     * @param pid The pid to be resolved.
     * @param mode The display mode.
     * @return The Metaresolver URL, which resolves the PID.
     */
    @CacheResult(cacheName = "pid-resolution")
    public String resolve(String pid, String mode) {

        var provider = providerService.getProviderByPid(pid, mode);

        return resolve(provider, pid, mode);
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
}
