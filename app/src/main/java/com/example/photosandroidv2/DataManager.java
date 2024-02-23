/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import android.content.Context;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DataManager {

    private static final String FILE_NAME = "albums_data.json";

    public static void SaveAllData(Context context, AlbumsHolder albumsHolder) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(albumsHolder);
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos)) {
            outputStreamWriter.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AlbumsHolder LoadAllData(Context context) {
        Gson gson = new Gson();
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             InputStreamReader inputStreamReader = new InputStreamReader(fis)) {
            return gson.fromJson(inputStreamReader, AlbumsHolder.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null in case of failure
        }
    }
}
