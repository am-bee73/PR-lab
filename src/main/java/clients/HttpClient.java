package clients;

import requests.GetRequest;
import requests.HeadRequest;
import requests.OptionRequest;
import requests.PostRequest;

import java.io.IOException;

public class HttpClient {

    private static String hostname = "65.215.2.68";
    private static int port = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("GET request:");
        GetRequest.sendGet(hostname, port);
        System.out.println(System.lineSeparator());
        System.out.println("POST request:");
        PostRequest.sendPost(hostname, port);
        System.out.println(System.lineSeparator());
        System.out.println("HEAD request:");
        HeadRequest.sendHead(hostname, port);
        System.out.println(System.lineSeparator());
        System.out.println("OPTIONS request:");
        OptionRequest.sendOptions(hostname, port);
    }
}

