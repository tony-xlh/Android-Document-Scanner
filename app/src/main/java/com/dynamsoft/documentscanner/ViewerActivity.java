package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.EnumImagePixelFormat;
import com.dynamsoft.core.ImageData;
import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.NormalizedImageResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ViewerActivity extends AppCompatActivity {

    private ImageView normalizedImageView;
    private Point[] points;
    private Bitmap rawImage;
    private DocumentNormalizer ddn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        RadioGroup filterRadioGroup = findViewById(R.id.filterRadioGroup);
        filterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.binaryRadioButton) {
                    updateSettings(R.raw.binary_template);
                }else if (checkedId == R.id.grayscaleRadioButton) {
                    updateSettings(R.raw.gray_template);
                }else{
                    updateSettings(R.raw.color_template);
                }
                normalize();
            }
        });


        normalizedImageView = findViewById(R.id.normalizedImageView);
        try {
            ddn = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
        loadImageAndPoints();
        normalize();
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

    }

    private void normalize(){
        Quadrilateral quad = new Quadrilateral();
        quad.points = points;
        try {
            NormalizedImageResult result = ddn.normalize(rawImage,quad);
            normalizedImageView.setImageBitmap(result.image.toBitmap());
        } catch (DocumentNormalizerException | CoreException e) {
            e.printStackTrace();
        }
    }

    private void updateSettings(int id) {
        try {
            ddn.initRuntimeSettingsFromString(readTemplate(id));
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
    }

    private String readTemplate(int id){
        Resources resources = this.getResources();
        InputStream is=resources.openRawResource(id);
        byte[] buffer;
        try {
            buffer = new byte[is.available()];
            is.read(buffer);
            String content = new String(buffer);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void convertBitmapToImageData(){
        ImageData data = new ImageData();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rawImage.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte[] byteArray = stream.toByteArray();
        data.format = EnumImagePixelFormat.IPF_RGB_888;
        data.orientation = 0;
        data.width = rawImage.getWidth();
        data.height = rawImage.getHeight();
        data.bytes = byteArray;
        data.stride = 4 * ((rawImage.getWidth() * 3 + 31)/32);
    }
}