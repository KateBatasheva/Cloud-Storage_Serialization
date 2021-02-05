import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import model.FilePackage;
import model.Message;
import model.RequestDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GoToNet {

    private static final Logger LOG = LoggerFactory.getLogger(GoToNet.class);


    private static ObjectDecoderInputStream is;
    private static  ObjectEncoderOutputStream os;
    private static  Socket socket;
    private static String nameClient;

    public static String getNameClient() {
        return socket.getLocalSocketAddress().toString();
    }

    public static void start() {
        try {
            socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            LOG.debug("Streams created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop () throws IOException {
        os.close();
        is.close();
        socket.close();
    }

    public static Message readObject() throws IOException, ClassNotFoundException {
        Object obj = is.readObject();
        return (Message) obj;
    }

    public static boolean sendMessToServer(Message nameFile) {
        try {
            os.writeObject(nameFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
