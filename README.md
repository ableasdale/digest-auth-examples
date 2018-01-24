# digest-auth-examples

Example patterns for using Digest Authentication with a range of Java HTTP Clients.

Tested with HTTP requests to a MarkLogic HTTP Application Server (using MarkLogic 9.0-3 and above) configured with "Digest Authentication" to demonstrate the possible uses for some common Java HTTP Client Libraries with Digest Authentication.

## The pattern

To see how Digest Authentication works between client and server, you can use cURL with the -v switch; below is a simple example of a GET where Digest Authentication is used:

```
curl --anyauth --user q:q -v -i GET 'http://localhost:65534'
```

## Working examples

- Apache HTTP Components (HTTP Client)
- OkHTTP

## Non-working examples (currently broken / WIP)

- Google HTTP Client
- Jersey Client
- Jetty Client
- Netty


```bash
curl --anyauth --user q:q -i -X GET 'http://localhost:65534'
HTTP/1.1 401 Unauthorized
Server: MarkLogic
WWW-Authenticate: Digest realm="public", qop="auth", nonce="35e2a7a98da338:IZsXr6ZTryi4ct7ZtWMC7g==", opaque="23928c8c7e58e1ea"
Content-Type: text/html; charset=utf-8
Content-Length: 209
Connection: Keep-Alive
Keep-Alive: timeout=5

HTTP/1.1 200 OK
Server: MarkLogic
Content-Type: text/plain; charset=UTF-8
Content-Length: 346
Connection: Keep-Alive
Keep-Alive: timeout=5
```

```bash
curl -v --digest --user q:q -i GET 'http://localhost:65534'
* Rebuilt URL to: GET/
* Could not resolve host: GET
* Closing connection 0
curl: (6) Could not resolve host: GET
* Rebuilt URL to: http://localhost:65534/
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 65534 (#1)
* Server auth using Digest with user 'q'
> GET / HTTP/1.1
> Host: localhost:65534
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 401 Unauthorized
HTTP/1.1 401 Unauthorized
< Server: MarkLogic
Server: MarkLogic
< WWW-Authenticate: Digest realm="public", qop="auth", nonce="35e317e3d3a360:1iw2tpWDYJy6dL+tsV+V9g==", opaque="ade472824b98adc8"
WWW-Authenticate: Digest realm="public", qop="auth", nonce="35e317e3d3a360:1iw2tpWDYJy6dL+tsV+V9g==", opaque="ade472824b98adc8"
< Content-Type: text/html; charset=utf-8
Content-Type: text/html; charset=utf-8
< Content-Length: 209
Content-Length: 209
< Connection: Keep-Alive
Connection: Keep-Alive
< Keep-Alive: timeout=5
Keep-Alive: timeout=5

<
* Ignoring the response-body
* Connection #1 to host localhost left intact
* Issue another request to this URL: 'http://localhost:65534/'
* Found bundle for host localhost: 0x7ffc6550bab0 [can pipeline]
* Re-using existing connection! (#1) with host localhost
* Connected to localhost (::1) port 65534 (#1)
* Server auth using Digest with user 'q'
> GET / HTTP/1.1
> Host: localhost:65534
> Authorization: Digest username="q", realm="public", nonce="35e317e3d3a360:1iw2tpWDYJy6dL+tsV+V9g==", uri="/", cnonce="YzIwOTU5YzJmMGJjMTA0NGMxN2YyY2FlY2QzZjBiZjM=", nc=00000001, qop=auth, response="1e1e38d7a1550ec894a77df772b2f8be", opaque="ade472824b98adc8"
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200 OK
HTTP/1.1 200 OK
< Server: MarkLogic
Server: MarkLogic
< Content-Type: text/plain; charset=UTF-8
Content-Type: text/plain; charset=UTF-8
< Content-Length: 346
Content-Length: 346
< Connection: Keep-Alive
Connection: Keep-Alive
< Keep-Alive: timeout=5
Keep-Alive: timeout=5

```