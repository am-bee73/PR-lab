package requests;

import static utils.HttpUtils.printResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class GetRequest {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URL = "http://webcode.me";

    public static void sendGet(String hostname, int port) throws IOException {

        URL obj = new URL(URL);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            printResponse(con);
        } else {
            System.out.println("GET request not worked");
        }
    }
}

