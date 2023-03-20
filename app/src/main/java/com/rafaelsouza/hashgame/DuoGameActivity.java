package com.rafaelsouza.hashgame;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rafaelsouza.hashgame.app.AppPlayer;

import me.rafaelsouza.jogodavelha.board.entities.Board;
import me.rafaelsouza.jogodavelha.game.entities.Game;
import me.rafaelsouza.jogodavelha.game.entities.GamePiece;
import me.rafaelsouza.jogodavelha.game.entities.GamePosition;
import me.rafaelsouza.jogodavelha.game.entities.MatchReports;

import me.rafaelsouza.jogodavelha.game.entities.UI;
import me.rafaelsouza.jogodavelha.game.entities.player.Player;
import me.rafaelsouza.jogodavelha.game.entities.player.enums.PlayerType;
import me.rafaelsouza.jogodavelha.game.enums.GameStatus;
import me.rafaelsouza.jogodavelha.game.enums.PieceType;

public class DuoGameActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private ImageView a1, a2, a3, b1, b2, b3, c1, c2, c3, piecePlayerTurn, lineWinner;
    private final ImageView[][] pieceHouse = new ImageView[3][3];
    private TextView namePlayerTurn, namePlayerWinner, textResult;
    private ImageView[] player1_points = new ImageView[2], player2_points = new ImageView[2];
    private LinearLayout headerInGame, headerWinner;
    private ConstraintLayout headVelha, beforeMatch, inGame, afterRound, afterMatch, footer;
    private FrameLayout adViewBannerFrame;
    private Bundle data;
    private String namePlayer1, namePlayer2, toastDoubleTapToExit;
    private AppPlayer player1, player2;
    private Player[] orderPlayers;
    private Game game;
    private UI ui;
    private int round = 0, match = 0;
    private boolean doubleBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        data = getIntent().getExtras();

        initLayouts();
        initObjects();
        listenerPositionToPlay();

        pieceHouse[0][0] = a1; pieceHouse[0][1] = a2; pieceHouse[0][2] = a3;
        pieceHouse[1][0] = b1; pieceHouse[1][1] = b2; pieceHouse[1][2] = b3;
        pieceHouse[2][0] = c1; pieceHouse[2][1] = c2; pieceHouse[2][2] = c3;

        ui = new UI() {
            @Override
            public void updateHash(Board board) {
                updateBoard(board);
                updateTurn();
            }
            @Override
            public void finished(MatchReports matchReports) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);

                if(matchReports.getGameStatus() == GameStatus.WINNER){
                    layoutWinner();
                    namePlayerWinner.setText(matchReports.getWinner().getName());
                    updatePoints(matchReports);

                }else if(matchReports.getGameStatus() == GameStatus.VELHA){
                    layoutVelha();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        if(player1.getPoints() == 2 || player2.getPoints() == 2){
                            match++;
                            afterMatch();
                            if(match % 2.0 == 0.0){
                                showInterstitialAd();
                            }
                        }else {
                            afterRound();
                        }
                    }
                }, 1500);
            }
        };

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adViewBannerFooter = new AdView(this);
        adViewBannerFooter.setAdSize(AdSize.SMART_BANNER);
        adViewBannerFooter.setAdUnitId(getString(R.string.adBanner_footer_in_game));
        adViewBannerFooter.loadAd(adRequest);
        adViewBannerFrame.addView(adViewBannerFooter);

        createPlayers();
        createPieces();
        beforeMatch();
    }
    private void initObjects() {
        a1 = findViewById(R.id.a1);
        a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);

        adViewBannerFrame = findViewById(R.id.adBanner_footer);

        headerInGame = findViewById(R.id.header_in_game);
        headerWinner = findViewById(R.id.header_winner);
        headVelha = findViewById(R.id.header_velha);
        lineWinner = findViewById(R.id.line_winner);

        piecePlayerTurn = findViewById(R.id.piecePlayerTurn);
        namePlayerTurn = findViewById(R.id.namePlayerTurn);
        namePlayerWinner = findViewById(R.id.namePlayerWinner);

        toastDoubleTapToExit = getResources().getString(R.string.double_tap_to_exit);
    }
    private void initLayouts(){
        beforeMatch = findViewById(R.id.layout_before_match);
        inGame = findViewById(R.id.layout_in_game);
        afterRound = findViewById(R.id.layout_after_round);
        afterMatch = findViewById(R.id.layout_after_match);
        footer = findViewById(R.id.footer);
    }
    private void beforeMatch(){
        AnimateLayout.layoutInputCrossfade(beforeMatch);
        inGame.setVisibility(View.GONE);
        afterRound.setVisibility(View.GONE);
        afterMatch.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);
        adViewBannerFrame.setVisibility(View.VISIBLE);

        ImageView resourcePiecePlayer1 = findViewById(R.id.before_select_piece_resource_player1);
        TextView textNamePlayer1 = findViewById(R.id.before_select_piece_name_player1);
        ImageView resourcePiecePlayer2 = findViewById(R.id.before_select_piece_resource_player2);
        TextView textNamePlayer2 = findViewById(R.id.before_select_piece_name_player2);

        Button nextButton = findViewById(R.id.before_button_next);

        final FrameLayout VIEW = findViewById(R.id.adbanner_before_match);
        AdSize adSize = AdSize.getInlineAdaptiveBannerAdSize(271, 150);

        AdView adBannerAfterMatch = new AdView(this);
        adBannerAfterMatch.setAdSize(adSize);
        adBannerAfterMatch.setAdUnitId(getString(R.string.adBanner_before_after_game));
        AdRequest adRqst = new AdRequest.Builder().build();
        adBannerAfterMatch.loadAd(adRqst);
        VIEW.addView(adBannerAfterMatch);

        textNamePlayer1.setText(namePlayer1.toString());
        textNamePlayer2.setText(namePlayer2.toString());

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMatch();
                inGame();
            }
        });

        resourcePiecePlayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView srcPiecePlayer1 = findViewById(R.id.before_select_piece_resource_player1);
                ImageView srcPiecePlayer2 = findViewById(R.id.before_select_piece_resource_player2);
                Drawable DrawTemp;

                DrawTemp = srcPiecePlayer1.getDrawable();
                srcPiecePlayer1.setImageDrawable(srcPiecePlayer2.getDrawable());
                srcPiecePlayer2.setImageDrawable(DrawTemp);

                invertPieces();
            }
        });

        resourcePiecePlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView srcPiecePlayer1 = findViewById(R.id.before_select_piece_resource_player1);
                ImageView srcPiecePlayer2 = findViewById(R.id.before_select_piece_resource_player2);
                Drawable DrawTemp;

                DrawTemp = srcPiecePlayer1.getDrawable();
                srcPiecePlayer1.setImageDrawable(srcPiecePlayer2.getDrawable());
                srcPiecePlayer2.setImageDrawable(DrawTemp);

                invertPieces();
            }
        });

    }
    private void inGame(){
        AnimateLayout.layoutOutputCrossfade(beforeMatch);
        AnimateLayout.layoutInputCrossfade(inGame);
        AnimateLayout.layoutOutputCrossfade(afterRound);
        AnimateLayout.layoutOutputCrossfade(afterMatch);;
        footer.setVisibility(View.VISIBLE);
        adViewBannerFrame.setVisibility(View.VISIBLE);
    }
    private void afterRound(){
        beforeMatch.setVisibility(View.GONE);
        AnimateLayout.layoutOutputCrossfade(inGame);
        AnimateLayout.layoutInputCrossfade(afterRound);
        afterMatch.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);
        adViewBannerFrame.setVisibility(View.VISIBLE);

        Button buttonStartQuickly = findViewById(R.id.button_start_quickly_play_round);
        TextView secondsForNextRound = findViewById(R.id.seconds_for_next_round);

        TextView textPlayer1 = findViewById(R.id.after_round_name_player1);
        TextView textPlayer2 = findViewById(R.id.after_round_name_player2);
        player1_points[0] = findViewById(R.id.after_round_point_1_player1);
        player1_points[1] = findViewById(R.id.after_round_point_2_player1);
        player2_points[0] = findViewById(R.id.after_round_point_1_player2);
        player2_points[1] = findViewById(R.id.after_round_point_2_player2);


        textPlayer1.setText(namePlayer1.toString());
        textPlayer2.setText(namePlayer2.toString());
        printPoints();

        secondsForNextRound.setText(String.valueOf(3));
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondsForNextRound.setText(String.valueOf(2));
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        secondsForNextRound.setText(String.valueOf(1));

                        Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(afterRound.getVisibility() != View.GONE){
                                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.select_sound);
                                    media.start();
                                    inGame();
                                    newRound();
                                }
                            }
                        }, 1000l);
                    }
                }, 1000l);
            }
        }, 1000l);

        buttonStartQuickly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.select_sound);
                media.start();
                inGame();
                newRound();
            }
        });
    }
    private void afterMatch(){
        beforeMatch.setVisibility(View.GONE);
        AnimateLayout.layoutOutputCrossfade(inGame);
        afterRound.setVisibility(View.GONE);
        AnimateLayout.layoutInputCrossfade(afterMatch);
        footer.setVisibility(View.GONE);
        adViewBannerFrame.setVisibility(View.VISIBLE);

        textResult = findViewById(R.id.winner_or_loser_after_match);

        TextView textPlayer1 = findViewById(R.id.after_match_name_player1);
        TextView textPlayer2 = findViewById(R.id.after_match_name_player2);
        player1_points[0] = findViewById(R.id.after_match_point_1_player1);
        player1_points[1] = findViewById(R.id.after_match_point_2_player1);
        player2_points[0] = findViewById(R.id.after_match_point_1_player2);
        player2_points[1] = findViewById(R.id.after_match_point_2_player2);

        ImageView resourcePiecePlayer1 = findViewById(R.id.after_select_piece_resource_player1);
        TextView textNamePlayer1 = findViewById(R.id.after_select_piece_name_player1);
        ImageView resourcePiecePlayer2 = findViewById(R.id.after_select_piece_resource_player2);
        TextView textNamePlayer2 = findViewById(R.id.after_select_piece_name_player2);

        Button nextButton = findViewById(R.id.after_button_next);

        FrameLayout view = findViewById(R.id.adbanner_after_match);
        AdSize adSize = AdSize.getInlineAdaptiveBannerAdSize(271, 150);

        AdView adBannerAfterMatch = new AdView(this);
        adBannerAfterMatch.setAdSize(adSize);
        adBannerAfterMatch.setAdUnitId(getString(R.string.adBanner_before_after_game));
        AdRequest adRqst = new AdRequest.Builder().build();
        adBannerAfterMatch.loadAd(adRqst);
        view.addView(adBannerAfterMatch);

        textPlayer1.setText(namePlayer1.toString());
        textPlayer2.setText(namePlayer2.toString());
        textNamePlayer1.setText(namePlayer1.toString());
        textNamePlayer2.setText(namePlayer2.toString());

        if(player1.getPlayer().getPiece().getSymbol() == PieceType.PIECE_O){
            resourcePiecePlayer1.setImageResource(R.drawable.piece_o);
            resourcePiecePlayer2.setImageResource(R.drawable.piece_x);
        }else{
            resourcePiecePlayer1.setImageResource(R.drawable.piece_x);
            resourcePiecePlayer2.setImageResource(R.drawable.piece_o);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMatch();
                inGame();
            }
        });

        resourcePiecePlayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView srcPiecePlayer1 = findViewById(R.id.after_select_piece_resource_player1);
                ImageView srcPiecePlayer2 = findViewById(R.id.after_select_piece_resource_player2);
                Drawable DrawTemp;

                DrawTemp = srcPiecePlayer1.getDrawable();
                srcPiecePlayer1.setImageDrawable(srcPiecePlayer2.getDrawable());
                srcPiecePlayer2.setImageDrawable(DrawTemp);

                invertPieces();
            }
        });

        resourcePiecePlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView srcPiecePlayer1 = findViewById(R.id.after_select_piece_resource_player1);
                ImageView srcPiecePlayer2 = findViewById(R.id.after_select_piece_resource_player2);
                Drawable DrawTemp;

                DrawTemp = srcPiecePlayer1.getDrawable();
                srcPiecePlayer1.setImageDrawable(srcPiecePlayer2.getDrawable());
                srcPiecePlayer2.setImageDrawable(DrawTemp);

                invertPieces();
            }
        });

        printResult();
    }
    private void listenerPositionToPlay(){

        a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('a', 1));
                }
            }
        });
        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('a', 2));
                }
            }
        });
        a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('a', 3));
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('b', 1));
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('b', 2));
                }
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('b', 3));
                }
            }
        });
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('c', 1));
                }
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('c', 2));
                }
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.getMatchData().getGameStatus() == GameStatus.RUNNING){
                    MediaPlayer media = new MediaPlayer().create(DuoGameActivity.this, R.raw.insert_piece_sound);
                    media.start();
                    game.playerMove(new GamePosition('c', 3));
                }
            }
        });
    }
    private void showInterstitialAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.adInterstitial), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(DuoGameActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
    private void newGame(){
        clearBoard();
        game = new Game(ui, orderPlayers);
        game.startGame();
        updateTurn();
        layoutInRound();
    }
    private void newRound(){
        round++;
        invertPlayerOrder();
        newGame();
    }
    private void newMatch(){
        round = 0;
        player1.resetPoints();
        player2.resetPoints();
        newRound();
    }
    private void updatePoints(@NonNull MatchReports matchReports){
        if(matchReports.getWinner() == player1.getPlayer()){
            player1.addPoints();
        }else if(matchReports.getWinner() == player2.getPlayer()){
            player2.addPoints();
        }
    }
    private void layoutInRound(){
        headerInGame.setVisibility(View.VISIBLE);
        headerWinner.setVisibility(View.GONE);
        headVelha.setVisibility(View.GONE);
        piecePlayerTurn.setVisibility(View.VISIBLE);
        AnimateLayout.layoutInputCrossfade(footer);
    }
    private void layoutWinner(){
        headerInGame.setVisibility(View.GONE);
        headerWinner.setVisibility(View.VISIBLE);
        headVelha.setVisibility(View.GONE);
        piecePlayerTurn.setVisibility(View.GONE);
        lineWinner();
    }
    private void layoutVelha(){
        headerInGame.setVisibility(View.GONE);
        headerWinner.setVisibility(View.GONE);
        headVelha.setVisibility(View.VISIBLE);
        piecePlayerTurn.setVisibility(View.GONE);
    }
    private void lineWinner(){

        switch (game.getMatchData().getLineWinner()){
            case LINE_1:
                lineWinner.setImageResource(R.drawable.winner_line_1);
                break;
            case LINE_2:
                lineWinner.setImageResource(R.drawable.winner_line_2);
                break;
            case LINE_3:
                lineWinner.setImageResource(R.drawable.winner_line_3);
                break;
            case COLUMN_1:
                lineWinner.setImageResource(R.drawable.winner_column_1);
                break;
            case COLUMN_2:
                lineWinner.setImageResource(R.drawable.winner_column_2);
                break;
            case COLUMN_3:
                lineWinner.setImageResource(R.drawable.winner_column_3);
                break;
            case DIAGONAL_A1_C3:
                lineWinner.setImageResource(R.drawable.winner_diagonal_a1_c3);
                break;
            case DIAGONAL_A3_C1:
                lineWinner.setImageResource(R.drawable.winner_diagonal_a3_c1);
                break;
        }
        lineWinner.setVisibility(View.VISIBLE);
    }
    private void clearBoard(){
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                pieceHouse[i][j].setImageResource(R.drawable.piece_void);
                pieceHouse[i][j].setEnabled(true);
            }
        }
        lineWinner.setVisibility(View.GONE);
    }
    private void updateBoard(Board board){

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                GamePiece piece = (GamePiece) board.getBoard()[i][j];

                if (piece != null){
                    switch(piece.getSymbol())   {
                        case PIECE_X:
                            pieceHouse[i][j].setImageResource(R.drawable.piece_x);
                            pieceHouse[i][j].setEnabled(false);
                            break;

                        case PIECE_O:
                            pieceHouse[i][j].setImageResource(R.drawable.piece_o);
                            pieceHouse[i][j].setEnabled(false);
                            break;

                        default:
                            pieceHouse[i][j].setEnabled(true);
                            break;
                    }
                }
            }
        }
    }
    private void updateTurn(){
        namePlayerTurn.setText(game.getMatchData().getPlayerTurn().getName());
        if(game.getMatchData().getPlayerTurn().getPiece().getSymbol() == PieceType.PIECE_O){
            piecePlayerTurn.setImageResource(R.drawable.piece_o);
            return;
        }
        if(game.getMatchData().getPlayerTurn().getPiece().getSymbol() == PieceType.PIECE_X){
            piecePlayerTurn.setImageResource(R.drawable.piece_x);
        }
    }
    private void printResult(){

        if(game.getMatchData().getWinner() == player1.getPlayer()){
            textResult.setText(namePlayer1 + "\n" + getText(R.string.winner));
        }else{
            textResult.setText(namePlayer2 + "\n" + getText(R.string.winner));
        }
        printPoints();
    }
    private void printPoints(){

        switch (player1.getPoints()){
            case 1:
                player1_points[0].setImageResource(R.drawable.blue_dot);
                break;
            case 2:
                player1_points[0].setImageResource(R.drawable.blue_dot);
                player1_points[1].setImageResource(R.drawable.blue_dot);
                break;
            default:
                player1_points[0].setImageResource(R.drawable.gray_dot);
                player1_points[1].setImageResource(R.drawable.gray_dot);
                break;
        }

        switch (player2.getPoints()){
            case 1:
                player2_points[0].setImageResource(R.drawable.green_dot);
                break;
            case 2:
                player2_points[0].setImageResource(R.drawable.green_dot);
                player2_points[1].setImageResource(R.drawable.green_dot);
                break;
            default:
                player2_points[0].setImageResource(R.drawable.gray_dot);
                player2_points[1].setImageResource(R.drawable.gray_dot);
                break;
        }
    }
    private Player[] getPlayerRandomOrder(Player player1, Player player2){

        switch ((int) ((Math.random() * 2 ) + 1)){
            case 1:
                return orderPlayers = new Player[]{player1, player2};
            case 2:
                return orderPlayers = new Player[]{player2, player1};
            default:
                return orderPlayers = new Player[]{player1, player2};

        }
    }
    private void invertPlayerOrder(){
        Player[] playersTemp = new Player[2];

        playersTemp[0] = orderPlayers[1];
        playersTemp[1] = orderPlayers[0];

        orderPlayers = playersTemp;
    }
    private void createPieces(){
        player1.getPlayer().setPiece(new GamePiece(null, PieceType.PIECE_O));
        player2.getPlayer().setPiece(new GamePiece(null, PieceType.PIECE_X));
    }
    private void invertPieces(){
        GamePiece pieceTemp = player1.getPlayer().getPiece();
        player1.getPlayer().setPiece(player2.getPlayer().getPiece());
        player2.getPlayer().setPiece(pieceTemp);
    }
    private void createPlayers(){

        namePlayer1 = data.getString("namePlayer1");
        namePlayer2 = data.getString("namePlayer2");

        if(namePlayer1.isEmpty()) {
            namePlayer1 = getResources().getString(R.string.player_1);
        }
        if(namePlayer2.isEmpty()) {
            namePlayer2 = getResources().getString(R.string.player_2);
        }
        player1 = new AppPlayer(new Player(namePlayer1, null, PlayerType.PLAYER));
        player2 = new AppPlayer(new Player(namePlayer2, null, PlayerType.MACHINE));

        orderPlayers = getPlayerRandomOrder(player1.getPlayer(), player2.getPlayer());
    }
    @Override
    public void onBackPressed() {
        if (doubleBack == true){
            Intent intent = new Intent(DuoGameActivity.this, ModeSelectionActivity.class);
            startActivity(intent);
            finish();
            return;

        }
        Toast toast = Toast.makeText(DuoGameActivity.this, toastDoubleTapToExit, Toast.LENGTH_SHORT);
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
