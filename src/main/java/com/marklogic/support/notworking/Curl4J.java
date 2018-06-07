package com.marklogic.support.notworking;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static org.toilelibre.libe.curl.Curl.curl;

public class Curl4J {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {
        HttpResponse h = curl("--digest -U q:q -X GET 'http://localhost:65534'");
        LOG.info(String.format("Response Body: %s", EntityUtils.toString(h.getEntity())));

    }
}
