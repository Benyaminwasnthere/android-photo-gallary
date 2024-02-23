/* 2023 Fall Android Photos App made By Sebastian Lecaros (sjl214) and Benyamin Plaksienko (Bp535) */
package com.example.photosandroidv2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchViewerActivity extends AppCompatActivity {

    private ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_viewer);

        fullImageView = findViewById(R.id.fullImageView);

        String photoPath = getIntent().getStringExtra("PHOTO_PATH");
        if (photoPath != null && !photoPath.isEmpty()) {
            fullImageView.setImageURI(Uri.parse(photoPath));
        }
    }
}
