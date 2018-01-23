package com.marklogic.support;

import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class HTTPComponentsDigestAuthenticationExampleDeprecated {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {

        //CloseableHttpClient httpclient = HttpClients.createDefault();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHost target = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));

        /*
        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst(new HTTPComponentsDigestAuthenticationExampleNew.PreemptiveAuth())
                .addInterceptorFirst(new HTTPComponentsDigestAuthenticationExampleNew.PersistentDigest())
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        */
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(Configuration.HOSTNAME, Configuration.PORT),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));

        BasicHttpContext localcontext = new BasicHttpContext();
        // Generate DIGEST scheme object, initialize it and stick it to
        // the local execution context
        DigestScheme digestAuth = new DigestScheme();
        localcontext.setAttribute("preemptive-auth", digestAuth);

        // Add as the first request interceptor
        httpclient.addRequestInterceptor(new PreemptiveAuth(), 0);
        // Add as the last response interceptor
        httpclient.addResponseInterceptor(new PersistentDigest());

        HttpHost targetHost = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");

        HttpGet httpget = new HttpGet("/");

        System.out.println("executing request: " + httpget.getRequestLine());
        System.out.println("to target: " + targetHost);

        for (int i = 0; i < 2; i++) {
            HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                LOG.info(EntityUtils.toString(response.getEntity()));
                EntityUtils.consumeQuietly(response.getEntity());

            }
        }

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {
        public void process(final HttpRequest request, final HttpContext context) throws HttpException {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.update(authScheme, creds);
                }
            }
        }
    }

    static class PersistentDigest implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
            if (authState != null) {
                AuthScheme authScheme = authState.getAuthScheme();
                if (authScheme instanceof DigestScheme) {
                    context.setAttribute("preemptive-auth", authScheme);
                }
            }
        }
    }
}