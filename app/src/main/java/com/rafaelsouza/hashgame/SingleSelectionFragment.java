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
import android.widget.RadioButton;
public class SingleSelectionFragment extends Fragment {
    private RadioButton buttonEasy, buttonMedium, buttonHard;
    private Button machinePlay;
    private EditText namePlayer;

    public SingleSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_single_selection, container, false);
        initComponents(view);
        eventsListener();

        return view;
    }

    private void initComponents(View view){
        buttonEasy = view.findViewById(R.id.button_machine_difficulty_easy);
        buttonMedium = view.findViewById(R.id.button_machine_difficulty_medium);
        buttonHard = view.findViewById(R.id.button_machine_difficulty_hard);
        namePlayer = view.findViewById(R.id.name_player);
        machinePlay = view.findViewById(R.id.machine_play);
    }

    private void eventsListener(){

        buttonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer media = new MediaPlayer().create(getActivity().getApplication(), R.raw.insert_piece_sound);
                media.start();
            }
        });
        buttonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer media = new MediaPlayer().create(getActivity().getApplication(), R.raw.insert_piece_sound);
                media.start();
            }
        });
        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer media = new MediaPlayer().create(getActivity().getApplication(), R.raw.insert_piece_sound);
                media.start();
            }
        });

        machinePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaPlayer media = new MediaPlayer().create(getActivity().getApplication(), R.raw.select_sound);
                media.start();

                Intent intent = new Intent(getActivity().getApplication(), SingleGameActivity.class);
                Bundle data = new Bundle();
                data.putString("namePlayer",  namePlayer.getText().toString());

                if(buttonEasy.isChecked()){
                    data.putString("difficulty", "EASY");
                } else if (buttonMedium.isChecked()) {
                    data.putString("difficulty", "MEDIUM");
                } else{
                    data.putString("difficulty", "HARD");
                }

                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }
}