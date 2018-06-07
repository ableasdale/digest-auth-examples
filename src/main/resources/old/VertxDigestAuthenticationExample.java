package old;

import com.marklogic.support.Configuration;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.DigestAuthHandler;
import io.vertx.ext.auth.htdigest.HtdigestAuth;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.util.SocketAddressResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.AuthProvider;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class VertxDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static String NONCE = "x";
    private static String REALM = "x";

    public static String processWwwAuth(String header) {
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
        return DigestUtils.md5Hex(HA1+":"+NONCE+":"+HA2);
    }

    public static void main(String[] args) {

        /* TODO - this is a Work in Progress: Vert.X does not appear to handle authentication, so you'd need to:
         1. Get the WWW-Authenticate Header [x]
         2. Parse the values [x]
         3. Complete the challenge
         4. Repeat the request with the Authorization header
         */

        /* Notes on creating the .htdigest file
        // echo -n username:realm:password | md5
        //q:public:q
        // Should look like: q:public:290bb4dc7027538594e1c7e48ccd6d6b
        */


        Vertx vertx = Vertx.vertx();
        HttpClient hc = vertx.createHttpClient();
        HtdigestAuth authProvider = HtdigestAuth.create(vertx);  // , ".htdigest"

        //io.vertx.ext.auth.htdigest.HtdigestAuth authProvider = null;
        // AuthHandler basicAuthHandler = DigestAuthHandler.create(authProvider);


        /*
LOG.info("***********************   attempt1:");


        final HttpClientRequest httpClientRequest = hc.request(HttpMethod.GET, Configuration.PORT, Configuration.HOSTNAME, "/");
        httpClientRequest.handler(new AuthHttpHandler(this, responseHandler));
        httpClientRequest.putHeader(HttpHeaders.ACCEPT, Format.JSON.getDefaultMimetype());
        if (authorization == null && AuthScheme.Type.BASIC == realm.getSchemeType()) {
            authorize(uri, AuthSchemeFactory.newScheme(realm));
        }
        if (authorization != null) {
            httpClientRequest.putHeader(HttpHeaders.AUTHORIZATION, authorization);
        }
        if (body != null) {
            httpClientRequest.putHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength))
                    .putHeader(HttpHeaders.CONTENT_TYPE, contentType)
                    .write(body);
        }
        httpClientRequest.exceptionHandler(exception -> {
            responseHandler.handle(new ErrorResponse(exception));
        }).end();




//    SocketAddressResolver.Async async = context.async();
/*
    HttpClientRequest req = httpClient.get(TEST_PORT, TEST_HOST, "/test", response -> {
    	context.assertEquals(response.statusCode(), 401);
    	String header = response.getHeader("WWW-Authenticate");
        context.assertNotNull(header);
     	context.assertEquals("Basic realm=\"vertx-web\"", header);
        async.complete();
    });
    req.putHeader("Authorization", DUMMY_BASIC_AUTH);
    req.end();
 */


LOG.info("***********************   attempt2:");
        hc.get(Configuration.PORT, Configuration.HOSTNAME, "/", response -> {


            /*
            *
            * router.get("/").handler((RoutingContext ctx) {
  ctx.response().putHeader("content-type", "text/html").end("Hello<br><a href=\"/protected/somepage\">Protected by Github</a>");
});
            * */



            // Should get an HTTP 401 to start with; if so:
            LOG.info("Received response with status code " + response.statusCode());
            // 1. Look for WWW-Authenticate Header:
            String challengeResponse = "";
            for (Map.Entry<String, String> entry : response.headers())
            {
                if("WWW-Authenticate".equals(entry.getKey())){
                    LOG.info("Found "+ entry.getKey() + " header.");
                    challengeResponse = processWwwAuth(entry.getValue());
                }
                LOG.info(entry.getKey() + " / " + entry.getValue());
            }

            // WWW-Authenticate: Digest realm="public", qop="auth", nonce="364bf8703cac04:Q0LSETmLnYgcvcCGt3q5uQ==", opaque="a46660df268c3b57"
            //Authorization: Digest username="q", realm="public", nonce="364bf8703cac04:Q0LSETmLnYgcvcCGt3q5uQ==", opaque="a46660df268c3b57", algorithm="MD5", uri="/", qop="auth", nc="00000001", cnonce="48f73e5dc5db1917", response="fb22d5eeb2b2cc3dae8a9550aa79b01a"
            // Authorization: Digest username="q", realm="public", nonce="364bf81d7dc322:mgrRA5INhaI0uqXSorlvTw==", opaque="d97510db6825affd", algorithm="MD5", uri="/", qop="auth", nc="00000001", cnonce="433a540f9520a217", response="d48c0fd9967d47e30fffee3ee60dc356"



/*
            LOG.info("Content-Type: "+ response.getHeader("Content-Type"));
            LOG.info("Content-Length: "+ response.getHeader("Content-Length"));
            LOG.info("auth: "+ response.getHeader("WWW-Authenticate"));
*/


            JsonObject authInfo = new JsonObject()
                    .put("username", "q")
                    .put("realm", "public")
                    .put("nonce", NONCE)
                    .put("method", "GET")
                    .put("uri", "/")
                    .put("response", challengeResponse);

            LOG.info("ABOUT TO AUTH");
            LOG.info(authInfo.toString());

            authProvider.authenticate(authInfo, res -> {
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


/*

            response.bodyHandler( body -> {
                LOG.info( body.toString("UTF-8") );
            });
*/
        });
       // hc.putHeader(HttpHeaders.Names.AUTHORIZATION, "Basic "+base64key);
        //.putHeader(HttpHeaders.AUTHORIZATION, "do do do da da da").end();
        // TODO - try again with the new header ? hc.get();

        //final HttpClientRequest req = hc.get(Configuration.PORT, Configuration.HOSTNAME, "/");
        //((HttpClientRequest) req).handler(authProvider);




    /* Exception {
    // Attempt to testGet a private url
    final HttpClientRequest successfulRequest = client.get(8080, "localhost", "/private/success.html");
    successfulRequest.handler(// redirect to auth handler
    expectAndHandleRedirect(client, extractCookie(), // redirect to auth response handler
    expectAndHandleRedirect(client,  clientResponse -> {
    }, // redirect to original url if authorized
    expectAndHandleRedirect(client,  httpClientResponse -> {
    }, finalResponseHandler)))).end();
}*/


    }
}
