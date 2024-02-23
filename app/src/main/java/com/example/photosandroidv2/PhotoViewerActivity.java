/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Map;

public class PhotoViewerActivity extends AppCompatActivity {
    private ImageView photoImageView;
    private TextView photoNameTextView;
    private Button previousButton, forwardButton, addTagButton, deleteTagButton;
    private ListView tagListView;
    private Spinner tagTypeSpinner;
    private EditText tagValueEditText;
    private ArrayList<PhotoModel> photoList;
    private int currentIndex;
    private ArrayAdapter<String> tagAdapter;
    private AlbumsHolder currentAlbumMapData;
    private String CurrentAlbumName;
    private AlbumModel selectedAlbum;
    private String selectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        // Initialize views
        photoImageView = findViewById(R.id.photoImageView);
        photoNameTextView = findViewById(R.id.photoNameTextView);
        previousButton = findViewById(R.id.previousButton);
        forwardButton = findViewById(R.id.forwardButton);
        tagListView = findViewById(R.id.tagListView);
        addTagButton = findViewById(R.id.addTagButton);
        deleteTagButton = findViewById(R.id.deleteTagButton);
        tagTypeSpinner = findViewById(R.id.tagTypeSpinner);
        tagValueEditText = findViewById(R.id.tagValueEditText);

        // Setup the spinner for tag types
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.tag_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner.setAdapter(spinnerAdapter);

        // Load album data and get the current album name
        currentAlbumMapData = DataManager.LoadAllData(this);
        CurrentAlbumName = getIntent().getStringExtra("ALBUM_NAME");

        // Retrieve and display photos from the current album
        if (currentAlbumMapData != null) {
            selectedAlbum = currentAlbumMapData.getAlbum(CurrentAlbumName);
            if (selectedAlbum != null) {
                photoList = selectedAlbum.getPhotosModelArrayList();
                currentIndex = getIntent().getIntExtra("CURRENT_INDEX", 0);
                updatePhotoDisplay();
            }
        }

        // Button click listeners
        previousButton.setOnClickListener(v -> navigatePhoto(-1));
        forwardButton.setOnClickListener(v -> navigatePhoto(1));
        addTagButton.setOnClickListener(v -> {
            String tagType = tagTypeSpinner.getSelectedItem().toString();
            String tagValue = tagValueEditText.getText().toString().trim();
            modifyTag(true, tagType, tagValue);
        });

        // Listener for the deleteTagButton
        deleteTagButton.setOnClickListener(v -> {
            if (selectedTag != null && !selectedTag.isEmpty()) {
                // Split the selected tag into type and value
                String[] parts = selectedTag.split(": ");
                if (parts.length == 2) {
                    String tagType = parts[0].trim();
                    String tagValue = parts[1].trim();
                    modifyTag(false, tagType, tagValue);
                }
            } else {
                // Fallback to manually entered values if no tag is selected from the list
                String tagType = tagTypeSpinner.getSelectedItem().toString();
                String tagValue = tagValueEditText.getText().toString().trim();
                modifyTag(false, tagType, tagValue);
            }
        });

        tagListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedTag = (String) parent.getItemAtPosition(position);
        });
    }

    private void navigatePhoto(int direction) {
        currentIndex += direction;

        // Loop back to the start if the end is reached
        if (currentIndex >= photoList.size()) {
            currentIndex = 0; // Reset to the first photo
        }

        // Loop to the end if going back from the first photo
        if (currentIndex < 0) {
            currentIndex = photoList.size() - 1; // Set to the last photo
        }

        updatePhotoDisplay();
    }

    private void modifyTag(boolean isAdding, String tagType, String tagValue) {
        if (!tagValue.isEmpty()) {
            PhotoModel currentPhoto = photoList.get(currentIndex);
            if (isAdding) {
                currentPhoto.addTag(tagType, tagValue);
                currentAlbumMapData.getAlbum(CurrentAlbumName).getPhotosModelArrayList().set(currentIndex, currentPhoto);
                DataManager.SaveAllData(this, currentAlbumMapData);
            } else {
                currentPhoto.removeTag(tagType, tagValue);
                currentAlbumMapData.getAlbum(CurrentAlbumName).getPhotosModelArrayList().set(currentIndex, currentPhoto);
                DataManager.SaveAllData(this, currentAlbumMapData);
            }


            // Save changes
            currentAlbumMapData.getAlbum(CurrentAlbumName).getPhotosModelArrayList().set(currentIndex, currentPhoto);
            DataManager.SaveAllData(this, currentAlbumMapData);
            updatePhotoDisplay();
        }
    }

    private void updatePhotoDisplay() {
        PhotoModel currentPhoto = photoList.get(currentIndex);
        photoNameTextView.setText(currentPhoto.getFileName());
        photoImageView.setImageURI(Uri.parse(currentPhoto.getFilePath()));

        // Update tag list view
        ArrayList<String> tagList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : currentPhoto.getTagMap().entrySet()) {
            for (String tag : entry.getValue()) {
                tagList.add(entry.getKey() + ": " + tag);
            }
        }
        tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagList);
        tagListView.setAdapter(tagAdapter);
    }
}
