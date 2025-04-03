package org.example;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.concurrent.*;

public class ProxyServer {
    private static final int PORT = 9090;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new ProxyHandler());
        server.setExecutor(executor);
        server.start();
        System.out.println("Proxy Server started on port " + PORT);
    }

    static class ProxyHandler implements HttpHandler {
        private final HttpClient client = HttpClient.newHttpClient();
        private final BlockingQueue<HttpExchange> requestQueue = new LinkedBlockingQueue<>();

        public ProxyHandler() {
            new Thread(this::processRequests).start();
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                requestQueue.put(exchange);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void processRequests() {
            while (true) {
                try {
                    HttpExchange exchange = requestQueue.take();
                    handleRequest(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRequest(HttpExchange exchange) throws IOException {
            String targetUrl = exchange.getRequestURI().toString();
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
