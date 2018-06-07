package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import com.marklogic.support.DigestAuthenticationHelper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class VertXWebClientDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(vertx);

        client
                .get(Configuration.PORT, Configuration.HOSTNAME, "/")
                .send(ar -> {
                    if (ar.succeeded()) {
                        // Obtain response
                        HttpResponse<Buffer> response = ar.result();
                        LOG.info("Received response with status code: " + response.statusCode());
                        String challengeResponse = DigestAuthenticationHelper.processWwwAuthHeader(response.getHeader("WWW-Authenticate"));
                        client.get(Configuration.PORT, Configuration.HOSTNAME, "/")
                                .putHeader("Authorization", challengeResponse).send(ar2 -> {
                            if (ar2.succeeded()) {
                                HttpResponse<Buffer> response2 = ar2.result();
                                LOG.info("Received response with status code " + response2.statusCode());
                                LOG.info(response2.body().toString());
                                // Ok
                            }
                        });
                    } else {
                        LOG.error("Something went wrong " + ar.cause().getMessage());
                    }
                });

    }
}
