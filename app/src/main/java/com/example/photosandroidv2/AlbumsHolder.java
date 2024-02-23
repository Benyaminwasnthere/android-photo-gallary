/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import java.util.Collection;
import java.util.HashMap;


public class AlbumsHolder {

    HashMap<String, AlbumModel> albumsMap;

    public AlbumsHolder() {
        albumsMap = new HashMap<>();
    }

    public AlbumModel getAlbum(String albumName) {
        return albumsMap.get(albumName);
    }

    public void addAlbum(String albumName, AlbumModel album) {
        albumsMap.put(albumName, album);
    }

    public void deleteAlbum(String albumName) {
        albumsMap.remove(albumName);
    }

    public Collection<String> getAlbumNames() {
        return albumsMap.keySet();
    }

    public HashMap<String, AlbumModel> getAlbumsMap() {
        return albumsMap;
    }


}
