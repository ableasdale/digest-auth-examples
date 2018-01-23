package com.marklogic.support;

import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class HTTPComponentsDigestAuthenticationExampleNew {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws IOException, MalformedChallengeException {

        HttpHost target = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));

        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst(new PreemptiveAuth())
                .addInterceptorFirst(new PersistentDigest())
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {

//            // Create AuthCache instance
//            AuthCache authCache = new BasicAuthCache();
//            // Generate DIGEST scheme object, initialize it and add it to the local
//            // auth cache
//            DigestScheme digestAuth = new DigestScheme();
//            // Suppose we already know the realm name
//            digestAuth.overrideParamter("realm", "public");
//            // Suppose we already know the expected nonce value
//            //digestAuth.overrideParamter("nonce", "12345");
//            authCache.put(target, digestAuth);

            // Add AuthCache to the execution context
            DigestScheme digestAuth = new DigestScheme();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAttribute("preemptive-auth", digestAuth);
            //localContext.setAuthCache(authCache);

            /* authCache.put(targetHost, digestScheme);

context.setCredentialsProvider(credsProvider);
context.setAuthCache(authCache); */


            HttpGet httpget = new HttpGet("/");

            System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
            for (int i = 0; i < 3; i++) {
                CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
                try {
                    System.out.println("----------------------------------------");
                    LOG.info("Response Status: " + response.getStatusLine());
                    //System.out.println(EntityUtils.toString(response.getEntity()));
                } finally {
                    response.close();
                }
            }


        } finally {
            httpclient.close();
        }

    }
    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(
                final HttpRequest request,
                final HttpContext context) throws HttpException, IOException {
            LOG.info("Preemptive Auth");

            AuthState authState = (AuthState) context.getAttribute(
                    HttpClientContext.TARGET_AUTH_STATE);

            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                LOG.info("Authstate is null!");
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        HttpClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        HttpClientContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(),
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.update(authScheme, creds);
                }
            }

        }

    }

    static class PersistentDigest implements HttpResponseInterceptor {

        public void process(
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            LOG.info("Persistent Digest");
            AuthState authState = (AuthState) context.getAttribute(
                    HttpClientContext.TARGET_AUTH_STATE);
            if (authState != null) {
                LOG.info("Authstate is not null!");
                AuthScheme authScheme = authState.getAuthScheme();

                if(response.containsHeader("WWW-Authenticate")){
                    LOG.info("We have an auth header!");
                    for (HeaderElement element : response.getFirstHeader("WWW-Authenticate").getElements()) {
                        LOG.info(element.getName() + " | " + element.getValue());
                    }
                    //authScheme.processChallenge(response.getFirstHeader("WWW-Authenticate"));
                }


                // Stick the auth scheme to the local context, so
                // we could try to authenticate subsequent requests
                // preemptively
                if (authScheme instanceof DigestScheme) {
                    context.setAttribute("preemptive-auth", authScheme);
                }
            }
        }
    }
}
