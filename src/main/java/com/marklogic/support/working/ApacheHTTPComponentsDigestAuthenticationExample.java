package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ApacheHTTPComponentsDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

        HttpHost target = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
                .build();
        BasicHttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("/");

        LOG.info(String.format("Executing request: %s to target: %s", httpGet.getRequestLine(), target));

        HttpResponse response = httpClient.execute(target, httpGet, localContext);
        LOG.info(String.format("Request status line: %s", httpGet.getRequestLine()));
        LOG.info(String.format("Response status line: %s", response.getStatusLine()));
        LOG.info(String.format("Response Body: %s", EntityUtils.toString(response.getEntity())));
        EntityUtils.consumeQuietly(response.getEntity());

        httpClient.close();
    }
}