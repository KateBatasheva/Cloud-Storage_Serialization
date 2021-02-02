package model;

public class RequestUpload implements Message{
    String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RequestUpload(String fileName) {
        this.fileName = fileName;
    }
}
