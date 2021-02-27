package com.dotpot.app.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ObjectTransporter;
import com.dotpot.app.utl;

public class GameActivity extends BaseActivity {

    Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String gameId = getIntent().getStringExtra("gameId");
        game = (Game)ObjectTransporter.getInstance().remove(gameId);
        if(game == null)
        {
            utl.toast(ctx,getstring(R.string.error_msg_try_again));
            finish();
            return;
        }

        TextView head =findViewById(R.id.head);

        if(game.isPlayer1Won()){
            head.setText("You Won !! INR "+game.getPossibleAward());
        }
        else {
            head.setText("You Lost !!");
        }

        RestAPI.getInstance().finishGame(game,new GenricObjectCallback<Game>(){
            @Override
            public void onEntity(Game data) {
                utl.snack(act,"Game Finished !!!");
                RestAPI.getInstance().invalidateCacheWalletAndTxns();
                WalletViewModel.getInstance().refresh("");
            }

            @Override
            public void onError(String message) {
                utl.snack(act,"Error !!! "+message);
            }
        });


    }
}
