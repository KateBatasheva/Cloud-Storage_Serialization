package model;

public class RegistrationForm implements Message {

    private String password;
    private String nickName;

    public RegistrationForm(String nickName, String password) {
        this.password = password;
        this.nickName = nickName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
