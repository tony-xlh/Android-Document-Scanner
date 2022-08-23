package com.dynamsoft.documentscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.LicenseManager;
import com.dynamsoft.core.LicenseVerificationListener;

public class MainActivity extends AppCompatActivity {
    private Button startScanButton;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startScanButton = findViewById(R.id.startScanButton);
        startScanButton.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                startScan();
            } else {
                requestPermission();
            }
        });
        initDynamsoftLicense();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    Toast.makeText(this, "Please grant camera permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void startScan(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void initDynamsoftLicense(){
        LicenseManager.initLicense("DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ==", MainActivity.this, new LicenseVerificationListener() {
            @Override
            public void licenseVerificationCallback(boolean isSuccess, CoreException error) {
                if(!isSuccess){
                    error.printStackTrace();
                }else{
                    Log.d("DDN","license valid");
                }
            }
        });
    }
}