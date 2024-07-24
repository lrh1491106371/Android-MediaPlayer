package com.example.mediaplayer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private int imageResourceId;
    private String songName;
    private String artistName;
    private int songDuration;

    public Song(int imageResourceId, String songName, String artistName, int songDuration) {
        this.imageResourceId = imageResourceId;
        this.songName = songName;
        this.artistName = artistName;
        this.songDuration = songDuration;
    }

    protected Song(Parcel in) {
        imageResourceId = in.readInt();
        songName = in.readString();
        artistName = in.readString();
        songDuration = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageResourceId);
        parcel.writeString(songName);
        parcel.writeString(artistName);
        parcel.writeInt(songDuration);
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getSongDuration() {
        return songDuration;
    }
    public Uri getSongUri() {
        String songFileName = "";
        switch (songName) {
            case "AI":
                songFileName = "ai";
                break;
            case "drama":
                songFileName = "drama";
                break;
            case "愿与愁":
                songFileName = "yuanyuchou";
                break;
            default:
                throw new IllegalArgumentException("Unknown song: " + songName);
        }
        return Uri.parse("android.resource://com.example.mediaplayer/raw/" + songFileName);
    }

}
