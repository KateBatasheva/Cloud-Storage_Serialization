import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClientStorageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientStorageHandler.class);

    public ClientStorageHandler(String nickName) {
        this.nickName = nickName;
        clientFolder = Paths.get("FolderServer", nickName);
        if (!Files.exists(clientFolder)){
            try {
                Files.createDirectory(clientFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //
//    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();
//
    private String nickName;
    private Path clientFolder;
    //
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        LOG.debug("Channel for work with client is activated");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RegistrationForm){
            RegistrationForm reg = (RegistrationForm) msg;
                nickName = reg.getNickName();
        }

        if (msg instanceof RequestDownload) {
            RequestDownload fileName = (RequestDownload) msg;
            if (Files.exists(clientFolder.resolve(fileName.getName()))) {
                FilePackage sendPack = new FilePackage(clientFolder.resolve(fileName.getName()));
                DataInputStream in = new DataInputStream( new FileInputStream(String.valueOf(clientFolder.resolve(fileName.getName()))));
                FilePath fp;
                int read;
                int numberPath = 0;
                byte[] buffer = new byte[1024];
                while ((read = in.read(buffer)) != -1) {
                    numberPath++;
                    fp = new FilePath(numberPath, false, fileName.getName(), buffer);
                    ctx.writeAndFlush(fp);
                }
                numberPath++;
                fp = new FilePath(numberPath, true, fileName.getName(), null);
                ctx.writeAndFlush(fp);
                in.close();
            }
        }


        if (msg instanceof RequestRename) {
            RequestRename fileName = (RequestRename) msg;
            if (Files.exists(clientFolder.resolve(fileName.getOldName()))) {
                Path file = clientFolder.resolve(fileName.getOldName());
                Files.move(file, file.resolveSibling(fileName.getNewName()));
            }
            ctx.writeAndFlush(fileName);
        }
        if (msg instanceof RequestDelete) {
            RequestDelete fileName = (RequestDelete) msg;
            if (fileName.getPlaceWhereDelete() == 's'){
                Files.deleteIfExists(clientFolder.resolve(fileName.getFileName()));
            }
            ctx.writeAndFlush(fileName);
        }
    }
}


