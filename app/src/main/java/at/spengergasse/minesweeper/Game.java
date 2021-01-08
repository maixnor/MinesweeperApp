package at.spengergasse.minesweeper;
import android.widget.Toast;

import java.util.*;

import static at.spengergasse.minesweeper.Game.GameState.ZERO;
import static at.spengergasse.minesweeper.Game.ShowState.HIDDEN;
import static at.spengergasse.minesweeper.Game.ShowState.VISIBLE;

public class Game {

	public void restart() {
		setup(cols, rows, numBombs);
	}

	public enum ShowState {HIDDEN, VISIBLE, FLAGGED};
	public enum Result {WIN, HIT, VALID, INVALID};
	public enum GameState {BOMB(-1), ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8);		
	private final int num;

	GameState(int n)
		{
			num = n;
		}

	public GameState inc()
		{
			if (num >= 0 && num < 8)
				return GameState.values()[num+2];
				else throw new ArrayIndexOutOfBoundsException("Wrong value in enum GameState");
		}
	}

	private GameState[][] gameState;
	private ShowState[][] showState;
	private int numVisible;
	private int numFlagged;
	private int numBombs;
	private int cols, rows;

	public String[] getAllStringsOfAllCells() {
		String[] grid = new String[rows * cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				grid[cols * i + j] = getStringOfCell(j, i);
			}
		}

		return grid;
	}

	private String getStringOfCell(int row, int column) {
		switch (getShowState(row, column)) {
			case HIDDEN:
				return "X";
			case FLAGGED:
				return "F";
			case VISIBLE:
				switch (getGameState(row, column)) {
					case ONE:
						return "1";
					case TWO:
						return "2";
					case THREE:
						return "3";
					case FOUR:
						return "4";
					case FIVE:
						return "5";
					case SIX:
						return "6";
					case SEVEN:
						return "7";
					case EIGHT:
						return "8";
					case BOMB:
						return "B";
				} // end of gameStates switch
				break; // end of case VISIBLE
		} // end of showStates switch
		return "";
	}


	public Game(int cols, int rows, int numBombs)
	{		
		setup(cols, rows, numBombs);
	}

	public GameState getGameState(int x, int y)
	{
		return gameState[x][y];
	}
	public ShowState getShowState(int x, int y)
	{
		return showState[x][y];
	}

	public void setup(int cols, int rows, int numBombs)
	{
		this.cols = cols;
		this.rows = rows;
		this.numBombs = numBombs;
		gameState = new GameState[cols][rows];
		showState = new ShowState[cols][rows];
		numVisible = 0;
		numFlagged = 0;
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
			{
				gameState[i][j] = ZERO;
				showState[i][j] = HIDDEN;
			}
		Random rand = new Random();
		for (int i = 0; i < numBombs; i++)
		{
			int bx = rand.nextInt(cols);
			int by = rand.nextInt(rows);
			if (gameState[bx][by] == ZERO) gameState[bx][by] = GameState.BOMB;
			else i--;
		}
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
			{
				if (gameState[i][j] == GameState.BOMB)
				{
					if(i > 0 && j > 0 && gameState[i-1][j-1] != GameState.BOMB) gameState[i-1][j-1] = gameState[i-1][j-1].inc();
					if(j > 0 && gameState[i][j-1] != GameState.BOMB) gameState[i][j-1] = gameState[i][j-1].inc();
					if(i < cols-1 && j > 0 && gameState[i+1][j-1] != GameState.BOMB) gameState[i+1][j-1] = gameState[i+1][j-1].inc();
					if(i > 0 && gameState[i-1][j] != GameState.BOMB) gameState[i-1][j] = gameState[i-1][j].inc();
					if(i < cols-1 && gameState[i+1][j] != GameState.BOMB) gameState[i+1][j] = gameState[i+1][j].inc();
					if(i > 0 && j < rows-1 && gameState[i-1][j+1] != GameState.BOMB) gameState[i-1][j+1] = gameState[i-1][j+1].inc();
					if(j < rows-1 && gameState[i][j+1] != GameState.BOMB) gameState[i][j+1] = gameState[i][j+1].inc();
					if(i < cols-1 && j < rows-1 && gameState[i+1][j+1] != GameState.BOMB) gameState[i+1][j+1] = gameState[i+1][j+1].inc();
				}
			}
	}

	public Result uncover (int absolute) {
		return uncover(absolute % rows, absolute / rows);
	}

	public Result flag(int absolute) {
		return flag(absolute % rows, absolute / rows);
	}

	private Result uncover(int x, int y)
	{
		if (cols*rows - numVisible - numFlagged == 0)
			return Result.INVALID;		
		if (showState[x][y] == HIDDEN)
		{
			showState[x][y] = VISIBLE;
			numVisible++;
			if(gameState[x][y] == GameState.BOMB)
			{
				uncoverAll();
				return Result.HIT;
			}
			if (gameState[x][y] == ZERO)
				recursiveUncover(x,y);
			if (cols*rows - numVisible - numBombs == 0)
				return Result.WIN;
			return Result.VALID;
		}
		return Result.INVALID;
	}

	public void uncoverAll()
	{
		for (int i = 0; i < cols; i++)
			for(int j = 0; j < rows; j++)
				showState[i][j] = VISIBLE;
	}

	private void recursiveUncover(int x, int y)
	{
		if(x > 0 && y > 0) recUncover(x-1,y-1);
		if(y > 0) recUncover(x,y-1);
		if(x < cols-1 && y > 0) recUncover(x+1,y-1);
		if(x > 0) recUncover(x-1,y);
		if(x < cols-1) recUncover(x+1,y);
		if(x > 0 && y < rows-1) recUncover(x-1,y+1);
		if(y < rows-1) recUncover(x,y+1);
		if(x < cols-1 && y < rows-1) recUncover(x+1,y+1);
		
	}	

	private void recUncover(int x, int y)
	{
		if (showState[x][y] == HIDDEN)
		{
			numVisible++;
			showState[x][y] = VISIBLE;
			if (gameState[x][y] == ZERO) recursiveUncover(x,y);
		}
	}

	private Result flag(int x, int y)
	{
		if (cols*rows - numVisible - numFlagged == 0)
			return Result.INVALID;
		if (showState[x][y] == HIDDEN)
		{
			numFlagged++;
			showState[x][y] = ShowState.FLAGGED;
			if (cols*rows - numVisible - numBombs == 0)
				return Result.WIN;
			return Result.VALID;
		}
		else if (showState[x][y] == ShowState.FLAGGED)
		{
			numFlagged--;
			showState[x][y] = HIDDEN;
			return Result.VALID;
		}
		return Result.INVALID;
	}
}
