package com.example.vidu2videoshortapiserverviewpager2;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    private Context context;
    private List<VideoModel> videolist;
    private boolean isFav = false;

    public VideosAdapter(Context context, List<VideoModel> videolist) {
        this.context = context;
        this.videolist = videolist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapter.MyViewHolder holder, int position) {
        VideoModel videoModel = videolist.get(position);
        holder.textVideoTitle.setText(videoModel.getTitle());
        holder.textVideoDescription.setText(videoModel.getDescription());
        //holder.videoView.setVideoPath(videoModel.getUrl());
        holder.videoView.setVideoURI(Uri.parse(videoModel.getUrl()));
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.videoProgressBar.setVisibility(View.GONE);
                mp.start();
                float videoRatio = (float) mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = (float) holder.videoView.getWidth() / (float) holder.videoView.getHeight();
                float scale = videoRatio / screenRatio;
                if (scale >= 1f) {
                    holder.videoView.setScaleX(scale);
                } else {
                    holder.videoView.setScaleY(1f / scale);
                }
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.videoView.setMediaController(new MediaController(context));
                holder.videoView.requestFocus();
                mp.start();
            }
        });
        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFav){
                    holder.favorites.setImageResource(R.drawable.favourite);
                    isFav = true;
                }
                else {
                    holder.favorites.setImageResource(R.drawable.heart);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(videolist != null)
            return videolist.size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private VideoView videoView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textVideoDescription;
        private ImageView inPerson, favorites, inShare, inMore;

        public MyViewHolder(@NonNull View itemView) {
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
