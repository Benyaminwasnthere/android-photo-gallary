/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import java.util.ArrayList;

public class AlbumModel {

    String albumName;
    ArrayList<PhotoModel> photosModelArrayList;



    public AlbumModel(String albumName) {
        this.albumName = albumName;
        this.photosModelArrayList = new ArrayList<>();
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<PhotoModel> getPhotosModelArrayList() {
        return photosModelArrayList;
    }


    public void setName(String name){
        this.albumName = name;
    }

    public void addPhoto(PhotoModel photo) {
        photosModelArrayList.add(photo);
    }


    public void deletePhoto(String photoName) {
        photosModelArrayList.removeIf(photo -> photo.getFileName().equals(photoName));
    }


}
