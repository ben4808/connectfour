package com.ben4808.connectfour;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.content.SharedPreferences;

public class GameActivity extends Activity {
	private static final int RESET_GAME_ID = Menu.FIRST;
	private static final int RESET_SCORES_ID = Menu.FIRST + 1;
	
	public static final String PREFS_NAME = "ConnectFourPrefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		GameView gameView = (GameView) findViewById(R.id.game_view);
		if(savedInstanceState != null) {
			//gameView.setState((ConnectFour)savedInstanceState.getSerializable("cf_state"));
		}
		gameView.updateOtherViews();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/*GameView gameView = (GameView) findViewById(R.id.game_view);
		outState.putSerializable("cf_state", gameView.getState());*/
	}
	
	@Override
	protected void onPause(){
        super.onPause();

        GameView gameView = (GameView) findViewById(R.id.game_view);
        ConnectFour cf = gameView.getState();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.clear();
        cf.loadIntoPreferences(editor);
        editor.commit();
    }
	
	@Override
	protected void onResume(){
        super.onResume();

        GameView gameView = (GameView) findViewById(R.id.game_view);
        ConnectFour cf = gameView.getState();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if(settings.getString("board", null) != null) {
        	cf.restoreFromPreferences(settings);
        	gameView.updateOtherViews();
        	gameView.invalidate();
        }
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, RESET_GAME_ID, 0, R.string.menu_reset_game);
        menu.add(0, RESET_SCORES_ID, 0, R.string.menu_reset_scores);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	GameView gameView = (GameView) findViewById(R.id.game_view);
        switch(item.getItemId()) {
            case RESET_GAME_ID:
        		gameView.startNewGame(false);
                return true;
            case RESET_SCORES_ID:
        		gameView.resetScore();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
	
	// New Game button listener
	public void startNewGame(View w) {
		GameView gameView = (GameView) findViewById(R.id.game_view);
		gameView.startNewGame(true);
	}
}
