package model;

public class InfoMessage implements Message{
    String message;

    public InfoMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
