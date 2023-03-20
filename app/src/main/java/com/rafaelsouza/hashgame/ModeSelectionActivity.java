package com.rafaelsouza.hashgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.rafaelsouza.hashgame.adapters.FragsModeAdapter;

public class ModeSelectionActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String toastDoubleTapToExit;
    private boolean doubleBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        initObjects();

        tabLayout.setupWithViewPager(viewPager);

        FragsModeAdapter fragsModeAdapter = new FragsModeAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        fragsModeAdapter.addFragment(new SingleSelectionFragment(), getString(R.string.single));
        fragsModeAdapter.addFragment(new DuoSelectionFragment(), getString(R.string.duo));

        viewPager.setAdapter(fragsModeAdapter);
    }
    private void initObjects(){
        tabLayout = findViewById(R.id.tab_mode);
        viewPager = findViewById(R.id.view_mode);

        toastDoubleTapToExit = getResources().getString(R.string.double_tap_to_exit);
    }
    @Override
    public void onBackPressed() {

        if (doubleBack == true){
            finish();
            return;

        }
        Toast toast = Toast.makeText(ModeSelectionActivity.this, toastDoubleTapToExit, Toast.LENGTH_SHORT);
        toast.show();

        doubleBack = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBack = false;
            }
        }, 2000);
    }
}