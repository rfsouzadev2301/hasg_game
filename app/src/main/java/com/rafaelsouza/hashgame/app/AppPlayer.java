package com.rafaelsouza.hashgame.app;

import me.rafaelsouza.jogodavelha.game.entities.player.Player;

public class AppPlayer {

    private Player player;
    private int points = 0;
    public AppPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints() {
        this.points += 1;
    }

    public void resetPoints() {
        this.points = 0;
    }
}
