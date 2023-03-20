package com.rafaelsouza.hashgame;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AnimateLayout {
    static void layoutInputCrossfade(ConstraintLayout layout) {

        layout.setAlpha(0f);
        layout.setVisibility(View.VISIBLE);

        layout.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100l);
    }
    static void layoutOutputCrossfade(ConstraintLayout layout) {

        layout.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(100l);

        layout.setVisibility(View.GONE);
    }
}
