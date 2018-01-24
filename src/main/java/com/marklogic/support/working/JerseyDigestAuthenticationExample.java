package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.lang.invoke.MethodHandles;

public class JerseyDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(HttpAuthenticationFeature.digest(Configuration.USERNAME, Configuration.PASSWORD));
        clientConfig.register(new LoggingFeature());
        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget target = client.target(Configuration.URI);
        String s = target.request().get(String.class);
        LOG.info(s);
    }
}
