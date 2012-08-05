package game;


import gui.Controller;

import javax.swing.JPanel;

public class GameModel {
//-----------------------------------------------------------------------------
	public static final int GAMESTATE_MENU = 0;
	public static final int GAMESTATE_PLAYING = 1;
	public static final int GAMESTATE_PAUSED = 2;
	public static final int GAMESTATE_OVER = 3;
	
	private ScreenManager screenManager;
	private GameManager gameManager;
	private Controller controller;
	
	private int gameState;
	private boolean ffMode = false;
//-----------------------------------------------------------------------------
	public GameModel(JPanel screen){
		//Create the resources to run and manage the game
		gameManager = new GameManager(this);
		controller = new Controller(gameManager, this);
		screenManager = new ScreenManager(gameManager, controller, screen, this);
		
		this.setGameState(GAMESTATE_MENU);
	}
//-----------------------------------------------------------------------------
	public void update(long elapsedTime){
		if(ffMode){ elapsedTime = elapsedTime * 2; }
		//If there's a game in progress, update the game manager & render the screen
		if(gameState==GAMESTATE_PLAYING){
			gameManager.update(elapsedTime);
			screenManager.update(elapsedTime);
		}
	}
//-----------------------------------------------------------------------------
	public void gameOver(){
		this.setGameState(GAMESTATE_OVER);
		screenManager.showGameOverScreen();
	}
//-----------------------------------------------------------------------------
	public int getGameState(){
		return gameState;
	}
//-----------------------------------------------------------------------------
	public void setGameState(int gameState){
		this.gameState = gameState;
	}
//-----------------------------------------------------------------------------
	public void pauseGame(){
		gameManager.togglePause();
		this.setGameState(GAMESTATE_PAUSED);
	}
//-----------------------------------------------------------------------------
	public void resumeGame(){
		gameManager.togglePause();
		this.setGameState(GAMESTATE_PLAYING);
	}
//-----------------------------------------------------------------------------
	public void fastForwardGameplay(){
		ffMode = true;
		//DEBUG System.out.printf("ffMode = true;  \n");
	}
//-----------------------------------------------------------------------------
	public void resumeNormalSpeed(){
		ffMode = false;
		//DEBUG System.out.printf("ffMode = false;  \n");
	}
//-----------------------------------------------------------------------------
}
