package com.marklogic.support.notworking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class AirliftHttpClientAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {

        /* TODO: It's not clear from looking at the airlift code whether you can add the auth store as you do with Jetty:
            AuthenticationStore auth = httpClient.getAuthenticationStore();
            auth.addAuthentication(new DigestAuthentication(new URI(Configuration.URI), "public", Configuration.USERNAME, Configuration.PASSWORD));
            httpClient.setAuthenticationStore(auth);
        */

        /* Commenting out for now

        HttpClient client = new JettyHttpClient(new HttpClientConfig().setConnectTimeout(new Duration(2.0, TimeUnit.SECONDS)));
        System.setProperty("http-client.log.enabled", "true");

        StatusResponseHandler.StatusResponse response = client.execute(prepareGet().setUri(new URI(Configuration.URI).resolve("/")).build(), createStatusResponseHandler());

        if (response != null) {
            LOG.info("Response Status: " + response.getStatusCode());
            LOG.info("Response Message: " + response.getStatusMessage());
        } */
    }
}
