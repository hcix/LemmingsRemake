package skills;

import game.CollisionDetector;
import game.GameManager;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class BashSkill implements Skill{
//-----------------------------------------------------------------------------
	private static final int BASHER_WAIT_INTERVAL = 1600; //3200;
	private static final String NAME = "basher";
	private static final int[][] BASHER_HOLE_LEFT = {
		{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	};
	private static final int[][] BASHER_HOLE_RIGHT = {
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
	};
	private static final ObjectBounds BASHER_BOUNDS = 
			new ObjectBounds( null, new Rectangle(0, 0, 4, 14), 
			new Rectangle(8, 0, 4, 14), new Rectangle(4, 8, 4, 8) );
	//private static final int dx = 5;
	
	private long timer = 0;
//	private ImageIcon leftAnim;
//	private ImageIcon rightAnim;
	private AnimatedGif leftAnim;
	private AnimatedGif rightAnim;
	private ImageIcon leftHole;
	private ImageIcon rightHole;
	
	//private int phase;
	private Lemming lemming;
	private boolean done;
//-----------------------------------------------------------------------------
	public BashSkill(Lemming lemming) {
		this.lemming = lemming;
		loadImages();
		init();
		lemming.setObjectBounds(BASHER_BOUNDS);
		//phase = 0;
	}
//-----------------------------------------------------------------------------
	private void init(){
		lemming.setVelocityX(0);
		lemming.setVelocityY(0);
		if(lemming.getDirection()==Lemming.DIR_RIGHT){
			lemming.setAnimation(rightAnim);
		} else {
			lemming.setAnimation(leftAnim);
		}
		//Gravity does weird things to the basher's forward path
		lemming.setGetsGravity(false);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		timer += elapsedTime;
		if(timer>=BASHER_WAIT_INTERVAL){
			timer = timer % BASHER_WAIT_INTERVAL;
			this.done = bash(lemming, gm);
			//Return gravity to normal when done bashing
			if(done){ lemming.setGetsGravity(true); }
		}
	}
//-----------------------------------------------------------------------------
	private boolean bash(Lemming l, GameManager gm){
		char[][] grid = gm.getLevel().getGrid();
		
		//Get the positions to be checked
		ObjectBounds bounds = l.getObjectBounds();
		Rectangle rect;
		if(lemming.getDirection()==Lemming.DIR_RIGHT){
			rect = bounds.getRight(l.getXVal(), l.getYVal());
		} else {
			rect = bounds.getLeft(l.getXVal(), l.getYVal());
		}

		int startX = rect.x; 
		int startY = rect.y; 
		int endX = startX + 15; 
		int endY = startY + bounds.getHeight();
		
		
		//Get the positions in terms of tiles
		int tileStartX = CollisionDetector.pixelsToTiles(startX);
		int tileStartY = CollisionDetector.pixelsToTiles(startY);// - 1;
		int tileEndX = CollisionDetector.pixelsToTiles(endX);
		int tileEndY = CollisionDetector.pixelsToTiles(endY);
		
//		System.out.printf("tileStartX = %d, tileStartY = %d ; tileEndX = %d, tileEndY = %d \n",
//				tileStartX, tileStartY, tileEndX, tileEndY);
	
		//Create the hole object
		GameObject hole;
		if(l.getDirection()==Lemming.DIR_RIGHT){
			hole = new GameObject(rightHole);
		} else {
			hole = new GameObject(leftHole);
		}
		hole.setName("hole");
		hole.setY(lemming.getYVal() - 1);

		//Check each tile starting from top to find first non-empty tile
		for(int c=tileStartX; c<=tileEndX; c++){
			for(int r=tileStartY; r<=tileEndY; r++){
				if(grid[r][c] != 'a' ){
					//non-empty tile found
					int newX = CollisionDetector.tilesToPixels(c) - bounds.getWidth();//lemming.getWidth();
					//System.out.printf("lemming.getXVal() = %f; newX = %d\n", lemming.getXVal(), newX);
					int stop = c + 5;
					for(int x=c; x<=stop; x++){
						for(int y=tileStartY; y<=tileEndY; y++){
							gm.getLevel().setTileAt(y,  x, 'a');
						}
					}
					hole.setX(newX);
					gm.addObject(hole);
					lemming.setX(newX);
					return false;
				}
			}
		}
		
		//No non-empty tiles found
		return true;
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		leftAnim =  ImageHelper.getLemmingAnim("lemming_"+NAME+"_l");
		rightAnim = ImageHelper.getLemmingAnim("lemming_"+NAME+"_r");
		
		//Create the hole image
		leftHole = ImageHelper.getImage("hole_"+NAME+"_l.gif");
		rightHole = ImageHelper.getImage("hole_"+NAME+"_r.gif");
	}
//-----------------------------------------------------------------------------
	public String getName() {
		return NAME;
	}
//-----------------------------------------------------------------------------
	public boolean isDone() {
		return done;
	}
//-----------------------------------------------------------------------------
}
