package org.example;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.net.http.*;

public class ProxyClient {
    private static final int PORT = 8080;
    private static final String SERVER_URL = "http://localhost:9090";
    private final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException {
        HttpServer clientServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        clientServer.createContext("/", new ClientHandler());
        clientServer.setExecutor(null);
        clientServer.start();
        System.out.println("Proxy Client started on port " + PORT);
    }

    static class ClientHandler implements HttpHandler {
        private final HttpClient client = HttpClient.newHttpClient();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String targetUrl = SERVER_URL + exchange.getRequestURI().toString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                    .thenAccept(response -> {
                        try {
                            exchange.sendResponseHeaders(response.statusCode(), response.body().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.body());
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}