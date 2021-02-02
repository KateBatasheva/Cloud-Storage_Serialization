import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClientStorageHandler extends ChannelInboundHandlerAdapter {
//
//    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();
//
    private String nickName;
    private Path clientFolder;
    private static int count = 0;
//
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        count++;
        nickName = "user#" + count;
        clientFolder = Paths.get("FolderServer", nickName);
        if (!Files.exists(clientFolder)){
            Files.createDirectory(clientFolder);
        }
        ctx.writeAndFlush(new ClientDelails(nickName));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RequestDownload) {
            RequestDownload fileName = (RequestDownload) msg;
            if (Files.exists(clientFolder.resolve(fileName.getName()))) {
                FilePackage sendPack = new FilePackage(clientFolder.resolve(fileName.getName()));
                PathFilePackage [] pathFilePackages = new PathFilePackage[sendPack.getFileBytes().length/1024+1];
                for (int i = 0; i <= sendPack.getFileBytes().length/1024; i++) {
                    pathFilePackages[i] = new PathFilePackage(sendPack);
                    ctx.writeAndFlush(pathFilePackages[i]);
                }
//                ctx.writeAndFlush(new FinishPack());
//                ctx.writeAndFlush(sendPack);
            }
        }
//        if (msg instanceof FilePackage) {
//            FilePackage filePackage = (FilePackage) msg;
//            Files.write(clientFolder.resolve(filePackage.getFileName()), filePackage.getFileBytes(), StandardOpenOption.CREATE);
//        }

        if (msg instanceof PathFilePackage){
            PathFilePackage pathFilePackage = (PathFilePackage) msg;
            if (!Files.exists(clientFolder.resolve(pathFilePackage.getFileName()))) {
                Files.createFile(clientFolder.resolve(pathFilePackage.getFileName()));
            }
            Files.write(clientFolder.resolve(pathFilePackage.getFileName()), pathFilePackage.getBufferPath(), StandardOpenOption.APPEND);
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

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        clients.remove(ctx);
//    }

