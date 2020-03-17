import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class CustomThread extends Thread {

    private Socket socket;

    private String name;

    CustomThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        File file = new File(System.getProperty("user.home") + "\\Desktop\\files\\" + name);
        System.out.println(file.getPath());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = socket.getInputStream();

            boolean headerEnded = false;          // Header end flag.

            byte[] bytes = new byte[2048];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                // If the end of the header had already been reached, write the bytes to the file as normal.
                if (headerEnded) {
                    fileOutputStream.write(bytes, 0, length);
                } else {
                    for (int i = 0; i < 2048; i++) {
                        if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                            headerEnded = true;
                            fileOutputStream.write(bytes, i + 4, 2048 - i - 4);
                            break;
                        }
                    }
                }
            }
            inputStream.close();
            fileOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
