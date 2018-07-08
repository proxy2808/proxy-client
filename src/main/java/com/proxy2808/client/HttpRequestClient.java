package com.proxy2808.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpRequestClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestClient.class);

    private CloseableHttpClient client;

    public HttpRequestClient() {
        this.client = buildClient();
    }

    public static CloseableHttpClient buildClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(8000)
                .setConnectTimeout(1000)
                .setSocketTimeout(20000)
                .setRedirectsEnabled(true)
                .build();
        return HttpClients.custom()
                .setMaxConnPerRoute(100)
                .setDefaultRequestConfig(requestConfig).build();
    }


    public RequestResult get(HttpActions.HttpGetBuilder getBuilder) {
        CloseableHttpResponse response =  null;
        try {
            response = client.execute(getBuilder.build());
            HttpEntity entity = response.getEntity();
            return RequestResult.create(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
        } catch (Exception e) {
            logger.warn("execute request " + getBuilder.toString() + ", fail", e);
            return RequestResult.fail();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.warn("close response exception: ", e);
                }
            }
        }
    }
}
