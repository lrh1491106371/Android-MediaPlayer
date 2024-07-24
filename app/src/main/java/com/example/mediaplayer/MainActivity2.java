package com.example.mediaplayer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {

    private TextView songName;
    private ImageView ivMusic;
    private TextView tvTotal, tvProgress;
    private SeekBar seekBar;
    private AppCompatImageButton btnPrevious, btnPlay, btnPause, btnNext;
    private ImageView btnExit;
    private List<Song> songList;
    private int currentSongIndex;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Runnable updateProgress; // Declare updateProgress as a member variable
    private float currentRotation = 0f; // Track the current rotation angle
    private boolean isPaused = false;
    private RotateAnimation rotateAnimation; // Declare the RotateAnimation


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                // Convert milliseconds to seconds and format
                int currentSeconds = mediaPlayer.getCurrentPosition() / 1000;
                String formattedTime = formatDuration(currentSeconds);
                tvProgress.setText(formattedTime);
            }
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        songName = findViewById(R.id.song_name);
        ivMusic = findViewById(R.id.iv_music);
        tvTotal = findViewById(R.id.tv_total);
        tvProgress = findViewById(R.id.tv_progress);
        seekBar = findViewById(R.id.sb);
        btnPrevious = findViewById(R.id.btn_pre);
        btnPlay = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnNext = findViewById(R.id.btn_next);
        btnExit = findViewById(R.id.btn_exit);

        mediaPlayer = new MediaPlayer();

        Intent intent = getIntent();
        songList = intent.getParcelableArrayListExtra("songList");
        currentSongIndex = intent.getIntExtra("currentSongIndex", 0);

        if (songList != null && currentSongIndex >= 0 && currentSongIndex < songList.size()) {
            Song selectedSong = songList.get(currentSongIndex);
            songName.setText(selectedSong.getSongName());
            ivMusic.setImageResource(selectedSong.getImageResourceId());
            tvTotal.setText(formatDuration(selectedSong.getSongDuration()));
            playMusic(selectedSong);
        }

        btnPrevious.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                Song previousSong = songList.get(currentSongIndex);
                updateUI(previousSong);
                playMusic(previousSong);

                isPlaying = true;
                isPaused = false;
                currentRotation = 0f; // 重置旋转角度，因为是播放新歌
                ivMusic.setRotation(0f); // 重置图片视图旋转
                rotateImageView(); // 开始新的旋转
                handler.postDelayed(updateProgress, 1000); // 每秒更新进度
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                isPaused = false;
                rotateImageView();
                // Restart the progress update runnable
                handler.postDelayed(updateProgress, 1000); // Update every 1 second
            }
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                isPaused = true;
                // Stop the progress update runnable
                handler.removeCallbacks(updateProgress);
                // Update currentRotation with the current rotation of the ImageView
                currentRotation = ivMusic.getRotation();
                stopRotation();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentSongIndex < songList.size() - 1) {
                currentSongIndex++;
                Song nextSong = songList.get(currentSongIndex);
                updateUI(nextSong);
                playMusic(nextSong);

                isPlaying = true;
                isPaused = false;
                currentRotation = 0f; // 重置旋转角度，因为是播放新歌
                ivMusic.setRotation(0f); // 重置图片视图旋转
                rotateImageView(); // 开始新的旋转
                handler.postDelayed(updateProgress, 1000); // 每秒更新进度
            }
        });


        btnExit.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        updateProgress = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) { // Only update progress while playing
                    handler.sendEmptyMessage(1);
                    handler.postDelayed(this, 1000); // Update every 1 second
                }
            }
        };
        handler.postDelayed(updateProgress, 1000);


    }

    private void updateUI(Song song) {
        songName.setText(song.getSongName());
        ivMusic.setImageResource(song.getImageResourceId());
        tvTotal.setText(formatDuration(song.getSongDuration()));
        currentRotation = 0f; // Reset rotation angle when changing song
        ivMusic.setRotation(0f); // Reset image view rotation
    }

    private void playMusic(Song song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, song.getSongUri());
            mediaPlayer.prepareAsync(); // Use prepareAsync() for asynchronous preparation
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start(); // Start playback
                    seekBar.setMax(mp.getDuration()); // Set max of seekbar
                    isPlaying = true; // Update playing state
                    rotateImageView(); // Start rotation
                    // Start updating the progress (reference the runnable declared in onCreate)
                    handler.postDelayed(updateProgress, 1000); // Update every 1 second
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Handle the case when the song is finished
                    isPlaying = false;
                    stopRotation();
                    // Optionally: move to next song, etc.
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
    private void rotateImageView() {
        // Create the RotateAnimation if it doesn't exist
        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(currentRotation, currentRotation + 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(30000); // Rotate for 30 seconds (adjust as needed)
            rotateAnimation.setRepeatCount(Animation.INFINITE); // Repeat indefinitely
            rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // Use AccelerateDecelerateInterpolator
        } else {
            // If the RotateAnimation exists, reset its start and end angles
            rotateAnimation.setStartOffset((long) currentRotation);
            rotateAnimation.setRepeatCount(Animation.INFINITE); // Repeat indefinitely
            rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // Use AccelerateDecelerateInterpolator
        }
        rotateAnimation.setFillAfter(true); // Apply final state
        ivMusic.startAnimation(rotateAnimation);
        // Update currentRotation for the next rotation
        currentRotation += 360;
    }

    private void stopRotation() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivMusic.setRotation(currentRotation);
                }
            }, 10); // Wait for 10 milliseconds
        }
    }

}