import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientUnite {

    private Socket socket;

    private List<String> imageList = new ArrayList<>();

    private InetAddress address;

    private int port;

    public ClientUnite(InetAddress address, int port) throws IOException {
        socket = new Socket(address, port);
        this.address = address;
        this.port = port;
    }

    public void getRequest() throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));

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
        String pattern = "/images/[^ ]*(.jpg|.gif|.png)+";

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
            socket = new Socket(address, port);
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
