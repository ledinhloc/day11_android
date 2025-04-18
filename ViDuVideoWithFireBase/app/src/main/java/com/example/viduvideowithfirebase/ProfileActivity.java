package com.example.viduvideowithfirebase;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import android.Manifest;

public class ProfileActivity extends AppCompatActivity {
    EditText edtTitle, edtDesc;
    Button btnSelectVideo, btnUpload;
    TextView txtVideoName;
    ProgressBar progressBar;

    Uri selectedVideoUri;
    private static final int REQUEST_CODE_PICK_VIDEO = 1001;
    private static final int REQUEST_CODE_PERM = 2001;
    private DatabaseReference videosRef;


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

        videosRef = FirebaseDatabase.getInstance()
                .getReference("videos"); // node "videos"

        CloudinaryHelper.initCloudinary(this);
        btnSelectVideo.setOnClickListener(v -> checkPermissionAndPick());
        btnUpload.setOnClickListener(v -> {
            if (selectedVideoUri != null) {
                uploadVideoToCloudinary();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkPermissionAndPick() {
        // runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                        REQUEST_CODE_PERM);
                return;
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERM);
                return;
            }
        }
        pickVideo();
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERM
                && grantResults.length>0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickVideo();
        } else {
            Toast.makeText(this,
                    "Cần cấp quyền để chọn video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK) {
            selectedVideoUri = data.getData();
            btnUpload.setEnabled(true);
            Toast.makeText(this, "Đã chọn video", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideoToCloudinary() {
        String title = edtTitle.getText().toString().trim();
        String desc  = edtDesc.getText().toString().trim();
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập tiêu đề và mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload lên Cloudinary
        MediaManager.get().upload(selectedVideoUri)
                .option("resource_type", "video")
                .option("upload_preset", "ml_default") // nếu unsigned
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(ProfileActivity.this,
                                "Bắt đầu upload...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId,
                                           long bytes, long totalBytes) {
                        // có thể update ProgressBar nếu cần
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        saveToFirebase(title, desc, url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(ProfileActivity.this,
                                "Upload lỗi: " + error.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    private void saveToFirebase(String title, String desc, String url) {
        // Tạo model và push
        Video1Model model = new Video1Model(title, desc, url);
        videosRef.push()
                .setValue(model)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,
                            "Lưu Firebase thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Lưu Firebase lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

}