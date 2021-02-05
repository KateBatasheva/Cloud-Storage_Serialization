public class Client {
    String nickName;
    String password;

    public Client(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return String.format("User with nick %s, and password length %s", getNickName(), getPassword().length());
    }
}
