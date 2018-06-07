package old;

import com.marklogic.support.Configuration;
import io.netty.handler.codec.http.HttpHeaders;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.htdigest.HtdigestAuth;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class VertXHttpClientDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static String NONCE = "x";
    private static String REALM = "x";
    private static JsonObject JSON = null;

    public static JsonObject getJSON() {
        return JSON;
    }

    public static void setJSON(JsonObject newJSON) {
        LOG.info("Setting: "+newJSON.toString());
        JSON = newJSON;
    }

    public static JsonObject processWwwAuth(String header) {
        LOG.info("Processing the WWW-Authenticate Header: " + header);
        //String wwwAuth = response.getHeader("WWW-Authenticate");
        String[] authBits = header.split(", ");
        for (String part : authBits) {
            LOG.info(part);
        }


        REALM = authBits[0].substring(authBits[0].indexOf("=") + 1).replace("\"", "");
        LOG.debug("REALM IS: " + REALM);

        NONCE = authBits[2].substring(authBits[2].indexOf("=") + 1).replace("\"", "");
        LOG.debug("SELECTED NONCE:" + NONCE);

        String HA1 = DigestUtils.md5Hex(Configuration.USERNAME+":"+REALM+":"+Configuration.PASSWORD);
        LOG.debug("HA1: "+ HA1);

        /*

        HA2	= md5(method:uri)
	= md5(GET:/)
	= 71998c64aea37ae77020c49c00f73fa8
	*/
        // TODO - this bit is hardcoded for now

        String HA2 = DigestUtils.md5Hex("GET:/");
        LOG.info("HA2: "+ HA2);

	/*
Response	= md5(ha1:nonce:ha2)
	= md5(290bb4dc7027538594e1c7e48ccd6d6b:364bd0ea68b2fc:/U4HOwb/6fP95nLFZ0FwvQ==:71998c64aea37ae77020c49c00f73fa8)
	= fd83b77b91b4909a92b53ca97079f8ad
         */
        return new JsonObject()
                .put("username", "q")
                .put("realm", authBits[0].substring(authBits[0].indexOf("=") + 1).replace("\"", ""))
                .put("nonce", authBits[2].substring(authBits[2].indexOf("=") + 1).replace("\"", ""))
                .put("method", "GET")
                .put("uri", "/")
                .put("response", DigestUtils.md5Hex(HA1+":"+NONCE+":"+HA2));
    }


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        //HttpClient hc = vertx.createHttpClient();
        HtdigestAuth authProvider = HtdigestAuth.create(vertx);  // , ".htdigest"
        HttpClient client = vertx.createHttpClient();


        //.setSSL(true)
               // .setTrustAll(true) //You may not want to trust them all
        //        .setHost("api.myawesomeapi.com")
          //      .setPort(443);
        /*

        MeshBinaryResponseHandler handler = new MeshBinaryResponseHandler(GET, uri);
		HttpClientRequest request = getClient().request(GET, uri, handler);
		authentication.addAuthenticationInformation(request).subscribe(() -> {
			request.headers().add("Accept", "application/json");
		});
         */


        HttpClientRequest clientRequest = client.get(Configuration.PORT, Configuration.HOSTNAME, "/", new Handler<HttpClientResponse>() {
            public void handle(final HttpClientResponse response) {
                LOG.info("RESPONSE STATUS CODE IS: "+response.statusCode());
                if (response.statusCode() == 200) {
                    LOG.info("*** 200!");
                    // It worked !
                } else if (response.statusCode() == 401) {
                    LOG.info("*** Need to deal with WWW_Auth here");
                    setJSON(processWwwAuth(response.getHeader("WWW-Authenticate")));
                    //clientRequest.putHeader();
                } else {
                    LOG.info("***** something else");
                    // Oops
                }
            }
        });

        LOG.info("REALMREALMREALM"+getJSON());

        // Authentication bit



        LOG.info("ABOUT TO AUTH");
        LOG.info(getJSON().toString());

        authProvider.authenticate(JSON, res -> {
            if (res.succeeded()) {
                LOG.info("We have authenticated");
                User user = res.result();
                LOG.info("res:"+res.result());
            } else {
                res.cause().printStackTrace();
                LOG.error("*** WE didn't authenticate!");
            }
        });
        LOG.info("DONE AUTH");


        clientRequest.putHeader(HttpHeaders.Names.AUTHORIZATION, "Basic "+123);
        clientRequest.end();
    }
}
