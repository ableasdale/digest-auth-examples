package old;

import com.marklogic.support.Configuration;
import com.marklogic.support.DigestAuthenticationHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class RollYerOwn {

    public static void main(String[] args) throws Exception {


        URL url = new URL(Configuration.URI);
        URLConnection uc = url.openConnection();
        //String userPass = Configuration.USERNAME + ":" + Configuration.PASSWORD;
        //String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userPass.getBytes());
        //uc.setRequestProperty("Authorization", basicAuth);

        /* 1. Look at the headers

        for (Map.Entry<String, List<String>> entry : uc.getHeaderFields().entrySet()) {
            System.out.println("Key : " + entry.getKey() +
                    " ,Value : " + entry.getValue());
        } */

        String authHeader = DigestAuthenticationHelper.processWwwAuthHeader(uc.getHeaderField("WWW-Authenticate"));
        uc.setRequestProperty("Authorization", authHeader);
        //uc.
        //uc = url.openConnection();

        InputStream in = uc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        for (String line; (line = reader.readLine()) != null; ) {
            System.out.println(line);
        }




/*
                URL oracle = new URL("http://www.oracle.com/");
                URLConnection yc = oracle.openConnection();
                BufferedReader bin = new BufferedReader(new InputStreamReader(
                        yc.getInputStream()));
                String inputLine;
                while ((inputLine = bin.readLine()) != null)
                    System.out.println(inputLine);
                bin.close();
            } */

    }
}
