package com.example.ubi_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class TimerScannerActivity extends AppCompatActivity {

    //Variables initializing
    private final String TAG = TimerScannerActivity.class.getSimpleName();
    private final int REQUEST_CODE_CAMERA = 2;
    private final int SCAN_DELAY = 5000;
    private List<String> scannedItems = new ArrayList<>();
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_scanner);

        codeScannerView = findViewById(R.id.scanner_view);

        initCountdownScan();
        stopScanDelay();
    }

    /**
     * Camera's permission management
     */
    @AfterPermissionGranted(REQUEST_CODE_CAMERA)
    private void startScanAfterPermissionIsGranted() {
        Log.i(TAG, "startScanAfterPermissionIsGranted");
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            codeScanner.startPreview();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_rationale),
                    REQUEST_CODE_CAMERA, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult");
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * Initializing the scanner and counting numbers of scanned items ONLY if different
     */
    public void initCountdownScan() {
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                Log.i(TAG, "onDecoded: " + result.getText());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!scannedItems.contains(result.getText())) {
                            scannedItems.add(result.getText());
                            Toast.makeText(TimerScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Log.i(TAG, "Exception: " + error.getLocalizedMessage());
            }
        });
    }

    /**
     * Limit activity to 15 seconds and send results to MainActivity
     */
    private void stopScanDelay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", scannedItems.size());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }, SCAN_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanAfterPermissionIsGranted();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}