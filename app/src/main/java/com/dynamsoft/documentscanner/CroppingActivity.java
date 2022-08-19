package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
    private Bitmap background;
    private ImageView imageView;
    private OverlayView overlayView;
    private ImageView corner1;
    private ImageView corner2;
    private ImageView corner3;
    private ImageView corner4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        overlayView = findViewById(R.id.cropOverlayView);
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

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //updateOverlayViewLayout();
            }
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
            }

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
            background = bitmap;
            int bitmapWidth = getIntent().getIntExtra("bitmapWidth",720);
            int bitmapHeight = getIntent().getIntExtra("bitmapHeight",1280);
            drawOverlay(bitmapWidth,bitmapHeight,points);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawOverlay(int width,int height,Point[] pts){
        Log.d("DDN","image width: "+width);
        Log.d("DDN","image height: "+height);
        overlayView.setPointsAndImageGeometry(pts,width,height);
    }

    private void updateOverlayViewLayout(){
        Bitmap bm = background;
        Log.d("DDN","image width: "+imageView.getWidth());
        Log.d("DDN","image height: "+imageView.getHeight());
        double ratioView = ((double) imageView.getWidth())/imageView.getHeight();
        double ratioImage = ((double) bm.getWidth())/bm.getHeight();
        double offsetX = (ratioImage*bm.getWidth()-bm.getHeight())/2;
        Log.d("DDN","ratioImage: "+ratioImage);
        Log.d("DDN","offsetX: "+offsetX);
        overlayView.setX((float) offsetX);
    }

}