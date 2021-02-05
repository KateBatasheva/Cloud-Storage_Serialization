package model;

import java.util.ArrayList;
import java.util.List;

public class RequestAllUsers  implements Message{

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    String users;
}
