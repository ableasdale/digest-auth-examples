package old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RollYerOwn {

    public static void main(String[] args) throws Exception {

                URL url = new URL(Configuration.URI);

                URLConnection uc = url.openConnection();
                String userPass = Configuration.USERNAME + ":" + Configuration.PASSWORD;
                String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userPass.getBytes());
                uc.setRequestProperty("Authorization", basicAuth);

                InputStream in = uc.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                for (String line; (line = reader.readLine()) != null;) {
                    System.out.println(line);
                }
    }
}
