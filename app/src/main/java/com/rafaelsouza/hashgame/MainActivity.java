package com.rafaelsouza.hashgame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private TextView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer media = new MediaPlayer().create(MainActivity.this, R.raw.select_sound);
                media.start();
                Intent intent = new Intent(MainActivity.this, ModeSelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initObjects(){

        button = findViewById(R.id.play_button);
    }
}