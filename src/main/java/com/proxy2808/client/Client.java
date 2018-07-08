package com.proxy2808.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String API_URL = "https://api.2808proxy.com";

    private String userName;
    private String password;
    private String token;
    private Boolean enhancedAuth;
    private Boolean tokenChangeOnLogin;
    private HttpRequestClient httpClient;

    private Client() {}


    public Client(String userName, String password, String token, Boolean enhancedAuth, Boolean tokenChangeOnLogin) {

        this.userName = userName;
        this.password = password;
        this.token = token;
        this.enhancedAuth = enhancedAuth;
        this.tokenChangeOnLogin = tokenChangeOnLogin;
        this.httpClient = new HttpRequestClient();
        this.token = this.getToken().getData();
    }

    public Client(String userName, String password) {
        this(userName, password, null, Boolean.FALSE, Boolean.FALSE);
    }

    private static <V> TypeReference<V> toTypeRef(final Class<V> clazz) {
        return new TypeReference<V>() {
            public Type getType() {
                return clazz;
            }
        };
    }

    private <T> T convert(String content, Class<T> clazz) {
         try {
             return objectMapper.readValue(content, toTypeRef(clazz));
         } catch (IOException e) {
             logger.warn("[2808proxy] convert request result fail, e: ", e);
             throw new RuntimeException("[2808proxy] convert request result fail", e);
         }
    }

    private HttpActions.HttpGetBuilder getLoginGetBuilder() throws Proxy2808Exception {
        String url = API_URL + "/login";
        return new HttpActions.HttpGetBuilder(url)
                .setParameter("username", this.userName)
                .setParameter("password", this.password)
                .setParameter("change_token", this.tokenChangeOnLogin);
    }

    private String getNonce() {
        List<String> characters = Arrays.asList(chars.split(""));
        Collections.shuffle(characters);
        return String.join("" , characters);
    }

    private String getSign(String ts, String token, String nonce) {
        return DigestUtils.md5Hex(ts + token + nonce);
    }

    private HttpActions.HttpGetBuilder addAuthParam(HttpActions.HttpGetBuilder builder) {
        if (!this.enhancedAuth) {
            builder.setParameter("token", this.token);
        } else {
            long ts = System.currentTimeMillis() / 1000;
            String nonce = getNonce();
            builder.setParameter("username", this.userName);
            builder.setParameter("timestamp", ts);
            builder.setParameter("nonce", nonce);
            builder.setParameter("sign", getSign(String.valueOf(ts), token, nonce));
        }
        return builder;
    }

    private HttpActions.HttpGetBuilder getGetProxyGetBuilder(Integer amount, Long expireSeconds) throws Proxy2808Exception {
        String url = API_URL + "/proxy/get";
        HttpActions.HttpGetBuilder builder = new HttpActions.HttpGetBuilder(url)
                .setParameter("amount", amount)
                .setParameter("expire", expireSeconds);
        return addAuthParam(builder);
    }

    /**
     *  获取token
     *
     * @return  Result<String> 获取到的Result含有token
     */

    private Result<String> getToken() {
        if (StringUtils.isAnyBlank(password, userName)) {
            logger.warn("[2808proxy] you need to set userName and password");
            throw new RuntimeException("you need to set userName and password when get token");
        }
        try {
            RequestResult requestResult = this.httpClient.get(getLoginGetBuilder());
            checkRequestResult(requestResult);
            LoginResponse response = convert(requestResult.getContent(), LoginResponse.class);
            if (!response.isSuccess()) {
                logger.warn("[2808proxy] init token fail, msg: {}", response.getMessage());
                throw new RuntimeException("init token fail, msg: " + response.getMessage());
            }
            return new Result<>("", true, response.getLoginResponseData().getToken(), null);
        } catch (Proxy2808Exception e) {
            logger.warn("[2808proxy] init token exception, e: {}", e);
            throw new RuntimeException(e);
        }
    }

    private void checkRequestResult(RequestResult requestResult) throws Proxy2808Exception {
        if (!requestResult.isSuccess()) {
            logger.warn("[2808proxy] request fail");
            throw new Proxy2808Exception("[2808proxy] request fail");
        }
    }


    /**
     *
     * @param amount   代理数量
     * @param expireSeconds   过期时间
     *
     * @return Result  代理列表
     *
     */
    public Result<List<Proxy>> getProxy(Integer amount, Long expireSeconds){
        try {
            HttpActions.HttpGetBuilder getBuilder = getGetProxyGetBuilder(amount, expireSeconds);
            RequestResult requestResult = httpClient.get(getBuilder);
            checkRequestResult(requestResult);
            GetProxyResponse response = convert(requestResult.getContent(), GetProxyResponse.class);
            return new Result<>(response.getMessage(), response.isSuccess(), response.getProxies(), null);
        } catch (Proxy2808Exception e) {
            logger.warn("[2808proxy] get proxy exception", e);
            return new Result<>("get proxy exception", false, Collections.emptyList(), e);
        }
    }


    private HttpActions.HttpGetBuilder getReleaseProxyRequestBuilder(Proxy proxy) throws Proxy2808Exception {
        String url = API_URL + "/proxy/release";
        HttpActions.HttpGetBuilder builder = new HttpActions.HttpGetBuilder(url)
                .setParameter("id", proxy.getId());
        return addAuthParam(builder);
    }

    /**
     * 释放代理
     *
     * @param proxy 需要释放的代理
     * @return Result 释放结果
     *
     */
    public Result<ReleaseProxyResponse> releaseProxy(Proxy proxy) {
        try {
            HttpActions.HttpGetBuilder builder = getReleaseProxyRequestBuilder(proxy);
            RequestResult requestResult = httpClient.get(builder);
            checkRequestResult(requestResult);
            ReleaseProxyResponse response = convert(requestResult.getContent(), ReleaseProxyResponse.class);
            return new Result<>(response.getMessage(), response.isSuccess(), response, null);
        }catch (Proxy2808Exception e) {
            logger.warn("[2808proxy] release proxy {} exception", proxy, e);
            return new Result<>("release proxy exception, proxy: " + proxy, false, null, e);
        }
    }

    /**
     * 批量释放代理
     *
     * @param proxies 需要释放的代理
     * @return Result 釋放代理结果
     *
     */
    public Result<List<Proxy>> releaseProxies(List<Proxy> proxies) {

        if (CollectionUtils.isEmpty(proxies)) {
            logger.warn("[2808proxy] proxies is empty");
            return new Result<>("proxies are empty, no need to release", true, Collections.emptyList(), null );
        }

        Result<ReleaseProxyResponse>  releaseProxyResponseResult;
        List<Proxy> releaseFailedProxies = new ArrayList<>();
        for(Proxy proxy : proxies) {
            logger.info("[2808proxy] releasing proxy: {}", proxy);
            releaseProxyResponseResult = releaseProxy(proxy);
            if (!releaseProxyResponseResult.isSuccess()) {
                logger.warn("[2808proxy] release proxy : {} fail,  {}", proxy, releaseProxyResponseResult.getMessage());
                releaseFailedProxies.add(proxy);
            } else {
                logger.info("[2808proxy] release proxy: {} ok", proxy);
            }
        }
        return new Result<>("", true, releaseFailedProxies, null);
    }


    private HttpActions.HttpGetBuilder getListProxyBuilder() throws Proxy2808Exception {
        String url = API_URL + "/proxy/list";
        HttpActions.HttpGetBuilder builder = new HttpActions.HttpGetBuilder(url);
        return addAuthParam(builder);
    }

    /**
     *  列出所有获取的代理
     *
     * @return Result 代理列表
     *
     */
    public Result<List<Proxy>> listProxy() {
        try {
            HttpActions.HttpGetBuilder builder = getListProxyBuilder();
            RequestResult result = httpClient.get(builder);
            checkRequestResult(result);
            ListProxyResponse response = convert(result.getContent(), ListProxyResponse.class);
            return new Result<>(response.getMessage(), response.isSuccess(), response.getProxies(), null);
        }catch (Proxy2808Exception e) {
            logger.warn("[2808proxy] list proxy exception", e);
            return new Result<>("list proxy exception", false, Collections.emptyList(), null);
        }
    }

    /**
     *  释放所有获取到的代理
     *
     *  @return  Result 释放代理结果， 其中为释放失败的代理
     */
    public Result<List<Proxy>> releaseAll() {
        return releaseProxies(listProxy().getData());
    }

    public static void main(String[] args) throws Exception {
        String username = "xxxx";
        String password = "xxxx";
        Client client = new Client(username, password);
        Result<List<Proxy>> proxies = client.getProxy(1, 10L); // 获取1个代理，过期时间为10秒
        client.releaseProxies(proxies.getData());  // 释放代理
        proxies = client.listProxy(); // 列出所有已经获取到的代理
        client.releaseAll(); // 释放所有已经获取到的代理
    }
}
