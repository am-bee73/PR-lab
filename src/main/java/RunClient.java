import java.io.IOException;
import java.net.InetAddress;

public class RunClient {

    public static void main(String[] args) throws IOException {

        InetAddress addressUtm = InetAddress.getByName("utm.md");
        ClientUtm utm = new ClientUtm(addressUtm, 443);
        utm.getRequest();
        utm.getImages();

        InetAddress addressUnite = InetAddress.getByName("unite.md");
        ClientUnite unite = new ClientUnite(addressUnite, 80);
        unite.getRequest();
        unite.getImages();
    }
}
