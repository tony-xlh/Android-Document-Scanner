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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
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
    private ImageView[] corners = new ImageView[4];
    private int mLastX;
    private int mLastY;
    private Point[] points;
    private int screenWidth;
    private int screenHeight;
    private int bitmapWidth;
    private int bitmapHeight;
    private int cornerWidth = 30;
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

        corner1 = findViewById(R.id.corner1);
        corner2 = findViewById(R.id.corner2);
        corner3 = findViewById(R.id.corner3);
        corner4 = findViewById(R.id.corner4);
        corners[0] = corner1;
        corners[1] = corner2;
        corners[2] = corner3;
        corners[3] = corner4;
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //updateOverlayViewLayout();
            }
        });
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth=metrics.widthPixels;
        screenHeight=metrics.heightPixels;
        bitmapWidth = getIntent().getIntExtra("bitmapWidth",720);
        bitmapHeight = getIntent().getIntExtra("bitmapHeight",1280);
        loadPoints();
        loadImage();
        setEvents();
    }

    private void loadPoints(){
        Parcelable[] parcelables = getIntent().getParcelableArrayExtra("points");
        points = new Point[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            points[i] = (Point) parcelables[i];
        }
    }

    private void loadImage(){
        try {
            Uri uri = Uri.parse(getIntent().getStringExtra("imageUri"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
            background = bitmap;
            drawOverlay();
            updateCornersPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCornersPosition(){
        for (int i = 0; i < 4; i++) {
            corners[i].setX(points[i].x*screenWidth/bitmapWidth-cornerWidth);
            corners[i].setY(points[i].y*screenHeight/bitmapHeight-cornerWidth);
        }
    }

    private void setEvents(){
        for (int i = 0; i < 4; i++) {
            corners[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    Log.d("DDN",event.toString());
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mLastX = x;
                            mLastY = y;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.setX(view.getX()+x);
                            view.setY(view.getY()+y);
                            updatePointsAndRedraw();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }

    private void updatePointsAndRedraw(){
        for (int i = 0; i < 4; i++) {
            points[i].x = (int) ((corners[i].getX()+cornerWidth)/screenWidth*bitmapWidth);
            points[i].y = (int) ((corners[i].getY()+cornerWidth)/screenHeight*bitmapHeight);
        }
        drawOverlay();
    }

    private void drawOverlay(){
        overlayView.setPointsAndImageGeometry(points,bitmapWidth,bitmapHeight);
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