package com.example.torch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.torch.databinding.ActivityMainBinding;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    private boolean isTorchOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    // Permission denied, handle appropriately
                    Log.e(TAG, "Camera permission is required to use the torch");
                    Toast.makeText(this, "Camera permission is required to use the torch", Toast.LENGTH_LONG).show();
                } else {
                    // Permission granted, proceed to turn on the torch if needed
                    if (isTorchOn) {
                        turnOnTorch();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "Camera access exception: " + e.getMessage());
        }

        requestCameraPermission();

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (isTorchOn) {
                    turnOffTorch();
                } else {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        turnOnTorch();
                    } else {
                        requestCameraPermission();
                    }
                }
                isTorchOn = !isTorchOn;
            }
        });
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOnTorch() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.setTorchMode(cameraId, true);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                }
                binding.img.setImageResource(R.drawable.on);
                binding.clt.setBackgroundColor(Color.BLACK);
                binding.btn.setText("Turn Off");
                binding.btn.setBackgroundColor(Color.BLACK);
                binding.btn.setTextColor(Color.WHITE);
            } else {
                Log.e(TAG, "Camera permission is required to use the torch");
                Toast.makeText(this, "Camera permission is required to use the torch", Toast.LENGTH_LONG).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "Error turning on torch: " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOffTorch() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.setTorchMode(cameraId, false);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FD742B")));
                }
                binding.img.setImageResource(R.drawable.a);
                binding.clt.setBackgroundColor(Color.parseColor("#FEFEFE"));
                binding.btn.setText("Turn On");
                binding.btn.setBackgroundColor(Color.parseColor("#FD742B"));
                binding.btn.setTextColor(Color.parseColor("#FEFEFE"));
            } else {
                Log.e(TAG, "Camera permission is required to use the torch");
                Toast.makeText(this, "Camera permission is required to use the torch", Toast.LENGTH_LONG).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "Error turning off torch: " + e.getMessage());
        }
    }
}
