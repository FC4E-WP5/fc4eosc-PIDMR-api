package org.grnet.pidmr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.ServerErrorException;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.grnet.pidmr.dto.PidResolutionBatchResponse;
import org.grnet.pidmr.dto.PidResolutionRequest;
import org.grnet.pidmr.dto.PidResolutionResponse;
import org.grnet.pidmr.entity.AbstractProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.pidmr.exception.ModeIsNotSupported;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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

    public MetaresolverService() {
    }

    @SneakyThrows
    public String resolve(AbstractProvider provider, String pid, String mode) {

        if (provider.directResolution() && mode.equals("metadata")) {

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

            throw new ServerErrorException("Cannot communicate with metaresolver: " + e.getMessage(), 500);
        }
    }

    public PidResolutionBatchResponse asynchronousResolution(Set<PidResolutionRequest> pids) throws InterruptedException {

        var response = new PidResolutionBatchResponse();

        var data = new HashMap<String, List<PidResolutionResponse>>();

        pids.forEach(req-> {

            req.pid = java.net.URLDecoder.decode(req.pid.trim(), StandardCharsets.UTF_8);
            data.putIfAbsent(req.pid, new ArrayList<>());
        });

        final var responseCountDownLatch = new CountDownLatch(pids.size());

        pids.forEach(request -> {

            try {

                var provider = providerService.getProviderByPid(request.pid);

                provider.isModeSupported(request.mode);

                if (provider.directResolution() && request.mode.equals("metadata")) {

                    responseCountDownLatch.countDown();
                    var result = new PidResolutionResponse();
                    result.mode = request.mode;
                    result.url = proxy.concat(request.pid).concat("?noredirect");
                    result.success = Boolean.TRUE;
                    result.message = StringUtils.EMPTY;
                    data.get(request.pid).add(result);

                }else {

                    var body = provider.getRequestBody(request.pid, request.mode, bodyAttribute, bodyAttributePrefix, appendParam);

                    var httpRequest = new Request
                            .Builder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("PID", request.pid)
                            .addHeader("MODE", request.mode)
                            .url(proxy)
                            .post(body)
                            .build();
                    client.newCall(httpRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            responseCountDownLatch.countDown();

                            var result = new PidResolutionResponse();
                            result.mode = call.request().header("MODE");
                            result.url = StringUtils.EMPTY;
                            result.success = Boolean.FALSE;
                            result.message = e.getMessage();
                            data.get(call.request().header("PID")).add(result);
                        }

                        @Override
                        public void onResponse(Call call, Response response) {

                            responseCountDownLatch.countDown();

                            try (ResponseBody responseBody = response.body()) {

                                var result = new PidResolutionResponse();
                                result.mode = call.request().header("MODE");

                                if (!response.isSuccessful()) {

                                    result.url = StringUtils.EMPTY;
                                    result.success = Boolean.FALSE;
                                    result.message = response.message();
                                } else {

                                    result.url = response.request().url().toString();
                                    result.success = Boolean.TRUE;
                                    result.message = StringUtils.EMPTY;
                                }
                                data.get(call.request().header("PID")).add(result);
                            }
                        }
                    });
                }

            }
            catch (ModeIsNotSupported | NotAcceptableException exception){

                responseCountDownLatch.countDown();
                var result = new PidResolutionResponse();

                result.message = exception.getMessage();
                result.success = Boolean.FALSE;
                result.url = StringUtils.EMPTY;
                result.mode = StringUtils.EMPTY;

                data.get(request.pid).add(result);
            }
        });

        responseCountDownLatch.await();
        response.data = data;

        return response;
    }

    /**
     * This method returns the Metaresolver location that resolves the PID.
     *
     * @param pid  The pid to be resolved.
     * @param mode The display mode.
     * @return The Metaresolver URL, which resolves the PID.
     */
    public String resolve(String pid, String mode) {

        var provider = providerService.getProviderByPid(pid);

        provider.isModeSupported(mode);

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
