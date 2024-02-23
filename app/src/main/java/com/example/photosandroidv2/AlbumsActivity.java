/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;



public class AlbumsActivity extends AppCompatActivity {


    private EditText albumNameEditText;
    private Button addAlbumButton, removeAlbumButton;

    private Button renameAlbumButton;
    private Button openAlbumButton;
    private int selectedAlbumPosition = -1;


    AlbumsHolder currentAlbumMapData;
    private ListView albumsListView;
    private ArrayAdapter<String> albumsAdapter;
    private ArrayList<String> albumNames;
    private Button searchByTagsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        currentAlbumMapData = DataManager.LoadAllData(this);

        if (currentAlbumMapData == null) {
            Toast.makeText(this, "Failed to load album data.", Toast.LENGTH_LONG).show();
            currentAlbumMapData = new AlbumsHolder(); // Use an empty AlbumsHolder
        }

        albumsListView = findViewById(R.id.listViewAlbums); // Replace with your actual ListView ID
        albumNames = new ArrayList<>();

        albumsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumNames);
        albumsListView.setAdapter(albumsAdapter);

        albumsListView.setOnItemClickListener((parent, view, position, id) -> selectedAlbumPosition = position);


        albumNameEditText = findViewById(R.id.albumNameEditText);

        addAlbumButton = findViewById(R.id.addAlbumButton);
        addAlbumButton.setOnClickListener(v -> addAlbum());

        removeAlbumButton = findViewById(R.id.removeAlbumButton);
        removeAlbumButton.setOnClickListener(v -> removeAlbum());

        renameAlbumButton = findViewById(R.id.renameAlbumButton);
        renameAlbumButton.setOnClickListener(v -> renameAlbum());


        openAlbumButton = findViewById(R.id.openAlbumButton);
        openAlbumButton.setOnClickListener(v -> openAlbum());

        searchByTagsButton = findViewById(R.id.searchByTagsButton);
        searchByTagsButton.setOnClickListener(v -> openSearchActivity());


        updateAlbumList();
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchAllPhotosByTags.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentAlbumMapData = DataManager.LoadAllData(this);
        updateAlbumList(); // Refresh the list view
    }



    private void openAlbum() {
        if (selectedAlbumPosition < 0 || selectedAlbumPosition >= albumNames.size()) {
            Toast.makeText(this, "Please select an album to open", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedAlbumName = albumNames.get(selectedAlbumPosition);

        Intent intent = new Intent(this, PhotosListActivity.class);
        intent.putExtra("ALBUM_NAME", selectedAlbumName);
        startActivity(intent);
    }


    private void addAlbum() {
        String albumName = albumNameEditText.getText().toString().trim();

        if (albumName.isEmpty()) {
            Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAlbumMapData.albumsMap.containsKey(albumName)) {
            Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        AlbumModel newAlbum = new AlbumModel(albumName);
        currentAlbumMapData.addAlbum(albumName, newAlbum); // Using addAlbum method of AlbumsHolder
        DataManager.SaveAllData(this, currentAlbumMapData);
        updateAlbumList(); // Refresh the list view
        albumNameEditText.setText("");
    }

    private void removeAlbum() {
        if (selectedAlbumPosition < 0 || selectedAlbumPosition >= albumNames.size()) {
            Toast.makeText(this, "Please select an album to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        String albumNameToRemove = albumNames.get(selectedAlbumPosition);

        currentAlbumMapData.deleteAlbum(albumNameToRemove);
        DataManager.SaveAllData(this, currentAlbumMapData);
        updateAlbumList();
        selectedAlbumPosition = -1;
    }


    private void renameAlbum() {
        if (selectedAlbumPosition < 0 || selectedAlbumPosition >= albumNames.size()) {
            Toast.makeText(this, "Please select an album to rename", Toast.LENGTH_SHORT).show();
            return;
        }

        String newAlbumName = albumNameEditText.getText().toString().trim();

        if (newAlbumName.isEmpty()) {
            Toast.makeText(this, "New album name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldAlbumName = albumNames.get(selectedAlbumPosition);
        if (currentAlbumMapData.albumsMap.containsKey(newAlbumName)) {
            Toast.makeText(this, "Album with this name already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        AlbumModel album = currentAlbumMapData.albumsMap.remove(oldAlbumName);
        album.setName(newAlbumName);
        currentAlbumMapData.albumsMap.put(newAlbumName,album);
        DataManager.SaveAllData(this, currentAlbumMapData);
        updateAlbumList();

    }


    private void updateAlbumList() {
        albumNames.clear();
        albumNames.addAll(currentAlbumMapData.getAlbumNames());
        albumsAdapter.notifyDataSetChanged();
    }
}
