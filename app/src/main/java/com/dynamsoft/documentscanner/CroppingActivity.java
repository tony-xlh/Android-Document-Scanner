package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class CroppingActivity extends AppCompatActivity {
    private Button cancelButton;
    private Button saveButton;
    private Button rotateButton;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        imageView = findViewById(R.id.imageView);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            onBackPressed();
        });
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {

        });
        rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(v -> {

        });
        loadImage();
    }

    private void loadImage(){
        try {
            Uri uri = Uri.parse(getIntent().getStringExtra("imageUri"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}