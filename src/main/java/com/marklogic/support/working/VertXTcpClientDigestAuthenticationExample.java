package com.marklogic.support.working;

import com.marklogic.support.Configuration;
import com.marklogic.support.DigestAuthenticationHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VertXTcpClientDigestAuthenticationExample {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();

        client.connect(Configuration.PORT, "localhost",
                new Handler<AsyncResult<NetSocket>>() {

                    @Override
                    public void handle(AsyncResult<NetSocket> result) {
                        NetSocket socket = result.result();

                        socket.write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");

                        socket.handler(new Handler<Buffer>() {
                            @Override
                            public void handle(Buffer buffer) {
                                System.out.println("Received data: " + buffer.length());

                                String auth = (buffer.getString(0, buffer.length()));

                                List<String> result = Arrays.stream(auth.split(System.lineSeparator()))
                                        .filter(line -> line.startsWith("WWW-Authenticate"))
                                        .collect(Collectors.toList());

                                String authHeader = DigestAuthenticationHelper.processWwwAuthHeader(result.get(0));

                                socket.write("GET / HTTP/1.1\r\nHost: localhost\r\nAuthorization: " + authHeader + "\r\n\n");
                                socket.handler(new Handler<Buffer>() {
                                    @Override
                                    public void handle(Buffer buffer) {
                                        System.out.println("Received data: " + buffer.length());

                                        LOG.info("And:" + buffer.getString(0, buffer.length()));
                                    }
                                });
                            }

                        });
                    }
                });
        client.close();
    }
}
