package com.marklogic.support.notworking;

import com.marklogic.support.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.ws.Response;
import java.lang.invoke.MethodHandles;

public class JerseyDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        //Client client = ClientBuilder.newBuilder().build();
        //Client client = Client.create();
        //client.addFilter(new HTTPDigestAuthFilter(username, password));

        //Client client = ClientBuilder.newClient();
        //WebTarget target = client.target(Configuration.URI);
        //HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest(Configuration.USERNAME, Configuration.PASSWORD);



        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        client.register(HttpAuthenticationFeature.digest(Configuration.USERNAME, Configuration.PASSWORD));

        WebTarget target = client.target(Configuration.URI);
        String s = target.request().get(String.class);
        LOG.info(s);



//        JerseyClientConfiguration configuration = config.httpClient;
//        HttpHost target = HttpHost.create(config.apiUrl);
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope(target), new UsernamePasswordCredentials(config.user, config.password));
//
//        final Client httpClient = new JerseyClientBuilder(environment)
//                .using(configuration)
//                .using(credsProvider)
//                .build("my-client");


        //target.register(new HttpDigestAuthFilter(Configuration.USERNAME, Configuration.PASSWORD));

//
//        WebTarget res = clientSetup.getRestClient()
//                .pathResource( "system/index/rebuild/" + result.getProperties()
//                        .get( "jobId" ).toString() )
//                .getTarget();
//
//        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
//                .credentials( "superuser", "superpassword" ).build();

//        result = res.register( feature ).request().get( ApiResponse.class );
//
//        assertNotNull( result );
//        assertEquals(status,result.getProperties().get("jobId").toString());
//
//
//
//        Response response = (Response) client.target(Configuration.URI).request().buildGet();
//        LOG.info(response.toString());
    }
}
