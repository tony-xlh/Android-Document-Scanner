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
    private ImageView polygonImageView;
    private ImageView corner1;
    private ImageView corner2;
    private ImageView corner3;
    private ImageView corner4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        imageView = findViewById(R.id.imageView);
        polygonImageView = findViewById(R.id.polygonImageView);
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
            }

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
            drawOverlay(getIntent().getIntExtra("bitmapWidth",720),
                        getIntent().getIntExtra("bitmapHeight",1280),
                        points);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawOverlay(int width,int height,Point[] pts){
        Bitmap bm = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Paint stroke = new Paint();
        stroke.setColor(Color.GREEN);
        Canvas canvas = new Canvas(bm);
        for (int index = 0; index <= pts.length - 1; index++) {
            if (index == pts.length - 1) {
                canvas.drawLine(pts[index].x,pts[index].y,pts[0].x,pts[0].y,stroke);
            }else{
                canvas.drawLine(pts[index].x,pts[index].y,pts[index+1].x,pts[index+1].y,stroke);
            }
        }
        polygonImageView.setImageBitmap(bm);
    }


}