package model;

public class ClientDelails implements Message {
    String nickName;

    public ClientDelails(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
}
