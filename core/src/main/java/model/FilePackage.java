package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePackage implements Message {

    private String fileName;
    private byte [] fileBytes;
    private Path path;

    public FilePackage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        fileBytes = Files.readAllBytes(path);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    @Override
    public String toString() {
        return String.format("%s [%d bytes]",
                getFileName(), getFileBytes().length);
    }
}
