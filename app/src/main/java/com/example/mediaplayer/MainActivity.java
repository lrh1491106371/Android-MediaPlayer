package com.example.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songList = new ArrayList<>();
        songList.add(new Song(R.drawable.yuanyuchou, "愿与愁", "林俊杰", 225));
        songList.add(new Song(R.drawable.ai, "AI", "薛之谦", 240));
        songList.add(new Song(R.drawable.drama, "drama", "aespa", 215));

        SongAdapter adapter = new SongAdapter(songList, this, this::onSongSelected);
        recyclerView.setAdapter(adapter);
    }

    private void onSongSelected(int position) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class); // Corrected
        intent.putExtra("selectedSong", songList.get(position)); //  Keep this to send song data
        intent.putExtra("songList", (ArrayList<? extends Parcelable>) songList); // Pass the song list
        intent.putExtra("currentSongIndex", position); // Pass the index of the selected song
        startActivity(intent);
    }

}
