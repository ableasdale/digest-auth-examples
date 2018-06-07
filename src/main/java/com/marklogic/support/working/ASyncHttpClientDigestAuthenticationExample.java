package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Realm;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class ASyncHttpClientDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        Realm realm = new Realm.Builder(Configuration.USERNAME, Configuration.PASSWORD)
                .setScheme(Realm.AuthScheme.DIGEST)
                .build();

        try (AsyncHttpClient asyncHttpClient = asyncHttpClient()) {
            asyncHttpClient
                    .prepareGet(Configuration.URI)
                    .setRealm(realm)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(Response::getResponseBody)
                    .thenAccept(LOG::info)
                    .join();
        } catch (IOException e) {
            LOG.error("IO Exception: ",e);
        }
    }
}
