package com.example.viduvideowithfirebase;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class VideosFireBaseAdapter extends FirebaseRecyclerAdapter<Video1Model, VideosFireBaseAdapter.MyHolder> {
    private boolean isFav = false;
    public VideosFireBaseAdapter(@NonNull FirebaseRecyclerOptions<Video1Model> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Video1Model model) {
        holder.textVideoTitle.setText(model.getTitle());
        holder.textVideoTitle.setText(model.getDesc());
        holder.videoView.setVideoURI(Uri.parse(model.getUrl()));
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                holder.videoProgressBar.setVisibility(View.GONE);
                mediaPlayer.start();
                float videoRatio = mediaPlayer.getVideoHeight()/mediaPlayer.getVideoHeight();
                float screenRatio = holder.videoView.getWidth()/(float) holder.videoView.getHeight();
                float scale = videoRatio/screenRatio;
                if(scale >= 1f){
                    holder.videoView.setScaleX(scale);
                }else {
                    holder.videoView.setScaleY(1f/scale);
                }
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFav){
                    holder.favorites.setImageResource(R.drawable.favourite);
                }
                else {
                    holder.favorites.setImageResource(R.drawable.heart);
                    isFav = false;
                }
            }
        });

        holder.inPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext(); // lấy context từ view
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent); // dùng context để start
            }
        });
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyHolder(view);
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        private VideoView videoView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textVideoDescription;
        private ImageView inPerson, favorites, inShare, inMore;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            videoProgressBar = itemView.findViewById(R.id.videoProgressBar);
            textVideoTitle = itemView.findViewById(R.id.textVideoTitle);
            textVideoDescription = itemView.findViewById(R.id.textVideoDescription);
            inPerson = itemView.findViewById(R.id.imPerson);
            favorites = itemView.findViewById(R.id.favorites);
            inShare = itemView.findViewById(R.id.imShare);
            inMore = itemView.findViewById(R.id.imMore);
        }
    }
}
