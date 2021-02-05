package model;

public class AuthForm implements Message {
    String nickName;
    String password;

    public AuthForm(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }
}
