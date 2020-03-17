import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClientUtm {

    private SSLSocket socket;

    private List<String> imageList = new ArrayList<>();

    private InetAddress address;

    private int port;

    public ClientUtm(InetAddress address, int port) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(address, port);
        socket.startHandshake();
        this.address = address;
        this.port = port;
    }

    public void getRequest() throws IOException {
        //Instantiates a new PrintWriter passing in the sockets output stream
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //Creates a BufferedReader that contains the server response
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        out.println("GET / HTTP/1.1");
        out.println("Host: " + socket.getInetAddress().getHostName());
        out.println("Cache-Control: no-cache");
        out.println("Server: Apache");
        out.println("Vary: Accept-Encoding,Cookie");
        out.println("Cache-Control: max-age=3, must-revalidate");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("");
        out.flush();

        String line;
        String pattern = "[^\\/]+(.jpg|.gif|.png)+";
        while ((line = in.readLine()) != null) {
            Matcher matcher = Pattern.compile(pattern).matcher(line);
            if (matcher.find()) {
                imageList.add(matcher.group());
            }
            System.out.println(line);
        }
        in.close();
        out.close();
        socket.close();
    }

    public void getImages() throws IOException {
        for (String image : imageList) {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(address, port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("GET " + image + " HTTP/1.1");
            out.println("Host: " + socket.getInetAddress().getHostName());
            out.println("Cache-Control: no-cache");
            out.println("Server: Apache");
            out.println("Vary: Accept-Encoding,Cookie");
            out.println("Cache-Control: max-age=3, must-revalidate");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println("");

            CustomThread thread = new CustomThread(socket, image);
            thread.start();
        }
    }
}
