package com.rafaelsouza.hashgame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DuoSelectionFragment extends Fragment {
    private Button selectPlay;
    private EditText namePlayer1, namePlayer2;

    public DuoSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_duo_selection, container, false);
        initComponents(view);
        eventsListener();

        return view;
    }

    private void initComponents(View view){
        selectPlay = view.findViewById(R.id.select_play);
        namePlayer1 = view.findViewById(R.id.name_player_O);
        namePlayer2 = view.findViewById(R.id.name_player_x);
    }

    private void eventsListener(){

        selectPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer media = new MediaPlayer().create(getActivity().getApplication(), R.raw.select_sound);
                media.start();

                Intent intent = new Intent(getActivity().getApplication(), DuoGameActivity.class);
                Bundle data = new Bundle();
                data.putString("namePlayer1",  namePlayer1.getText().toString());
                data.putString("namePlayer2",  namePlayer2.getText().toString());
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }
}