/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchAllPhotosByTags extends AppCompatActivity {

    private Spinner tagTypeSpinner1, tagTypeSpinner2;
    private AutoCompleteTextView tagValueEditText1, tagValueEditText2;
    private CheckBox checkedAND, checkedOR;
    private Button generateSearchButton;
    private ListView searchResultsListView;
    AlbumsHolder currentAlbumMapData;
    private ArrayAdapter<String> searchResultsAdapter;
    private List<String> searchResults;
    private Set<String> tagSuggestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_photos_by_tags);

        tagTypeSpinner1 = findViewById(R.id.tagTypeSpinner1);
        tagTypeSpinner2 = findViewById(R.id.tagTypeSpinner2);
        tagValueEditText1 = findViewById(R.id.tagValueEditText1);
        tagValueEditText2 = findViewById(R.id.tagValueEditText2);
        checkedAND = findViewById(R.id.checkedAND);
        checkedOR = findViewById(R.id.checkedOR);
        generateSearchButton = findViewById(R.id.generateSearchButton);
        searchResultsListView = findViewById(R.id.searchResultsListView);

        currentAlbumMapData = DataManager.LoadAllData(this);
        gatherTagSuggestions();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.tag_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner1.setAdapter(spinnerAdapter);
        tagTypeSpinner2.setAdapter(spinnerAdapter);

        ArrayAdapter<String> tagValueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(tagSuggestions));
        tagValueEditText1.setAdapter(tagValueAdapter);
        tagValueEditText2.setAdapter(tagValueAdapter);

        searchResults = new ArrayList<>();
        searchResultsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
        searchResultsListView.setAdapter(searchResultsAdapter);

        generateSearchButton.setOnClickListener(v -> performSearch());

        // Set up double-click listener for search results
        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    String selectedResult = searchResults.get(position);
                    String photoPath = extractPhotoPath(selectedResult);
                    Intent intent = new Intent(SearchAllPhotosByTags.this, SearchViewerActivity.class);
                    intent.putExtra("PHOTO_PATH", photoPath);
                    startActivity(intent);
                }
                lastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    private void performSearch() {
        String tagType1 = tagTypeSpinner1.getSelectedItem().toString();
        String tagValue1 = tagValueEditText1.getText().toString().trim().toLowerCase(); // Case-insensitive
        String tagType2 = tagTypeSpinner2.getSelectedItem().toString();
        String tagValue2 = tagValueEditText2.getText().toString().trim().toLowerCase(); // Case-insensitive
        boolean isAndChecked = checkedAND.isChecked();
        boolean isOrChecked = checkedOR.isChecked();

        searchResults.clear();

        if (currentAlbumMapData != null) {
            for (AlbumModel album : currentAlbumMapData.getAlbumsMap().values()) {
                for (PhotoModel photo : album.getPhotosModelArrayList()) {
                    if (performTagSearch(photo, tagType1, tagValue1, tagType2, tagValue2, isAndChecked, isOrChecked)) {
                        searchResults.add(album.getAlbumName() + ": " + photo.getFileName());
                    }
                }
            }
        }

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No photos found with the given tags.", Toast.LENGTH_SHORT).show();
        }

        searchResultsAdapter.notifyDataSetChanged();
    }

    private void gatherTagSuggestions() {
        tagSuggestions = new HashSet<>();
        if (currentAlbumMapData != null) {
            for (AlbumModel album : currentAlbumMapData.getAlbumsMap().values()) {
                for (PhotoModel photo : album.getPhotosModelArrayList()) {
                    for (Map.Entry<String, ArrayList<String>> entry : photo.getTagMap().entrySet()) {
                        tagSuggestions.addAll(entry.getValue());
                    }
                }
            }
        }
    }

    private boolean performTagSearch(PhotoModel photo, String tagType1, String tagValue1, String tagType2, String tagValue2, boolean isAndChecked, boolean isOrChecked) {
        boolean matchesTag1 = photo.getTagMap().getOrDefault(tagType1, new ArrayList<>()).stream()
                .anyMatch(tag -> tag.equalsIgnoreCase(tagValue1)); // Case-insensitive
        boolean matchesTag2 = photo.getTagMap().getOrDefault(tagType2, new ArrayList<>()).stream()
                .anyMatch(tag -> tag.equalsIgnoreCase(tagValue2)); // Case-insensitive

        if (isAndChecked) {
            return matchesTag1 && matchesTag2;
        } else if (isOrChecked) {
            return matchesTag1 || matchesTag2;
        } else {
            return matchesTag1; // Default to single tag search
        }
    }

    private String extractPhotoPath(String searchResult) {
        // Assuming searchResult is in format "AlbumName: FileName"
        String[] parts = searchResult.split(": ");
        if (parts.length > 1) {
            String albumName = parts[0].trim();
            String fileName = parts[1].trim();
            AlbumModel album = currentAlbumMapData.getAlbum(albumName);
            for (PhotoModel photo : album.getPhotosModelArrayList()) {
                if (photo.getFileName().equals(fileName)) {
                    return photo.getFilePath();
                }
            }
        }
        return "";
    }

}
