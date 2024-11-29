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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private LinkedList<Integer> playlist = new LinkedList<>();
    private boolean isAleatorio = false; // Aleatorio activado o desactivado
    private boolean isRepetir = false;  // Repetir activado o desactivado
    private int currentSongId = -1;
    private LinearLayout currentPlayingLayout;
    private List<Integer> allSongs; // Lista de todas las canciones para el modo aleatorio
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

        // Inicializar la lista de todas las canciones para el modo aleatorio
        allSongs = new ArrayList<>();
        allSongs.add(TRACK_KEVIN_KAARL);
        allSongs.add(TRACK_EMPIRE);
        allSongs.add(TRACK_NIRVANA);
        allSongs.add(TRACK_ARTEMAS);
        allSongs.add(TRACK_LUIS_MIGUEL);

        // Set listeners for the Aleatorio and Repetir buttons
        findViewById(R.id.cmdModoAleatorio).setOnClickListener(v -> toggleAleatorio(v));
        findViewById(R.id.cmdModoRepeticion).setOnClickListener(v -> toggleRepetir(v));
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
            Toast.makeText(this, "Canción añadida a la fila de reproducción", Toast.LENGTH_SHORT).show();
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
        if (isRepetir) {
            // Reproduce la misma canción nuevamente
            playSong(currentSongId, findViewById(getLayoutIdForSong(currentSongId)));
            return;
        }

        if (isAleatorio) {
            // Si el modo aleatorio está activado, seleccionar una canción aleatoria que no se haya reproducido
            if (allSongs.size() > 0) {
                Collections.shuffle(allSongs);
                int nextSongId = allSongs.get(0); // Obtener la primera canción aleatoria de la lista
                allSongs.remove(0); // Eliminar la canción de la lista para que no se repita
                playSong(nextSongId, findViewById(getLayoutIdForSong(nextSongId)));
                return;
            }
            // Si ya se han reproducido todas las canciones, recargar la lista de canciones
            allSongs.add(TRACK_KEVIN_KAARL);
            allSongs.add(TRACK_EMPIRE);
            allSongs.add(TRACK_NIRVANA);
            allSongs.add(TRACK_ARTEMAS);
            allSongs.add(TRACK_LUIS_MIGUEL);
        }

        if (!playlist.isEmpty()) {
            int nextSongId = playlist.poll();  // Saca la primera canción de la cola
            playSong(nextSongId, findViewById(getLayoutIdForSong(nextSongId)));
        } else {
            Toast.makeText(this, "Fin de la cola", Toast.LENGTH_SHORT).show();
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

    // Método para activar/desactivar el modo aleatorio
    public void toggleAleatorio(View view) {
        isAleatorio = !isAleatorio;

        if (isAleatorio) {
            // Cambiar fondo del botón a verde
            view.setBackgroundColor(Color.GREEN);
            // Reproducir una canción aleatoria si no está sonando ninguna canción
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                Collections.shuffle(allSongs);
                int nextSongId = allSongs.get(0);
                allSongs.remove(0); // Eliminar la canción reproducida de la lista
                playSong(nextSongId, findViewById(getLayoutIdForSong(nextSongId)));
            }
            Toast.makeText(this, "Modo aleatorio activado", Toast.LENGTH_SHORT).show();
        } else {
            // Revertir fondo del botón al color original
            view.setBackgroundColor(getResources().getColor(R.color.colorButtonDefault));
            Toast.makeText(this, "Modo aleatorio desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para activar/desactivar el modo repetición
    public void toggleRepetir(View view) {
        isRepetir = !isRepetir;

        if (isRepetir) {
            // Cambiar fondo del botón a verde
            view.setBackgroundColor(Color.GREEN);
            Toast.makeText(this, "Modo repetición activado", Toast.LENGTH_SHORT).show();
        } else {
            // Revertir fondo del botón al color original
            view.setBackgroundColor(getResources().getColor(R.color.colorButtonDefault));
            Toast.makeText(this, "Modo repetición desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
