package model;

public class RequestDelete implements Message {
    String fileName;
    char placeWhereDelete;

    public RequestDelete(String fileName, char placeWhereDelete) {
        this.fileName = fileName;
        this.placeWhereDelete = placeWhereDelete;
    }

    public char getPlaceWhereDelete() {
        return placeWhereDelete;
    }

    public String getFileName() {
        return fileName;
    }
}
