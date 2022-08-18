package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
    private CropperView cropperView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        cropperView = findViewById(R.id.cropOverlayView);
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
            Parcelable[] parcelables = getIntent().getParcelableArrayExtra("points");
            Point[] points = new Point[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                points[i] = (Point) parcelables[i];
                Log.d("DDN",String.valueOf(points[i].x));
                Log.d("DDN",String.valueOf(points[i].y));
            }

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            //imageView.setImageBitmap(bitmap);
            cropperView.setSrcImageWidth(getIntent().getIntExtra("bitmapWidth",720));
            cropperView.setSrcImageHeight(getIntent().getIntExtra("bitmapHeight",1280));
            cropperView.setPoints(points);
            cropperView.setBackgroundImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}