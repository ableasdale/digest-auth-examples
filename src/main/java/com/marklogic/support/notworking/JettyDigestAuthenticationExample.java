package com.marklogic.support.notworking;

import com.marklogic.support.Configuration;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;


public class JettyDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {

        HttpClient httpClient = new HttpClient();

        AuthenticationStore auth = httpClient.getAuthenticationStore();
        auth.addAuthentication(new DigestAuthentication(new URI(Configuration.URI), "public", Configuration.USERNAME, Configuration.PASSWORD));
        httpClient.setAuthenticationStore(auth);

        //httpClient.setFollowRedirects(false);
        httpClient.start();
        Request r = httpClient.newRequest(Configuration.URI);
        r.method(HttpMethod.GET);
        ContentResponse response = r.send();
        LOG.info(response.getContentAsString());
    }
}
