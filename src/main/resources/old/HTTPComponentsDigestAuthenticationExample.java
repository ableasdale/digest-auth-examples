package old;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class HTTPComponentsDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws IOException, MalformedChallengeException {


        HttpHost target = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate DIGEST scheme object, initialize it and add it to the local
            // auth cache
            DigestScheme digestAuth = new DigestScheme();
            // Suppose we already know the realm name
            digestAuth.overrideParamter("realm", "public");
            // Suppose we already know the expected nonce value
            digestAuth.overrideParamter("nonce", "12345");
            authCache.put(target, digestAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            /* authCache.put(targetHost, digestScheme);

context.setCredentialsProvider(credsProvider);
context.setAuthCache(authCache); */

            HttpGet httpget = new HttpGet(Configuration.URI);

            System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);

                CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
            digestAuth.processChallenge(response.getFirstHeader("WWW-Authenticate"));
            response = httpclient.execute(target, httpget, localContext);
                try {
                    for (Header h : response.getAllHeaders())
                    {
                        LOG.info(h.getName() +" | "+ h.getValue());
                    }
                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    System.out.println(EntityUtils.toString(response.getEntity()));
                } finally {
                    response.close();
                }


        } finally {
            httpclient.close();
        }



/*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(Configuration.URI);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            LOG.info(response.toString());
        } finally {
            response.close();
        } */

    }
}
