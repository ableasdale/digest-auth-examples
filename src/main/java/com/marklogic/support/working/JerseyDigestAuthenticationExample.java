package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class JerseyDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {

        // Intercept JUL so we can get some log output
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("global").setLevel(Level.ALL);

        // Configure Jersey2 Client with Digest Authentication
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(HttpAuthenticationFeature.digest(Configuration.USERNAME, Configuration.PASSWORD));

        // Capture logged output from Jersey 2
        clientConfig.register(new LoggingFeature(java.util.logging.Logger.getLogger("Jersey HTTP Client"), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192));
        Client client = ClientBuilder.newClient(clientConfig);

        // Prepare request
        WebTarget target = client.target(Configuration.URI);

        // Get request details
        Invocation req = target.request().buildGet();
        req.invoke();

       // target.getUriBuilder().

        // Get the HTTP response:
        Response r = target.request().get();

        LOG.info(String.format("Response Status: %d", r.getStatus()));
        Map m = r.getStringHeaders();
        for (Object k : m.keySet()) {
            LOG.info(String.format("Response Header: %s | %s", k.toString(), r.getHeaderString(k.toString())));
        }
        
        LOG.info(String.format("Response: %s", target.request().get(String.class)));
    }
}
