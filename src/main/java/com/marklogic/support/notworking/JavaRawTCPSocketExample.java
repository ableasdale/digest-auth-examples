package com.marklogic.support.notworking;

import com.marklogic.support.Configuration;
import com.marklogic.support.DigestAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaRawTCPSocketExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public static String ReadStringFromBufferedReader(BufferedReader inFromServer) throws Exception {
        StringBuilder sb = new StringBuilder();
        String line = "";
        while (inFromServer.ready() && (line = inFromServer.readLine()) != null) {
            sb.append(line + "\r\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {

        Socket clientSocket = new Socket("localhost", Configuration.PORT);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        /* 1. Get the Digest Auth Challenge from Server */
        outToServer.writeBytes("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
        //String response = inFromServer.lines().collect(Collectors.joining("\n"));
        String response = ReadStringFromBufferedReader(inFromServer);
        //inFromServer.reset();
        LOG.info(response);

        /* 2. Respond to Challenge and get resource data */
        List<String> result = Arrays.stream(response.split(System.lineSeparator()))
                .filter(line -> line.startsWith("WWW-Authenticate"))
                .collect(Collectors.toList());
        LOG.info("WWW-Authenticate Header: "+result.get(0));
        String authHeader = DigestAuthenticationHelper.processWwwAuthHeader(result.get(0));

        LOG.info("*AUTH HEADER "+ authHeader);
        outToServer.writeBytes("GET / HTTP/1.1\r\nHost: localhost\r\nAuthorization: " + authHeader + "\r\n\n");
        response = ReadStringFromBufferedReader(inFromServer);
        LOG.info(response);


        clientSocket.close();

    }
}
