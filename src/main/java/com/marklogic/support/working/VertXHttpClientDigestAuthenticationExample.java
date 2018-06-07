package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import com.marklogic.support.DigestAuthenticationHelper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class VertXHttpClientDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        client.get(Configuration.PORT, Configuration.HOSTNAME, "/")
                .send(authHandler -> {
                    // Authentication 401 challenge.
                    if (authHandler.succeeded() && 401 == authHandler.result().statusCode()) {
                        LOG.debug("WWW-Authenticate Challenge: " + authHandler.result().getHeader("WWW-Authenticate"));
                        String authResponseHeader = DigestAuthenticationHelper.processWwwAuthHeader(authHandler.result().getHeader("WWW-Authenticate"));
                        client.get(Configuration.PORT, Configuration.HOSTNAME, "/")
                                .putHeader("Authorization", authResponseHeader)
                                .send(handler -> {

                                    if (handler.succeeded() && 200 == handler.result().statusCode()) {
                                        LOG.info(handler.result().body().toString());
                                    } else {
                                        LOG.error("Unable to Authenticate: " + handler.result().statusCode());
                                    }
                                });

                    } else { // Authentication failed
                        LOG.error("Unable to Authenticate: " + authHandler.result().statusCode());
                    }
                });
    }
}
