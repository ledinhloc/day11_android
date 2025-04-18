package com.example.viduvideowithfirebase;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ProfileActivity extends AppCompatActivity {

    EditText edtTitle, edtDesc;
    Button btnSelectVideo, btnUpload;
    TextView txtVideoName;
    ProgressBar progressBar;

    Uri selectedVideoUri;

    private ActivityResultLauncher<Intent> videoChooserLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnUpload = findViewById(R.id.btnUpload);
        txtVideoName = findViewById(R.id.txtVideoName);
        progressBar = findViewById(R.id.progressBar);

        
        CloudinaryHelper.initCloudinary(this);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openVideoChooserIntent();
                    } else {
                        Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        videoChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedVideoUri = result.getData().getData();
                        Toast.makeText(this, "Đã chọn video", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Chưa chọn video", Toast.LENGTH_SHORT).show();
                    }
                }
    }
    private void checkStoragePermissionAndOpenVideoChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openVideoChooserIntent();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            openVideoChooserIntent();
        }
    }

    private void openVideoChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        videoChooserLauncher.launch(Intent.createChooser(intent, "Chọn video"));
    }

    private void uploadVideoToCloudinary() {
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Vui lòng chọn video trước khi tải lên", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        MediaManager.get().upload(selectedVideoUri)
                .option("resource_type", "video") // rất quan trọng để Cloudinary nhận diện là video
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("CloudinaryUpload", "Bắt đầu tải video lên...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        double progress = (double) bytes / totalBytes * 100;
                        Log.d("CloudinaryUpload", "Tiến trình tải: " + String.format("%.2f", progress) + "%");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        progressBar.setVisibility(View.GONE);
                        String videoUrl = resultData.get("secure_url").toString();
                        Log.d("CloudinaryUpload", "Tải lên thành công, URL: " + videoUrl);
                        saveVideoInfoToFirebase(videoUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Lỗi tải lên: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        Log.e("CloudinaryUpload", "Lỗi tải lên: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w("CloudinaryUpload", "Đang thử lại tải lên...");
                    }
                }).dispatch();
    }

    private void saveVideoInfoToFirebase(String videoUrl) {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề và mô tả", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Video1Model video = new Video1Model(title, desc, videoUrl);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("videos");

        String key = ref.push().getKey();
        ref.child(key).setValue(video)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đã lưu video thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // hoặc reset form
                    } else {
                        Toast.makeText(this, "Lưu Firebase thất bại: " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseSave", "Lỗi lưu Firebase: " + task.getException());
                    }
                });
    }
}