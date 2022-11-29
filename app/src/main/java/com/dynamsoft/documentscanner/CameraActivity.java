package com.dynamsoft.documentscanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.ImageData;
import com.dynamsoft.core.LicenseManager;
import com.dynamsoft.core.LicenseVerificationListener;
import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private OverlayView overlayView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService exec;
    private Camera camera;
    private DocumentNormalizer ddn;
    private ImageCapture imageCapture;
    private Boolean taken = false;
    private ArrayList<DetectedQuadResult> previousResults = new ArrayList<DetectedQuadResult>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.overlayView);
        exec = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindUseCases(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        initDDN();
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindUseCases(@NonNull ProcessCameraProvider cameraProvider) {

        int orientation = getApplicationContext().getResources().getConfiguration().orientation;
        Size resolution;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            resolution = new Size(720, 1280);
        }else{
            resolution = new Size(1280, 720);
        }

        Preview.Builder previewBuilder = new Preview.Builder();
        previewBuilder.setTargetAspectRatio(AspectRatio.RATIO_16_9);
        Preview preview = previewBuilder.build();

        ImageAnalysis.Builder imageAnalysisBuilder = new ImageAnalysis.Builder();

        imageAnalysisBuilder.setTargetResolution(resolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);

        ImageAnalysis imageAnalysis = imageAnalysisBuilder.build();

        imageAnalysis.setAnalyzer(exec, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                @SuppressLint("UnsafeOptInUsageError")
                Bitmap bitmap = BitmapUtils.getBitmap(image);
                overlayView.setSrcImageWidth(bitmap.getWidth());
                overlayView.setSrcImageHeight(bitmap.getHeight());
                try {
                    DetectedQuadResult[] results = ddn.detectQuad(bitmap);

                    if (results != null) {
                        if (results.length>0) {
                            DetectedQuadResult result = results[0];
                            Log.d("DDN","confidence: "+result.confidenceAsDocumentBoundary);
                            overlayView.setPoints(result.location.points);
                            if (result.confidenceAsDocumentBoundary > 50) {
                                if (taken == false) {
                                    if (previousResults.size() == 3) {
                                        if (steady() == true) {
                                            Log.d("DDN","take photo");
                                            takePhoto(result, bitmap.getWidth(), bitmap.getHeight());
                                            taken = true;
                                        }else{
                                            previousResults.remove(0);
                                            previousResults.add(result);
                                        }
                                    }else{
                                        previousResults.add(result);
                                    }
                                }
                            }
                        }
                    }
                } catch (DocumentNormalizerException e) {
                    e.printStackTrace();
                }
                image.close();
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture =
                new ImageCapture.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build();
        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .addUseCase(imageCapture)
                .build();
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, useCaseGroup);
    }

    private Boolean steady(){
        float iou1 = Utils.intersectionOverUnion(previousResults.get(0).location.points,previousResults.get(1).location.points);
        float iou2 = Utils.intersectionOverUnion(previousResults.get(1).location.points,previousResults.get(2).location.points);
        float iou3 = Utils.intersectionOverUnion(previousResults.get(0).location.points,previousResults.get(2).location.points);
        Log.d("DDN","iou1: "+iou1);
        Log.d("DDN","iou2: "+iou2);
        Log.d("DDN","iou3: "+iou3);
        if (iou1>0.9 && iou2>0.9 && iou3>0.9) {
            return true;
        }else{
            return false;
        }
    }

    private void takePhoto(DetectedQuadResult result,int bitmapWidth,int bitmapHeight){
        File dir = getExternalCacheDir();
        File file = new File(dir, "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, exec,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        Log.d("DDN","saved");
                        Log.d("DDN",outputFileResults.getSavedUri().toString());
                        Intent intent = new Intent(CameraActivity.this, CroppingActivity.class);
                        intent.putExtra("imageUri",outputFileResults.getSavedUri().toString());
                        intent.putExtra("points",result.location.points);
                        intent.putExtra("bitmapWidth",bitmapWidth);
                        intent.putExtra("bitmapHeight",bitmapHeight);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                    }

                }
        );
    }

    private void initDDN(){
        try {
            ddn = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        previousResults.clear();
        taken = false;
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}