package com.ben4808.connectfour;

import java.io.Serializable;

import android.content.SharedPreferences;

@SuppressWarnings("serial")
public class ConnectFour implements Serializable {
	public enum Player {BLUE, RED};
	public enum SquareState {EMPTY, BLUE, RED};
	public enum Direction {E, N, NE, NW};
	
	public final int BOARD_ROWS = 6;
	public final int BOARD_COLS = 7;
	
	private SquareState[][] squares;
	private Player firstTurn;
	private Player turn;
	
	private int blueScore;
	private int redScore;
	
	private boolean mIsWinner;
	private boolean mIsDraw;
	private GameWinner winner;
	
	public ConnectFour() {
		squares = new SquareState[BOARD_ROWS][BOARD_COLS];
		for(int i=0; i < BOARD_ROWS; i++) {
			for(int j=0; j < BOARD_COLS; j++) {
				squares[i][j] = SquareState.EMPTY;
			}
		}
		firstTurn = Player.BLUE;
		turn = Player.BLUE;
		
		blueScore = 0;
		redScore = 0;
		
		mIsWinner = false;
		mIsDraw = false;
		winner = new GameWinner();
	}
	
	public Player getTurn() { return turn; }
	public int getScore(Player player) { return player == Player.BLUE ? blueScore : redScore; }
	public boolean isWinner() { return mIsWinner; }
	public boolean isDraw() { return mIsDraw; }
	public GameWinner getWinner() { return winner; }
	
	public SquareState getSquareState(int row, int col) {
		if (!inBounds(row, col))
			return null;
		return squares[row][col];
	}
	
	public int getOpenSquaresInCol(int col) {
		int total = 0;
		for(int row = 0; row < BOARD_ROWS && squares[row][col] == SquareState.EMPTY; row++)
			total++;
		return total;
	}
	
	public void incrementScore(SquareState color) { 
		if (color == SquareState.BLUE) blueScore++;
		if (color == SquareState.RED) redScore++;
	}
	
	public void setSquareState(int row, int col, SquareState newState) {
		if (!inBounds(row, col))
			return;
		squares[row][col] = newState;
	}
	
	private boolean inBounds(int row, int col) {
		return row >= 0 && col >= 0 && row < BOARD_ROWS && col < BOARD_COLS;
	}
	
	public void makeMove(int col) {
		if (col < 0 || col >= BOARD_COLS)
			return;
		
		// check if column is full
		if(squares[0][col] != SquareState.EMPTY)
			return;
		
		int row;
		for(row = 0; row < BOARD_ROWS - 1; row++) {
			if(squares[row+1][col] != SquareState.EMPTY) break;
		}
		
		setSquareState(row, col, turn == Player.BLUE ? SquareState.BLUE : SquareState.RED);
		if (!checkAndProcessWinner())
			turn = turn == Player.BLUE ? Player.RED : Player.BLUE;
	}
	
	private boolean checkAndProcessWinner() {
		// check for winner
		Direction wDir = null;
		for(int row = BOARD_ROWS - 1; row >=0; row--) {
			for(int col = 0; col < BOARD_COLS; col++) {
				if (squares[row][col] == SquareState.EMPTY) continue;
				
				if(col < BOARD_COLS - 3)
					if(squares[row][col] == squares[row][col+1] && squares[row][col] == squares[row][col+2]
							&& squares[row][col] == squares[row][col+3])
						wDir = Direction.E;
			    if(col < BOARD_COLS - 3 && row > 2)
					if(squares[row][col] == squares[row-1][col+1] && squares[row][col] == squares[row-2][col+2]
							&& squares[row][col] == squares[row-3][col+3])
						wDir = Direction.NE;
				if(row > 2) 
					if(squares[row][col] == squares[row-1][col] && squares[row][col] == squares[row-2][col]
							&& squares[row][col] == squares[row-3][col])
						wDir = Direction.N;
				if(col > 2 && row > 2)
					if(squares[row][col] == squares[row-1][col-1] && squares[row][col] == squares[row-2][col-2]
							&& squares[row][col] == squares[row-3][col-3])
						wDir = Direction.NW;
				
				if(wDir != null) {
					mIsWinner = true;
					winner.winner = turn;
					winner.winnerRow = row;
					winner.winnerCol = col;
					winner.winnerDir = wDir;
					if(turn == Player.BLUE)
						blueScore++;
					else
						redScore++;
					return true;
				}
			}
		}
		
		// check for draw
		boolean draw = true;
		for(int col = 0; col < BOARD_COLS; col++) {
			if (squares[0][col] == SquareState.EMPTY)
				draw = false;
		}
		if(draw) {
			mIsDraw = true;
			return true;
		}
		
		return false;
	}
	
	public void newGame(boolean switchTurn) {
		for(int i=0; i < BOARD_ROWS; i++) {
			for(int j=0; j < BOARD_COLS; j++) {
				squares[i][j] = SquareState.EMPTY;
			}
		}
		
		if(switchTurn) {
			turn = firstTurn == Player.BLUE ? Player.RED : Player.BLUE;
			firstTurn = turn;
		}
		else
			turn = firstTurn;
		
		mIsWinner = false;
		mIsDraw = false;
	}
	
	public void reset() {
		blueScore = 0;
		redScore = 0;
	}
	
	public class GameWinner {
		public Player winner;
		public int winnerRow;
		public int winnerCol;
		public Direction winnerDir;
		
		public GameWinner() {
			winner = Player.BLUE;
			winnerRow = 0;
			winnerCol = 0;
			winnerDir = Direction.E;
		}
	}
	
	public void loadIntoPreferences(SharedPreferences.Editor editor) {
		StringBuilder board = new StringBuilder();
		for(int i=0; i < BOARD_ROWS; i++) {
			for(int j=0; j < BOARD_COLS; j++) {
				board.append(squares[i][j].toString() + ",");
			}
		}
		editor.putString("board", board.toString());
		
		editor.putString("firstTurn", firstTurn.toString());
		editor.putString("turn", turn.toString());
		editor.putInt("blueScore", blueScore);
		editor.putInt("redScore", redScore);
		
		editor.putBoolean("mIsWinner", mIsWinner);
		editor.putBoolean("mIsDraw", mIsDraw);
		
		editor.putString("wWinner", winner.winner.toString());
		editor.putInt("wWinnerRow", winner.winnerRow);
		editor.putInt("wWinnerCol", winner.winnerCol);
		editor.putString("wWinnerDir", winner.winnerDir.toString());
	}
	
	public void restoreFromPreferences(SharedPreferences prefs) {
		String[] board = prefs.getString("board", "").split(",");
		int curI = 0;
		for(int i=0; i < BOARD_ROWS; i++) {
			for(int j=0; j < BOARD_COLS; j++) {
				squares[i][j] = SquareState.valueOf(board[curI]);
				curI++;
			}
		}
		
		firstTurn = Player.valueOf(prefs.getString("firstTurn", ""));
		turn = Player.valueOf(prefs.getString("turn", ""));
		blueScore = prefs.getInt("blueScore", 0);
		redScore = prefs.getInt("redScore", 0);
		
		mIsWinner = prefs.getBoolean("mIsWinner", false);
		mIsDraw = prefs.getBoolean("mIsDraw", false);
		
		winner.winner = Player.valueOf(prefs.getString("wWinner", ""));
		winner.winnerRow = prefs.getInt("wWinnerRow", 0);
		winner.winnerCol = prefs.getInt("wWinnerCol", 0);
		winner.winnerDir = Direction.valueOf(prefs.getString("wWinnerDir", ""));
	}
}
