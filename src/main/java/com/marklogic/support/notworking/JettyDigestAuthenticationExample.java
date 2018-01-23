package com.marklogic.support.notworking;

import java.net.URI;
import java.net.URISyntaxException;

import com.marklogic.support.Configuration;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.http.HttpMethod;


public class JettyDigestAuthenticationExample {

    public static void main(String[] args) throws Exception {



            HttpClient httpClient = new HttpClient();
            AuthenticationStore auth = httpClient.getAuthenticationStore();

            auth.addAuthentication(new DigestAuthentication(new URI(Configuration.URI),"public", Configuration.USERNAME, Configuration.PASSWORD));

            httpClient.setFollowRedirects(false);
            httpClient.start();
            Request r = httpClient.newRequest(Configuration.URI);
            r.method(HttpMethod.GET);
            ContentResponse response = r.send();
            System.out.println(response.getContentAsString());
    }
}
