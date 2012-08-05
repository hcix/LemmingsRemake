package game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import objects.Trap;
import skills.Skill;
import utilities.AnimatedGif;

public class GameManager{
//-----------------------------------------------------------------------------
	/** The four possible game play categories */
	public static final String FUN = "fun";
	public static final String TRICKY = "tricky";
	public static final String TAXING = "taxing";
	public static final String MAYHEM = "mayhem";
	
	/** Gravity is a constant force in lemming world */
	public static final float GRAVITY = 0.00005f;
	
	private static final int RELEASE_CONSTANT = 35;//30;
	private GameModel model;
	private CollisionDetector collisionDetector;
	private Level level;
	private Lemming targetedLemming;
	
	private ArrayList<GameObject> objectList;
	private ArrayList<Trap> trapList;
	private ArrayList<Lemming> lemmingList;
	private ArrayList<GameObject> newlyCreated;
	
	private int selectedSkill;
	private int lemmingsOut;
	private int lemmingsRescued;
	/** The number of milliseconds to wait between releasing lemmings */
	private long releaseRate;
	/** The number of milliseconds that have passed since the last lemming
 	was released */
	private long time;
	/** Whether or not the GameManager is in active mode */
	private boolean active;
	/** The nukeAll flag, set to true if all lemmings should be nuked */
	private boolean nukeAll;
	
	//private boolean fastForwardMode = false;
//-----------------------------------------------------------------------------
	public GameManager(GameModel model){
		this.model = model;
		
		//Initialize the lists
		objectList = new ArrayList<GameObject>();
		lemmingList = new ArrayList<Lemming>();
		newlyCreated = new ArrayList<GameObject>();
		trapList = new ArrayList<Trap>();
	}
//-----------------------------------------------------------------------------
	public void update(long elapsedTime){
		if(!active){ return; }//do not update if not in active state
		
		if(level.update(elapsedTime)){ 
			gameOver();
		}
		
		boolean wakeALemming = false;
		
		//Release lemmings periodically according to the release rate
		if( (level!=null)&&(lemmingsOut<level.getLemmings()) && !nukeAll){
			time += elapsedTime;
			if(time>=releaseRate){
				time = time % releaseRate;
				lemmingsOut++;
				wakeALemming = true;
			}
		}
		
		//Update the traps list
		for(Trap trap : trapList){
			trap.update(elapsedTime);
		}
		
		//Run thru & update the list of lemmings
		if( (level!=null) && (level.doneLoadingGrid) ){
			if(lemmingList.size()==0){ gameOver(); }
			else{
				Iterator<Lemming> ll = lemmingList.iterator();
				while(ll.hasNext()){
					Lemming lemming = ll.next();
					int state = lemming.getState();
					//If the lemming's alive, update him
					if(state>=0){
						updateLemming(lemming, elapsedTime);
					} else if(state==Lemming.STATE_UNBORN){
						if(wakeALemming){//we're supposed to wake an unborn lemming 
						//Lookie here, an unborn lemming, let's go ahead and wake him
							if(lemming.getState()==Lemming.STATE_UNBORN){
								int x = level.getStart().x;
								int y = level.getStart().y;
								lemming.setX(x);
								lemming.setY(y);
								lemming.setState(Lemming.STATE_FALLING);
								addNewlyCreated(lemming);
								wakeALemming = false;//we only need to find and wake one
							}
						}
					}
				}
			}
		}				

	}
//-----------------------------------------------------------------------------
	private void updateLemming(Lemming l, long elapsedTime){		
		//Apply gravity 
		if( (l.getState()>=0) && l.shouldApplyGravity() ){
			l.setVelocityY(l.getVelocityY() + (GRAVITY * elapsedTime));
		}		
			
		//Get the bounding boxes for this lemming
		ObjectBounds bounds = l.getObjectBounds(); 
			
		//Change x
		float oldLemX = l.getXVal();
		float newLemX = oldLemX + l.getVelocityX() * elapsedTime;
		float dx = l.getVelocityX() * elapsedTime;
		if(dx > 0){//moving right
			//Check right side for collision
			Rectangle right = bounds.getRight(l.getXVal(), l.getYVal());
			float newX = right.x + dx;
			Tile tile = collisionDetector.getTileCollision(right, newX, right.y);
			if(tile==null){
				l.setX(newLemX);
			} else {
				if(tile.isExit()){
					//DEBUG System.out.printf("EXIT TILE  \n");
					l.setX(newLemX);
					l.sendHome();
				} else if(tile.killsLemming()){
					if(tile.isWater()){
						//DEBUG System.out.printf("drown lemming drown!  \n");
						l.drown();
					} else if(tile.isTrap()){
						Trap trap = getTrap(tile.getColumn(), tile.getRow());
						trap.trigger();
						l.kill();
					}
				} else{
					//DEBUG System.out.printf("tile.c = %c\n", tile.getChar());
					l.setX(tile.getX() - l.getWidth());
					l.collideHorizontal();
				}
			}
		} else if(dx < 0){//moving left
			//Check left side for collision
			Rectangle left = bounds.getLeft(l.getXVal(), l.getYVal());
			float newX = left.x + dx;
			
			Tile tile = collisionDetector.getTileCollision(left, newX, left.y);
			if(tile==null){
				l.setX(newLemX);
			} else {
				if(tile.isExit()){
					//DEBUG System.out.printf("home \n");
					l.setX(newLemX);
					l.sendHome();
				} else if(tile.killsLemming()){
					if(tile.isWater()){
						//DEBUG System.out.printf("drown lemming drown!  \n");
						l.drown();
					} else if(tile.isTrap()){
						Trap trap = getTrap(tile.getColumn(), tile.getRow());
						//Trap only kills one lemming at a time
						if(!trap.isInAction()){
							trap.trigger();
							l.kill();
						}
					}
				} else{
					l.setX(CollisionDetector.tilesToPixels(tile.getColumn() + 1));
					l.collideHorizontal();
				}
			}
		} 		
		
		//Change y
		float oldLemY = l.getYVal();
		float newLemY = oldLemY + (l.getVelocityY() * elapsedTime);
		float dy = l.getVelocityY() * elapsedTime;
		if(l.getVelocityY() > 0){//moving down
			//Check bottom for collision 
			Rectangle bot = bounds.getBottom(l.getXVal(), l.getYVal());
			float newY = bot.y + dy;
			Tile tile = collisionDetector.getTileCollision(bot, bot.x, newY);
			if(tile==null){
				l.setY(newLemY);
				l.setOnGround(false);
			} else if(tile.killsLemming()){
				if(tile.isWater()){
					//DEBUG System.out.printf("drown lemming drown!  \n");
					l.drown();
				} else if(tile.isTrap()){
					Trap trap = getTrap(tile.getColumn(), tile.getRow());
					trap.trigger();
					l.kill();
				}
			} else{
				l.setY(tile.getY() - bounds.getHeight());
				l.collideVertical();
			}
		} else if(l.getVelocityY() < 0){//moving up
			//Check top for collision
			Rectangle top = bounds.getTop(l.getXVal(), l.getYVal());
			float newY = top.y + dy;
			
			Tile tile = collisionDetector.getTileCollision(top, top.x, newY);
			if(tile==null){
				l.setOnGround(false);
				l.setY(newLemY);
			} else if(tile.killsLemming()){
				if(tile.isWater()){
					//DEBUG System.out.printf("drown lemming drown!  \n");
					l.drown();
				} else if(tile.isTrap()){
					Trap trap = getTrap(tile.getColumn(), tile.getRow());
					trap.trigger();
					l.kill();
				}
			} else{
				//DEBUG System.out.printf("tile.c = %c\n", tile.getChar());
				l.setY(CollisionDetector.tilesToPixels(tile.getRow() + 1));
				l.collideVertical();
			}
		}
				
				
//DEBUG if(l.getState()==Lemming.STATE_FALLING){ System.out.printf("GameManager: l.getVelocityY() = %f  \n",
//		l.getVelocityY()); }
		
		l.update(this, elapsedTime);
	}
//-----------------------------------------------------------------------------
	/**
	 * Pauses all the GIF animations on the game play screen.
	 */
	public void togglePause(){
		for(Lemming l : lemmingList){
			l.togglePause();
		}
		for(GameObject obj : objectList){
			obj.togglePause();
		}
		for(Trap t : trapList){
			if(t.isInAction()){ t.togglePause(); }
		}
	}
//-----------------------------------------------------------------------------
	public void fastForward(){
		//fastForwardMode = true;
		
		for(Lemming l : lemmingList){
			l.fastForward();
		}
		for(GameObject obj : objectList){
			AnimatedGif ani = obj.getAnimIcon();
			if(ani!=null){ ani.setSpeedFactor(2); }
		}
		for(GameObject t : trapList){
			t.getAnimIcon().setSpeedFactor(2);
		}
	}
//-----------------------------------------------------------------------------
	public void restoreNormalSpeed(){
		//fastForwardMode = false;
		
		for(Lemming l : lemmingList){
			l.restoreNormalSpeed();
		}
		for(GameObject obj : objectList){
			AnimatedGif ani = obj.getAnimIcon();
			if(ani!=null){ ani.setSpeedFactor(1); }
		}
		for(GameObject t : trapList){
			t.getAnimIcon().setSpeedFactor(1);
		}
	}
//-----------------------------------------------------------------------------
	public void nukeThemAll(){
		for(Lemming lemming : lemmingList){
			int state = lemming.getState();
			//If the lemming's alive, update him
			if(state>=0){
				lemming.assignSkill(Skill.EXPLODE_SKILL);
			}
		}
		nukeAll = true;
	}
//-----------------------------------------------------------------------------
	private void gameOver(){
		level.setLemmingsRescued(lemmingsRescued);
		model.gameOver();
	}
//-----------------------------------------------------------------------------
	public void activate(Level level){
		//Save a reference to the level
		this.level = level;
		//Setup the game's initial values
		nukeAll = false;
		lemmingsOut = 0;
		lemmingsRescued = 0;
		time = 3500;
		releaseRate = 4000;
		//Create a new CollisionDetector object for this level
		collisionDetector = new CollisionDetector(level);
		active = true;
	}
//-----------------------------------------------------------------------------
	public void deactive(){
		//Reset everything
		collisionDetector = null;
		level = null;
		targetedLemming = null;
		objectList = new ArrayList<GameObject>();
		lemmingList = new ArrayList<Lemming>();
		newlyCreated = new ArrayList<GameObject>();
		active = false;
	}
//-----------------------------------------------------------------------------
	public void setReleaseRate(int rr){
		releaseRate = RELEASE_CONSTANT*(99-rr) + 100;
	}
//-----------------------------------------------------------------------------
	/** 
	 * Returns the trap located at the specified tile coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public Trap getTrap(int x, int y){
		//DEBUG System.out.printf("Find (%d, %d)  \n", x, y);
		for(Trap t : trapList){
			//DEBUG System.out.printf("trap coordinates: (%d, %d) \n", t.getTrapX(), t.getTrapY());
			if(t.getTrapX()==x){
				if(t.getTrapY()==y){ return t; }
			}
		}
		//no trap found at this location
		System.out.printf("Couldn't find trap @ (%d, %d) \n", x, y);
		return null;
	}
//-----------------------------------------------------------------------------
	public void addTrap(Trap trap){
		trapList.add(trap);
		addNewlyCreated(trap);
	}
//-----------------------------------------------------------------------------
	public void addLemming(Lemming lemming){
		lemmingList.add(lemming);
	}
//-----------------------------------------------------------------------------
	public void addObject(GameObject object){
		objectList.add(object);
		addNewlyCreated(object);
	}
//-----------------------------------------------------------------------------
	public void addNewlyCreated(GameObject object){
		newlyCreated.add(object);
	}
//-----------------------------------------------------------------------------
	public ArrayList<GameObject> getObjectList(){
		return objectList;
	}
//-----------------------------------------------------------------------------
	public ArrayList<Lemming> getLemmingList(){
		return lemmingList;
	}
//-----------------------------------------------------------------------------
	public ArrayList<GameObject> getNewlyCreated(){
		return newlyCreated;
	}
//-----------------------------------------------------------------------------
	public ArrayList<Trap> getTrapList(){
		return trapList;
	}
//-----------------------------------------------------------------------------
	public void removeLemming(Lemming lemming){
		lemmingList.remove(lemming);
	}
//-----------------------------------------------------------------------------
	public Level getLevel(){
		return level;
	}
//-----------------------------------------------------------------------------
	public boolean isActive(){
		return active;
	}
//-----------------------------------------------------------------------------
	public int getSelectedSkill() {
		return selectedSkill;
	}
//-----------------------------------------------------------------------------
	public void setSelectedSkill(int selectedSkill) {
		this.selectedSkill = selectedSkill;
	}
//-----------------------------------------------------------------------------
	public int getLemmingsRescued(){
		return lemmingsRescued;
	}
//-----------------------------------------------------------------------------
	public void upLemmingsRescuedCount(){
		lemmingsRescued++;
	}
//-----------------------------------------------------------------------------
	public Lemming getTargetedLemming() {
		return targetedLemming;
	}
//-----------------------------------------------------------------------------
	public void setTargetedLemming(Lemming targetedLemming) {
		this.targetedLemming = targetedLemming;
	}
//-----------------------------------------------------------------------------
	public int getLemmingsOut(){
		return lemmingsOut;
	}
//-----------------------------------------------------------------------------
}
