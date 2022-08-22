package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.NormalizedImageResult;

import java.io.IOException;

public class ViewerActivity extends AppCompatActivity {

    private ImageView normalizedImageView;
    private Point[] points;
    private Bitmap rawImage;
    private DocumentNormalizer ddn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        normalizedImageView = findViewById(R.id.normalizedImageView);
        try {
            ddn = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
        loadImageAndPoints();

    }

    private void loadImageAndPoints(){
        Uri uri = Uri.parse(getIntent().getStringExtra("imageUri"));
        try {
            rawImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int bitmapWidth = getIntent().getIntExtra("bitmapWidth",720);
        int bitmapHeight = getIntent().getIntExtra("bitmapHeight",1280);
        Parcelable[] parcelables = getIntent().getParcelableArrayExtra("points");
        points = new Point[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            points[i] = (Point) parcelables[i];
            points[i].x = points[i].x*rawImage.getWidth()/bitmapWidth;
            points[i].y = points[i].y*rawImage.getHeight()/bitmapHeight;
        }
        Quadrilateral quad = new Quadrilateral();
        quad.points = points;
        try {
            NormalizedImageResult result = ddn.normalize(rawImage,quad);
            normalizedImageView.setImageBitmap(result.image.toBitmap());
        } catch (DocumentNormalizerException | CoreException e) {
            e.printStackTrace();
        }
    }

}