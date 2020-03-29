package requests;

import static utils.HttpUtils.printResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class PostRequest {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URL = "https://httpbin.org/post";
    private static final String POST_PARAMS = "userName=johny";

    public static void sendPost(String hostname, int port) throws IOException {

        URL obj = new URL(URL);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);

        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            printResponse(con);
        } else {
            System.out.println("POST request not worked");
        }
    }
}

