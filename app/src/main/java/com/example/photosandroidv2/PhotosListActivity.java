/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class PhotosListActivity extends AppCompatActivity {
    private ListView photosListView;
    private Button addPhotoButton;
    private ArrayAdapter<String> photosAdapter;
    private ArrayList<String> photoNames;
    private AlbumModel selectedAlbum;

    private Button deletePhotoButton;
    private int selectedPhotoPosition = -1;

    private TextView currentAlbumTextView;
    private Button movePhotoButton;
    private Spinner albumSpinner;
    private ArrayAdapter<String> albumSpinnerAdapter;
    private ArrayList<String> availableAlbumNames;

    private Button openPhotoButton;



    private ImageView thumbnailImageView;

    AlbumsHolder currentAlbumMapData;
    String CurrentAlbumName;
    private static final int REQUEST_CODE = 1; // Unique request code for gallery intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_list);

        // Load album data
        currentAlbumMapData = DataManager.LoadAllData(this);
        if (currentAlbumMapData == null) {
            Toast.makeText(this, "Failed to load album data.", Toast.LENGTH_LONG).show();
            currentAlbumMapData = new AlbumsHolder(); // Use an empty AlbumsHolder
        }

        // Get the current album name passed from the previous activity
        CurrentAlbumName = getIntent().getStringExtra("ALBUM_NAME");

        // Initialize views
        photosListView = findViewById(R.id.photosListView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        deletePhotoButton = findViewById(R.id.deletePhotoButton);
        movePhotoButton = findViewById(R.id.movePhotoButton);
        albumSpinner = findViewById(R.id.albumSpinner);
        currentAlbumTextView = findViewById(R.id.currentAlbumTextView);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        openPhotoButton = findViewById(R.id.openPhotoButton);

        // Set current album name to the TextView
        currentAlbumTextView.setText("Album: " + CurrentAlbumName);

        // Retrieve and display photos from the current album
        selectedAlbum = currentAlbumMapData.getAlbum(CurrentAlbumName);
        photoNames = new ArrayList<>();
        if (selectedAlbum != null) {
            for (PhotoModel photo : selectedAlbum.getPhotosModelArrayList()) {
                photoNames.add(photo.getFileName());
            }
        }

        // Set up the ArrayAdapter for the ListView
        photosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, photoNames);
        photosListView.setAdapter(photosAdapter);

        // Set up the ArrayAdapter for the Spinner
        availableAlbumNames = new ArrayList<>(currentAlbumMapData.getAlbumNames());
        albumSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableAlbumNames);
        albumSpinner.setAdapter(albumSpinnerAdapter);

        // OnClickListener for the Add Photo button
        addPhotoButton.setOnClickListener(v -> openGallery());

        // OnClickListener for the Delete Photo button
        deletePhotoButton.setOnClickListener(v -> deletePhoto());

        // OnClickListener for the Move Photo button
        movePhotoButton.setOnClickListener(v -> movePhoto());


        openPhotoButton.setOnClickListener(v -> openPhotoViewer());

        // Set up an item click listener for the ListView
        photosListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPhotoPosition = position;
            updateThumbnail(position); // Update the thumbnail based on selected photo
        });
    }

    private void openPhotoViewer() {
        if (selectedPhotoPosition < 0 || selectedPhotoPosition >= photoNames.size()) {
            Toast.makeText(this, "Please select a photo to view", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PhotoViewerActivity.class);
        intent.putExtra("PHOTO_LIST", selectedAlbum.getPhotosModelArrayList());
        intent.putExtra("CURRENT_INDEX", selectedPhotoPosition);
        intent.putExtra("ALBUM_NAME", CurrentAlbumName);
        startActivity(intent);
    }


    private void updateThumbnail(int position) {
        if (position < 0 || position >= selectedAlbum.getPhotosModelArrayList().size()) {
            return;
        }

        PhotoModel selectedPhoto = selectedAlbum.getPhotosModelArrayList().get(position);
        Uri photoUri = Uri.parse(selectedPhoto.getFilePath());
        thumbnailImageView.setImageURI(photoUri);
        thumbnailImageView.setVisibility(View.VISIBLE);
    }


    private void movePhoto() {
        if (selectedPhotoPosition < 0 || selectedPhotoPosition >= photoNames.size()) {
            Toast.makeText(this, "Please select a photo to move", Toast.LENGTH_SHORT).show();
            return;
        }
        currentAlbumMapData = DataManager.LoadAllData(this);
        selectedAlbum = currentAlbumMapData.getAlbum(CurrentAlbumName);

        String targetAlbumName = (String) albumSpinner.getSelectedItem();
        if (targetAlbumName.equals(CurrentAlbumName)) {
            Toast.makeText(this, "Photo is already in this album", Toast.LENGTH_SHORT).show();
            return;
        }

        PhotoModel photoToMove = selectedAlbum.getPhotosModelArrayList().get(selectedPhotoPosition);
        AlbumModel targetAlbum = currentAlbumMapData.getAlbum(targetAlbumName);

        if (targetAlbum != null) {
            selectedAlbum.deletePhoto(photoToMove.getFileName());
            targetAlbum.addPhoto(photoToMove);

            photoNames.remove(selectedPhotoPosition);
            photosAdapter.notifyDataSetChanged();

            DataManager.SaveAllData(this, currentAlbumMapData);

            // Reset the thumbnail
            thumbnailImageView.setImageURI(null);
            thumbnailImageView.setVisibility(View.GONE);
            selectedPhotoPosition = -1; // Reset the selected photo position

        }
    }

        private void deletePhoto() {
        if (selectedPhotoPosition < 0 || selectedPhotoPosition >= photoNames.size()) {
            Toast.makeText(this, "Please select a photo to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String photoNameToRemove = photoNames.get(selectedPhotoPosition);
        selectedAlbum.deletePhoto(photoNameToRemove);
        photoNames.remove(selectedPhotoPosition);
        photosAdapter.notifyDataSetChanged();

        DataManager.SaveAllData(this, currentAlbumMapData);

            // Reset the thumbnail
            thumbnailImageView.setImageURI(null);
            thumbnailImageView.setVisibility(View.GONE);
            selectedPhotoPosition = -1; // Reset the selected photo position
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fullPhotoUri = data.getData();
            if (fullPhotoUri != null) {
                // Take persistable URI permission to access the photo later
                getContentResolver().takePersistableUriPermission(
                        fullPhotoUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
            }

            try {
                String fileName = getFileName(fullPhotoUri);
                String filePath = fullPhotoUri.toString();
                PhotoModel newPhoto = new PhotoModel(fileName, filePath);

                // Use the updated addPhoto method
                if (!photoNames.contains(fileName)) {
                    selectedAlbum.addPhoto(newPhoto);
                    photoNames.add(fileName);
                    photosAdapter.notifyDataSetChanged();

                    currentAlbumMapData.albumsMap.put(CurrentAlbumName, selectedAlbum);
                    DataManager.SaveAllData(this, currentAlbumMapData);
                } else {
                    Toast.makeText(this, "This photo already exists in the album", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error while selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
