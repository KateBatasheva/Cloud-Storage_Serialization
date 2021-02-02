package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathFilePackage implements Message {

    private static final int BUFFERSIZE = 1024;

    public PathFilePackage(FilePackage pack) throws IOException {
        fileName = pack.getFileName();
        bufferPath = new byte[BUFFERSIZE];
        if ((pack.getFileBytes().length - filePathNum * bufferPath.length) < bufferPath.length){
            finish = (pack.getFileBytes().length - filePathNum * bufferPath.length);
        } else {
            finish =  bufferPath.length;
        }
        System.arraycopy(pack.getFileBytes(),filePathNum * bufferPath.length,bufferPath,0, finish);
        filePathNum++;
        len = pack.getFileBytes().length / BUFFERSIZE +1;
    }

    private int len;
    private String fileName;
    private long dataLen;
    private static int filePathNum;
    private byte [] bufferPath;
    int finish;

    public int getLen() {
        return len;
    }

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
