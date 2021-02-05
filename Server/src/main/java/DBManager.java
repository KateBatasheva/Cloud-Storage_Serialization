import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBManager {

    private static Connection connection;
    private static PreparedStatement prepAdd;
    private static PreparedStatement prepGetLog;
    private static PreparedStatement prepGetNick;
    private static PreparedStatement prepDeleteUsers;
    private static PreparedStatement prepGetAllUsers;

    private static final Logger LOG = LoggerFactory.getLogger(DBManager.class);


    public DBManager() {
        new Thread(() -> {
            try {
                setConnection();
//                System.out.println("DB is connected");
                LOG.debug("DB is connected");
                prepSQL();
//                System.out.println("PrepSQL is connected");
                LOG.debug("PrepSQL is connected");

            } catch (Exception o) {
                o.printStackTrace();
            }
        }).start();
    }

    private void prepSQL() throws SQLException {
        prepAdd = connection.prepareStatement("INSERT INTO usersStrorage (Nick, Password) VALUES (?, ?);");
        prepGetLog = connection.prepareStatement("SELECT * FROM usersStrorage WHERE Nick = ?;");
        prepGetNick = connection.prepareStatement("SELECT * FROM usersStrorage WHERE Nick = ?;");
        prepGetAllUsers = connection.prepareStatement("SELECT Nick FROM usersStrorage;");
        prepDeleteUsers = connection.prepareStatement("DELETE FROM usersStrorage " +
                "where Nick not null;");

    }

    public static void deleteUsers () throws SQLException {
        prepDeleteUsers.executeUpdate();
    }

    public static void disconnect() {
        try {
            prepAdd.close();
            prepGetNick.close();
            prepGetLog.close();
            prepGetAllUsers.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static void setConnection()  {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.db");
        } catch (ClassNotFoundException| SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean tryToAuth (String nick) throws SQLException {
        ResultSet resultSet = null;
        try {
            prepGetLog.setString(1, nick);
            resultSet = prepGetLog.executeQuery();
            if (!resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return true;
    }

    public static String tryToAuthReturnPassword (String nick) throws SQLException {
        ResultSet resultSet = null;
        try {
            prepGetLog.setString(1, nick);
            resultSet = prepGetLog.executeQuery();
            return resultSet.getString(2);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return null;
    }

    public static String getNick(String nick, String password) throws SQLException {
        ResultSet resultSet = null;
        try {
            prepGetLog.setString(1, nick);
            resultSet = prepGetLog.executeQuery();
            if (resultSet.getString(2).equals(password)) {
                return resultSet.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
        }
        return null;
    }

    public static boolean registr(String nick, String password) throws SQLException {
        ResultSet resultSet = null;
        prepGetLog.setString(1, nick);
        resultSet = prepGetLog.executeQuery();
        if (resultSet.next()) {
            prepGetLog.close();
            return false;
        }
        prepAdd.setString(1, nick);
        prepAdd.setString(2, password);
        prepAdd.executeUpdate();
        resultSet.close();
        return true;
    }

    public static String showAllUsers () throws SQLException {
        ResultSet resultSet = null;
        resultSet = prepGetAllUsers.executeQuery();
        StringBuilder sb = new StringBuilder();
       while ((resultSet.next())){
           sb.append(resultSet.getString(1)).append("; ");
       }
       return sb.toString();
    }
}
