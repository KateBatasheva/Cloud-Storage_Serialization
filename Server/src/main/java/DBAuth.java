import java.sql.SQLException;

public class DBAuth implements Autherization {
    @Override
    public String getNick(String login, String password) throws SQLException {
        return DBManager.getNick(login, password);
    }

    @Override
    public String tryToAuthReturnPassword(String nick) throws SQLException {
        return DBManager.tryToAuthReturnPassword(nick);
    }

    @Override
    public boolean tryToAuth(String nick) throws SQLException {
        return DBManager.tryToAuth (nick);
    }

    @Override
    public boolean registr(String nick, String password) throws SQLException {
        return DBManager.registr(nick, password);
    }

    @Override
    public String showAllUsers() throws SQLException {
        return  DBManager.showAllUsers();
    }

}