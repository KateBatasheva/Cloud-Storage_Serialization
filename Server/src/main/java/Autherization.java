import java.sql.SQLException;

public interface Autherization {

    String getNick(String nick, String password) throws SQLException;

    String tryToAuthReturnPassword(String nick) throws SQLException;

    boolean tryToAuth(String nick) throws SQLException;

    boolean registr(String nick, String password) throws SQLException;

    String showAllUsers() throws SQLException;
}
