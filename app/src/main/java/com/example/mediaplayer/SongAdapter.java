package com.example.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context context;
    private OnSongClickListener onSongClickListener;

    public interface OnSongClickListener {
        void onSongClick(int position);
    }

    public SongAdapter(List<Song> songList, Context context, OnSongClickListener onSongClickListener) {
        this.songList = songList;
        this.context = context;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songPic.setImageResource(song.getImageResourceId());
        holder.songName.setText(song.getSongName());
        holder.artistName.setText(song.getArtistName());

        holder.itemView.setOnClickListener(v -> {
            if (onSongClickListener != null) {
                onSongClickListener.onSongClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        ImageView songPic;
        TextView songName;
        TextView artistName;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songPic = itemView.findViewById(R.id.song_pic);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.name);
        }
    }
}
