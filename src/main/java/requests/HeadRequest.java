package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HeadRequest {

    public static final String URL = "http://webcode.me";

    public static void sendHead(String hostname, int port) throws IOException, InterruptedException {

        System.setProperty("http.proxyHost", hostname);
        System.setProperty("http.proxyPort", String.valueOf(port));

        HttpClient client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder(URI.create(URL)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        HttpHeaders headers = response.headers();

        headers.map().forEach((key, values) -> {
            System.out.printf("%s: %s%n", key, values);
        });
    }
}

