package com.ben4808.connectfour;

import com.ben4808.connectfour.ConnectFour.GameWinner;
import com.ben4808.connectfour.ConnectFour.Player;
import com.ben4808.connectfour.ConnectFour.SquareState;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class GameView extends View {
	private int borderSize;
	private int squareSize;
	private ConnectFour cf;
	
	private int curCol;
	private Paint animColor;
	private int animCol;
	private int animY;
	
	public static final Paint black, lightGray, gray, blue, red, white, yellow;
	
	static {
		black = new Paint(Paint.ANTI_ALIAS_FLAG);
		black.setColor(Color.BLACK);
		lightGray = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightGray.setColor(Color.LTGRAY);
		gray = new Paint(Paint.ANTI_ALIAS_FLAG);
		gray.setColor(Color.DKGRAY);
		blue = new Paint(Paint.ANTI_ALIAS_FLAG);
		blue.setColor(Color.BLUE);
		red = new Paint(Paint.ANTI_ALIAS_FLAG);
		red.setColor(Color.RED);
		white = new Paint(Paint.ANTI_ALIAS_FLAG);
		white.setColor(Color.WHITE);
		yellow = new Paint(Paint.ANTI_ALIAS_FLAG);
		yellow.setColor(Color.YELLOW);
		yellow.setStrokeWidth(10);
	}
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		borderSize = 0;
		squareSize = 0;
		cf = new ConnectFour();
		
		curCol = -1;
		animColor = blue;
		animCol = -1;
		animY = 0;
		
		this.setOnTouchListener(new GameTouchListener());
	}
	
	// orientation changes, etc.
	public ConnectFour getState() { return cf; }
	public void setState(ConnectFour c) { cf = c; }
	
	@Override
	public void onDraw(Canvas canvas) {
		borderSize = getResources().getDimensionPixelSize(R.dimen.game_border_size);
		squareSize = (getWidth() - 2*borderSize) / 7;
		canvas.drawRect(0, 0, getWidth(), getHeight(), lightGray);
		canvas.drawRect(0, squareSize, getWidth(), getHeight(), gray);
		canvas.drawRect(borderSize, borderSize + squareSize, getWidth() - borderSize, getHeight() - borderSize, black);
		
		int outerRadius = squareSize / 2;
		int innerRadius = outerRadius - borderSize;
		for(int i=0; i < cf.BOARD_ROWS; i++) {
			for(int j=0; j < cf.BOARD_COLS; j++) {
				int cx = borderSize + j * squareSize + outerRadius;
				int cy = borderSize + (i+1) * squareSize + outerRadius;
				
				SquareState state = cf.getSquareState(i, j);
				Paint color = null;
				switch(state) {
				case EMPTY: color = white; break;
				case BLUE: color = blue; break;
				case RED: color = red; break;
				}
				
				canvas.drawCircle(cx, cy, outerRadius, color == white ? lightGray : gray);
				canvas.drawCircle(cx, cy, innerRadius, color);
			}
		}
		
		if(curCol >= 0) {
			int cx = curCol * squareSize + borderSize + outerRadius;
			int cy = outerRadius;
			Paint color = cf.getTurn() == Player.BLUE ? blue : red;
			canvas.drawCircle(cx, cy, outerRadius, gray);
			canvas.drawCircle(cx, cy, innerRadius, color);
		}
		
		if(animCol >= 0) {
			int cx = animCol * squareSize + borderSize + outerRadius;
			int cy = animY + outerRadius;
			canvas.drawCircle(cx, cy, outerRadius, gray);
			canvas.drawCircle(cx, cy, innerRadius, animColor);
		}
		
		if(cf.isWinner()) {
			GameWinner winner = cf.getWinner();
			int row = winner.winnerRow;
			int col = winner.winnerCol;
			int cx1 = col * squareSize + borderSize + outerRadius;
			int cy1 = (row + 1) * squareSize + borderSize + outerRadius;
			
			int cx2 = 0, cy2 = 0;
			switch(winner.winnerDir) {
			case E:
				cx2 = cx1 + 3 * squareSize;
				cy2 = cy1;
				break;
			case N:
				cx2 = cx1;
				cy2 = cy1 - 3 * squareSize;
				break;
			case NE:
				cx2 = cx1 + 3 * squareSize;
				cy2 = cy1 - 3 * squareSize;
				break;
			case NW:
				cx2 = cx1 - 3 * squareSize;
				cy2 = cy1 - 3 * squareSize;
				break;
			}
			
			canvas.drawLine(cx1, cy1, cx2, cy2, yellow);
		}
	}
	
	public void updateOtherViews() {
		Activity activity = (Activity) getContext();
		TextView scoreView = (TextView)activity.findViewById(R.id.scores);
		TextView turnLabel = (TextView)activity.findViewById(R.id.turn_text);
		TurnIcon turnIcon = (TurnIcon)activity.findViewById(R.id.turn_icon);
		Button newGameButton = (Button)activity.findViewById(R.id.new_game_button);
		
		if (cf.isWinner()) {
			turnLabel.setText(R.string.winner_label);
			turnIcon.setVisibility(VISIBLE);
			turnIcon.setColor(cf.getWinner().winner == Player.BLUE ? blue : red);
			newGameButton.setVisibility(VISIBLE);
		}
		else if (cf.isDraw()) {
			turnLabel.setText(R.string.draw_label);
			turnIcon.setVisibility(GONE);
			newGameButton.setVisibility(VISIBLE);
		}
		else {
			turnLabel.setText(R.string.turn_label);
			turnIcon.setVisibility(VISIBLE);
			turnIcon.setColor(cf.getTurn() == Player.BLUE ? blue : red);
			newGameButton.setVisibility(INVISIBLE);
		}
		
		String text = "<font color='blue'>" + cf.getScore(Player.BLUE) + "</font><font color='black'> - " +
				"</font><font color='red'>" + cf.getScore(Player.RED);
		scoreView.setText(Html.fromHtml(text));
		
		scoreView.invalidate();
		turnLabel.invalidate();
		turnIcon.invalidate();
		newGameButton.invalidate();
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        
        setMeasuredDimension(size, size);
	}
	
	public class GameTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// animation still happening
			if(animCol > 0)
				return true;
			
			if(cf.isWinner() || cf.isDraw())
				return true;
			
			int x = (int)(event.getX());
			
			if (x >= borderSize && x < getWidth() - borderSize) {
				int col = (x - borderSize) / squareSize;
				if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
					curCol = col;
				} 
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					if(curCol >= 0 && cf.getOpenSquaresInCol(curCol) > 0)
						startMoveAnimation(cf.getTurn(), curCol);
					curCol = -1;
				}
			}
			else {
				curCol = -1;
			}
			
			invalidate();
			return true;
		}
	}
	
	public void startMoveAnimation(Player turn, final int col) {
		int outerRadius = squareSize / 2;
		int cy1 = outerRadius;
		int cy2 = cf.getOpenSquaresInCol(col) * squareSize + borderSize + outerRadius;
		animColor = turn == Player.BLUE ? blue : red;
		animCol = col;
		animY = cy1;
		
		ValueAnimator animator = ValueAnimator.ofInt(cy1, cy2);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setDuration(40 * cf.getOpenSquaresInCol(col));
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator anim) {
				animY = (Integer)anim.getAnimatedValue();
				invalidate();
			}
		});
		animator.addListener(new AnimatorListener() {
			@Override public void onAnimationCancel(Animator arg0) {}

			@Override
			public void onAnimationEnd(Animator arg0) {
				animCol = -1;
				cf.makeMove(col);
				updateOtherViews();
				invalidate();
			}

			@Override public void onAnimationRepeat(Animator arg0) {}
			@Override public void onAnimationStart(Animator arg0) {}
		});
		animator.start();
	}
	
	public void startNewGame(boolean switchTurn) {
		cf.newGame(switchTurn);
		updateOtherViews();
		invalidate();
	}
	
	public void resetScore() {
		cf.reset();
		updateOtherViews();
		invalidate();
	}
}
