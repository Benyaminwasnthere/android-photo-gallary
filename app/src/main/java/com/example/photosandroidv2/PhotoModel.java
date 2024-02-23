/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class PhotoModel implements Serializable {

    private String fileName;
    private String filePath; // This could be a file path or a URI string
    private HashMap<String, ArrayList<String>> tagMap;

    public PhotoModel(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.tagMap = new HashMap<>();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addTag(String tagType, String tagValue) {
        tagMap.computeIfAbsent(tagType, k -> new ArrayList<>()).add(tagValue);
    }

    public void removeTag(String tagType, String tagValue) {
        if (tagMap.containsKey(tagType)) {
            tagMap.get(tagType).remove(tagValue);
        }
    }

    public HashMap<String, ArrayList<String>> getTagMap() {
        return tagMap;
    }

}
