package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private LinkedList<Integer> playlist = new LinkedList<>();
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int currentSongId = -1;
    private LinearLayout currentPlayingLayout;
    private static final int TRACK_KEVIN_KAARL = R.raw.kevin_kaarl_esta_noche;
    private static final int TRACK_EMPIRE = R.raw.empire_of_the_sun_we_are_the_people;
    private static final int TRACK_NIRVANA = R.raw.nirvana_smells_like_teen_spirit;
    private static final int TRACK_ARTEMAS = R.raw.artemas_i_like_the_way_you_kiss_me;
    private static final int TRACK_LUIS_MIGUEL = R.raw.luis_miguel_ahora_te_puedes_marchar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSongClickListeners();
    }

    private void setupSongClickListeners() {
        findViewById(R.id.Cancion1).setOnClickListener(v -> handleSongClick(TRACK_KEVIN_KAARL, v));
        findViewById(R.id.Cancion2).setOnClickListener(v -> handleSongClick(TRACK_EMPIRE, v));
        findViewById(R.id.Cancion3).setOnClickListener(v -> handleSongClick(TRACK_NIRVANA, v));
        findViewById(R.id.Cancion4).setOnClickListener(v -> handleSongClick(TRACK_ARTEMAS, v));
        findViewById(R.id.Cancion5).setOnClickListener(v -> handleSongClick(TRACK_LUIS_MIGUEL, v));
    }

    private void handleSongClick(int songId, View songView) {
        if (currentSongId == songId && mediaPlayer != null && mediaPlayer.isPlaying()) {
            pauseMusic();
            return;
        }

        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            playSong(songId, songView);
        } else {
            playlist.add(songId);
            Toast.makeText(this, "Canción añadida a la cola", Toast.LENGTH_SHORT).show();
        }
    }

    private void playSong(int songId, View songView) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, songId);
        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
        mediaPlayer.start();
        currentSongId = songId;

        highlightCurrentSong(songView);
    }

    private void highlightCurrentSong(View songView) {
        if (currentPlayingLayout != null) {
            currentPlayingLayout.setBackgroundColor(getResources().getColor(R.color.light_gray));
        }

        songView.setBackgroundColor(Color.GREEN);
        currentPlayingLayout = (LinearLayout) songView;
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(this, "Canción pausada", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextSong() {
        if (isRepeat) {
            playSong(currentSongId, currentPlayingLayout);
            return;
        }

        if (isShuffle) {
            Collections.shuffle(playlist);
        }

        if (!playlist.isEmpty()) {
            int nextSongId = playlist.poll();
            playSong(nextSongId, findViewById(getLayoutIdForSong(nextSongId)));
        } else {
            currentSongId = -1;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private int getLayoutIdForSong(int songId) {
        if (songId == TRACK_KEVIN_KAARL) {
            return R.id.Cancion1;
        } else if (songId == TRACK_EMPIRE) {
            return R.id.Cancion2;
        } else if (songId == TRACK_NIRVANA) {
            return R.id.Cancion3;
        } else if (songId == TRACK_ARTEMAS) {
            return R.id.Cancion4;
        } else if (songId == TRACK_LUIS_MIGUEL) {
            return R.id.Cancion5;
        }
        return -1; // Si no encuentra la canción, devuelve un valor predeterminado.
    }

    public void toggleShuffle(View view) {
        isShuffle = !isShuffle;
        Toast.makeText(this, isShuffle ? "Modo aleatorio activado" : "Modo aleatorio desactivado", Toast.LENGTH_SHORT).show();
    }

    public void toggleRepeat(View view) {
        isRepeat = !isRepeat;
        Toast.makeText(this, isRepeat ? "Modo repetición activado" : "Modo repetición desactivado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}