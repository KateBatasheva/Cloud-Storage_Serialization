import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import model.AuthForm;
import model.InfoMessage;
import model.RegistrationForm;
import model.RequestAllUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRegHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthRegHandler.class);
    Autherization autherization;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("AuthRegChannel active for client");
        autherization = new DBAuth();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // registration step
        if (msg instanceof RegistrationForm) {
            RegistrationForm reg = (RegistrationForm) msg;
            if (reg.getNickName().contains(" ") || reg.getPassword().contains(" ") ||
                    reg.getNickName().trim().length() < 3 || reg.getPassword().trim().length() < 3) {
                ctx.writeAndFlush(new InfoMessage("NickName or password is too short or contains spaces")); // bad data
                return;
            }
            if (!autherization.registr(reg.getNickName(), reg.getPassword())){
                ctx.writeAndFlush(new InfoMessage("Client already exist. Try to authorization.")); // client exist, auth please
            } else {
                    ctx.writeAndFlush(new InfoMessage("Registration success")); // success try
                }
        }

        // authorization step
        if (msg instanceof AuthForm) {
            AuthForm auth = (AuthForm) msg;
            if (!autherization.tryToAuth(auth.getNickName())) {
                ctx.writeAndFlush(new InfoMessage("User not found")); // not found user
            } else {
                if (autherization.tryToAuthReturnPassword(auth.getNickName()).equals(auth.getPassword())) {
                    ctx.writeAndFlush(auth); // right, open
                    ctx.channel().pipeline().addLast(new ClientStorageHandler(auth.getNickName()));
                    ctx.channel().pipeline().remove(this);
                } else {
                    ctx.writeAndFlush(new InfoMessage("Wrong nickname or password. Try again.")); // wrong, try again
                }
            }
        }

        if (msg instanceof RequestAllUsers){
            RequestAllUsers users = (RequestAllUsers) msg;
            users.setUsers(autherization.showAllUsers());
            ctx.writeAndFlush(users);
        }

    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("AuthRegChannel disactive for client");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("e =", cause);
    }
}
