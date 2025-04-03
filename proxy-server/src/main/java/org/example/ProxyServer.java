package org.example;

import java.io.*;
import java.net.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080); // Listen on port 8080
        System.out.println("Proxy server started on port 8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String url = requestParts[1];

            if (method.equals("GET")) {
                try {
                    URL targetUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader targetIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = targetIn.readLine()) != null) {
                        out.println(line);
                    }
                    targetIn.close();
                } catch (IOException e) {
                    out.println("HTTP/1.0 500 Internal Server Error");
                    out.println();
                }
            } else {
                out.println("HTTP/1.0 501 Not Implemented");
                out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}