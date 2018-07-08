package com.proxy2808.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

public class HttpActions {

    public static HttpGetBuilder get(String url) throws Proxy2808Exception {
        return new HttpGetBuilder(url);
    }

    public static HttpPostBuilder post(String url) throws Proxy2808Exception {
        return new HttpPostBuilder(url);
    }

    public static class HttpGetBuilder {
        private URIBuilder uriBuilder;
        private HttpGet action;

        public HttpGetBuilder(String strUrl) throws Proxy2808Exception {
            try {
                uriBuilder = new URIBuilder(strUrl);
            } catch (URISyntaxException e) {
                throw new Proxy2808Exception(e);
            }
            action = new HttpGet();
        }

        public HttpGetBuilder setParameter(String param, Object value) {
            uriBuilder.setParameter(param, String.valueOf(value));
            return this;
        }

        public HttpGetBuilder setHeader(String name, String value) {
            action.setHeader(name, value);
            return this;
        }

        public HttpGetBuilder setConfig(RequestConfig config) {
            action.setConfig(config);
            return this;
        }

        public HttpGet build() {
            try {
                action.setURI(uriBuilder.build());
                return action;
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class HttpPostBuilder {
        private HttpPost action;
        private List<NameValuePair> parameters;
        private StringEntity entity;
        private Charset charset;

        public HttpPostBuilder(String strUrl) throws Proxy2808Exception {
            try {
                new URI(strUrl);
            } catch (URISyntaxException e) {
                throw new Proxy2808Exception(e);
            }
            action = new HttpPost(strUrl);
            parameters = new ArrayList<>();
        }

        public HttpPostBuilder setParameter(String param, Object value) {
            parameters.add(new BasicNameValuePair(param, String.valueOf(value)));
            return this;
        }

        public HttpPostBuilder setParameters(List<NameValuePair> parameters) {
            this.parameters = parameters;
            return this;
        }

        public HttpPostBuilder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public HttpPostBuilder setBody(String body) {
            try {
                entity = new StringEntity(body);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public HttpPostBuilder setHeader(String name, String value) {
            action.setHeader(name, value);
            return this;
        }

        public HttpPostBuilder setConfig(RequestConfig config) {
            action.setConfig(config);
            return this;
        }

        public HttpPost build() {
            if (entity != null) {
                action.setEntity(entity);
            } else {
                if (charset == null) {
                    action.setEntity(toEntity(parameters));
                } else {
                    action.setEntity(new UrlEncodedFormEntity(parameters, charset));
                }
            }
            return action;
        }

        private static UrlEncodedFormEntity toEntity(List<NameValuePair> parameters) {
            try {
                return new UrlEncodedFormEntity(parameters);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

