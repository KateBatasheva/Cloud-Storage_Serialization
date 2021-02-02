package model;

public class RequestDownload implements Message {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestDownload(String name) {
        this.name = name;
    }
}
