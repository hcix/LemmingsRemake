package game;

import gui.Controller;
import gui.GameOverScreen;
import gui.GameScreen;
import gui.LevelsMenu;
import gui.MainMenu;

import java.awt.CardLayout;

import javax.swing.JPanel;
/**
 * Facilitates communication between the classes that control the 
 * game and the classes that render it.
 * 
 */
public class ScreenManager {
	public static final String MAIN_MENU = "MAIN_MENU";
	public static final String GAME_SCREEN = "GAME_SCREEN";
	public static final String LEVELS_SCREEN = "LEVELS_SCREEN";
	public static final String GAME_OVER_SCREEN = "GAME_OVER_SCREEN";
	
	public static final int MAIN_MENU_ID = 0;
	public static final int GAME_SCREEN_ID = 1;
	public static final int LEVELS_SCREEN_ID = 2;
	public static final int GAME_OVER_SCREEN_ID = 3;
	
	private GameModel model;
	private MainMenu mainMenu;
	private LevelsMenu levelsMenu;
	private GameScreen gs;
	private GameOverScreen gameOver;
	private GameManager gm;
	//private Controller controller;
	private JPanel screen;
	private int currentScreen;
//-----------------------------------------------------------------------------
	public ScreenManager(GameManager gm, Controller controller, JPanel screen, GameModel model){
		this.screen = screen;
		this.gm = gm;
		this.model = model;
		//this.controller = controller;
		screen.setLayout(new CardLayout());
		
		//Create and add the screen views
		mainMenu = new MainMenu(this);
		levelsMenu = new LevelsMenu(this);
		gs = new GameScreen(this, gm, controller);
		gs.addMouseListener(controller);
		gameOver = new GameOverScreen(this);
		
		screen.add(mainMenu, MAIN_MENU);
		screen.add(levelsMenu, LEVELS_SCREEN);
		screen.add(gs, GAME_SCREEN);
		screen.add(gameOver, GAME_OVER_SCREEN);
		
		currentScreen = MAIN_MENU_ID;
	}
//-----------------------------------------------------------------------------
	public void update(long elapsedTime){
		if(currentScreen==GAME_SCREEN_ID){
			gs.render();
		}
	}
//-----------------------------------------------------------------------------
	public void showGameplayScreen(String category, int level){
		Level newLevel = new Level(category, level, gm);
		if(gameOver.isActive()){ gameOver.deactivate(); }
		gm.activate(newLevel);
		gs.activate(newLevel.getStartX());
		
		model.setGameState(GameModel.GAMESTATE_PLAYING);
		currentScreen = GAME_SCREEN_ID;
		setScreen(GAME_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void showMainMenu(){
		//Deactivate any active game components
		if(gm.isActive()){ gm.deactive(); }
		if(gs.isActive()){ gs.deactivate(); }
		if(gameOver.isActive()){ gameOver.deactivate(); }
		
		currentScreen = MAIN_MENU_ID;
		setScreen(MAIN_MENU);
	}
//-----------------------------------------------------------------------------
	public void showGameOverScreen(){
		//Activate the game over screen
		gameOver.activate(gm.getLevel());
		
		//Deactivate non-needed game components
		gs.deactivate();
		gm.deactive();
		
		currentScreen = GAME_OVER_SCREEN_ID;
		setScreen(GAME_OVER_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void showLevelsMenu(String category){
		levelsMenu.showLevelDirectory(category);
		currentScreen = LEVELS_SCREEN_ID;
		setScreen(LEVELS_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void displayFunLevels(){
		levelsMenu.showLevelDirectory(GameManager.FUN);
		currentScreen = LEVELS_SCREEN_ID;
		setScreen(LEVELS_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void displayTrickyLevels(){
		levelsMenu.showLevelDirectory(GameManager.TRICKY);
		currentScreen = LEVELS_SCREEN_ID;
		setScreen(LEVELS_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void displayMayhemLevels(){
		levelsMenu.showLevelDirectory(GameManager.MAYHEM);
		currentScreen = LEVELS_SCREEN_ID;
		setScreen(LEVELS_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void displayTaxingLevels(){
		levelsMenu.showLevelDirectory(GameManager.TAXING);
		currentScreen = LEVELS_SCREEN_ID;
		setScreen(LEVELS_SCREEN);
	}
//-----------------------------------------------------------------------------
	public void setScreen(String screenToShow){
		CardLayout cl = (CardLayout)(screen.getLayout());
    	cl.show(screen, screenToShow);
	}
//-----------------------------------------------------------------------------
}
