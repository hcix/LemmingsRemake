package core;

import javax.swing.JFrame;

public abstract class GameCore extends JFrame {
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	private static final int DESIRED_FPS = 60;
//=============================================================================
	/** Keeps the game running */
	private class GameLoop extends Thread {
		private final int sleepTime;
		private long      lastTimeMS;
		private long      elapsedTimeMS;
		
		public GameLoop(double desiredFPS) {
			sleepTime = (int) (1000 / desiredFPS);
		}
		
		private void checkElapsedTime() {
			long curTime = System.currentTimeMillis();
			elapsedTimeMS = 0;
			if(lastTimeMS > 0){
				elapsedTimeMS = curTime - lastTimeMS;
			}
			lastTimeMS = curTime;
		}
		        
		public void run() {
			while (true) {
				checkElapsedTime();
				update(elapsedTimeMS);
				render();
				try {
					Thread.sleep(sleepTime);
				} catch(InterruptedException e){
					e.printStackTrace();
				}	    
			}
		}
	}
//=============================================================================
	protected GameLoop gameLoop;
	    
	/** Loads images and creates game components */
	public abstract void initialize();
	
	/** Updates the game's data */
	public abstract void update(long elapsedMS);
	
	/** Drawings the game screen */
	public abstract void render();
//-----------------------------------------------------------------------------
	/**
	 * Initializes and starts the thread that keeps the game running.
	 */
	public void init() {
	    gameLoop = new GameLoop(DESIRED_FPS);
	    initialize();
	    gameLoop.start();
	}   
 //-----------------------------------------------------------------------------
}
