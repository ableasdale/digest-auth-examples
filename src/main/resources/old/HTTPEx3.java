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

public class HTTPEx3 {
    public static void main(String[] args) throws Exception {
        HttpHost targetHost = new HttpHost(Configuration.HOSTNAME, Configuration.PORT, "http");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();




            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(Configuration.USERNAME, Configuration.PASSWORD));
            AuthCache authCache = new BasicAuthCache();
            DigestScheme digestScheme = new DigestScheme();
            authCache.put(targetHost, digestScheme);

            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);



        HttpGet httpget = new HttpGet(Configuration.URI);

        CloseableHttpResponse response = httpClient.execute(targetHost, httpget, context);

        try {
            System.out.println(response.getStatusLine());
            System.out.println(EntityUtils.toString(response.getEntity()));
            //ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent());
            //fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } finally {
            response.close();
        }
    }
}
