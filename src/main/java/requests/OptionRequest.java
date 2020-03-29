package requests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class OptionRequest {

    private static final String URL = "https://httpbin.org/post";

    public static void sendOptions(String hostname, int port) throws IOException {
        URL obj = new URL(URL);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);

        con.setRequestMethod("OPTIONS");
        System.out.println(con.getHeaderField("Allow"));
    }
}

