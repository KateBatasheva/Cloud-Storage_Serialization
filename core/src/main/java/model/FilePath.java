package model;



import java.nio.file.Files;

import java.nio.file.Path;



public class FilePath implements Message{



    private int numberPath;

    private boolean finish;

    private String fileName;

    private byte[] pathData;


    private int dataLen;





    public FilePath(int numberPath, boolean finish, String fileName, byte[] pathData, int dataLen) {

        this.numberPath = numberPath;

        this.finish = finish;

        this.fileName = fileName;

        this.pathData = pathData;

        this.dataLen = dataLen;

    }

    public int getDataLen() {
        return dataLen;
    }

    public int getNumberPath() {

        return numberPath;

    }



    public boolean isFinish() {

        return finish;

    }



    public String getFileName() {

        return fileName;

    }



    public byte[] getPathData() {

        return pathData;

    }

}