package com.marklogic.support.working;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.marklogic.support.Configuration;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OkHTTPDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {

        final DigestAuthenticator authenticator = new DigestAuthenticator(new Credentials(Configuration.USERNAME, Configuration.PASSWORD));

        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
        final OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                .build();

        Request request = new Request.Builder()
                .url(Configuration.URI)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                LOG.info(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            LOG.info("Response code: " + response.code());
            LOG.info(response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
