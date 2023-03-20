package com.rafaelsouza.hashgame.app;

import android.graphics.drawable.Drawable;

import me.rafaelsouza.jogodavelha.game.enums.PieceType;

public class AppPiece {
    private PieceType pieceType;
    private Drawable pieceResource;

    public AppPiece(PieceType pieceType, Drawable pieceResource) {
        this.pieceType = pieceType;
        this.pieceResource = pieceResource;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public Drawable getPieceResource() {
        return pieceResource;
    }

    public void setPieceResource(Drawable pieceResource) {
        this.pieceResource = pieceResource;
    }
}
