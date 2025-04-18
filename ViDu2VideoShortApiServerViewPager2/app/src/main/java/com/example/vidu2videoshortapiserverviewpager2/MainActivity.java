package com.example.vidu2videoshortapiserverviewpager2;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private VideosAdapter videosAdapter;
    private List<VideoModel> list;

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

        viewPager2 = findViewById(R.id.vpager);
        list = new ArrayList<>();
        getVideos();
    }

    private void getVideos() {
        APIService.serviceApi.getVideos().enqueue(new Callback<MessageVideoModel>() {
            @Override
            public void onResponse(Call<MessageVideoModel> call, Response<MessageVideoModel> response) {
                list = response.body().getResult();
                videosAdapter = new VideosAdapter(getApplicationContext(), list);
                viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                viewPager2.setAdapter(videosAdapter);
            }

            @Override
            public void onFailure(Call<MessageVideoModel> call, Throwable t) {
                Log.d("TAG", t.getMessage());
            }
        });
    }
}