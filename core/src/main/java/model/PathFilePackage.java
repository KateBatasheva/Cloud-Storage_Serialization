package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathFilePackage implements Message {

    public PathFilePackage(FilePackage pack) throws IOException {
        fileName = pack.getFileName();
        bufferPath = new byte[1024];
        System.arraycopy(pack.getFileBytes(),filePathNum * bufferPath.length,bufferPath,0, bufferPath.length-1);
        filePathNum++;

    }

    private String fileName;
    private long dataLen;
    private int filePathNum;
    private byte [] bufferPath;

    public String getFileName() {
        return fileName;
    }

    public long getDataLen() {
        return bufferPath.length;
    }

    public int getFilePathNum() {
        return filePathNum;
    }

    public byte[] getBufferPath() {
        return bufferPath;
    }
}
