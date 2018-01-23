package old;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
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


public class HTTPComponentsExampleTwo {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args) throws IOException {

        /*
        String urlStr = “http://example.com:8080/abc/”;
        String host = “example.com”;
        String realm = “ExampleRealm”;
        String userName = “user”;
        String password = “password”;
        */

        HttpHost target = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        AuthCache authCache = new BasicAuthCache();
        // Generate DIGEST scheme object, initialize it and add it to the local
        // auth cache
        DigestScheme digestAuth = new DigestScheme();
        // Suppose we already know the realm name
       //digestAuth.overrideParamter("realm", "public");
        // Suppose we already know the expected nonce value
       // digestAuth.overrideParamter("nonce", "12345");
        authCache.put(target, digestAuth);

        // Add AuthCache to the execution context
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        HttpGet httpget = new HttpGet(Configuration.URI);

        CloseableHttpResponse response = client.execute(target, httpget, localContext);

        Header wwAuthHeader = response.getFirstHeader("WWW-Authenticate");
        for (HeaderElement element : wwAuthHeader.getElements()) {
            if(element.getName().contains("Digest realm")){
                LOG.info("put the realm in");
                digestAuth.overrideParamter("realm", "public");
            }
            LOG.info(element.getName() + " | " + element.getValue());
            digestAuth.overrideParamter(element.getName(), element.getValue());
            //System.out.println(element.getName() + " : " + element.getValue());

        }
        authCache.put(target, digestAuth);
        localContext.setAuthCache(authCache);

        response = client.execute(target, httpget, localContext);

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

        client.close();

    }
}