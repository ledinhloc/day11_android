package com.example.viduvideowithfirebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideosFireBaseAdapter videoAdapter;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intent = getIntent();
        String email = intent.getStringExtra("email");


        viewPager2 = findViewById(R.id.vpager);
        getVideos();

    }

    private void getVideos() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("videos");
        FirebaseRecyclerOptions<Video1Model> options = new FirebaseRecyclerOptions.Builder<Video1Model>()
                .setQuery(mDataBase, Video1Model.class).build();

        videoAdapter = new VideosFireBaseAdapter(options);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videoAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onStop() {
        super.onStop();
        videoAdapter.stopListening();
    }
}