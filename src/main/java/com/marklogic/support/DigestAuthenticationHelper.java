package com.marklogic.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class DigestAuthenticationHelper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String processWwwAuthHeader(String header) {

        header = header.replaceAll("\\p{Cntrl}", "");
        String[] authBits = header.split(", ");

        /*for (String part : authBits) {
            LOG.debug(part);
        }*/

        String REALM = authBits[0].substring(authBits[0].indexOf("=") + 1).replace("\"", "");
        String NONCE = authBits[2].substring(authBits[2].indexOf("=") + 1).replace("\"", "");
        String OPAQUE = authBits[3].substring(authBits[3].indexOf("=") + 1).replace("\"", "");

        String HA1 = DigestUtils.md5Hex(Configuration.USERNAME + ":" + REALM + ":" + Configuration.PASSWORD);
        LOG.debug("HA1 Hash: " + HA1);
        // TODO - A lot of this is hardcoded for now
        String HA2 = DigestUtils.md5Hex("GET:/");
        LOG.debug("HA2 Hash: " + HA2);
        LOG.debug("Nonce: "+ NONCE);
        LOG.debug("Opaque: "+ OPAQUE);
        /*
        If the qop directive's value is "auth" or "auth-int", then compute the response as follows:
        response=MD5(HA1:nonce:nonceCount:cnonce:qop:HA2)
        See: https://en.wikipedia.org/wiki/Digest_access_authentication
         */

        String RESPONSE = DigestUtils.md5Hex(HA1 + ":" + NONCE + ":00000001:0a4f113b:auth:" + HA2);
        LOG.info("Challenge Response: "+RESPONSE);
        //Authorization: Digest username="q", realm="public", nonce="364bf81d7dc322:mgrRA5INhaI0uqXSorlvTw==", opaque="d97510db6825affd", algorithm="MD5", uri="/", qop="auth", nc="00000001", cnonce="433a540f9520a217", response="d48c0fd9967d47e30fffee3ee60dc356"
        StringBuilder stb = new StringBuilder();
        stb.append("Digest username=\"q\", realm=\"public\", nonce=\"").append(NONCE).append("\", ").append(authBits[3]).append(", ").append("algorithm=\"MD5\", uri=\"/\", qop=\"auth\", nc=\"00000001\", cnonce=\"0a4f113b\", response=\"").append(RESPONSE).append("\"");
        //.append(OPAQUE);//.append(", ").append(OPAQUE).append(", algorithm=\"MD5\", uri=\"/\", qop=\"auth\", nc=\"00000001\", cnonce=\"0a4f113b\", response=\"").append(RESPONSE).append("\"");
        LOG.debug("This is the Authorization header: " + stb.toString());
        return stb.toString();
    }
}
