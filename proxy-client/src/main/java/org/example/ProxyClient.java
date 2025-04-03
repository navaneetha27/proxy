package org.example;

import java.io.*;
import java.io.IOException;
import java.net.*;

public class ProxyClient {
    public static void main(String[] args) throws IOException {
        String proxyHost = "localhost";
        int proxyPort = 8080;

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));

        URL url = new URL("http://example.com"); // Replace with the desired URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }
}