package skills;

import game.CollisionDetector;
import game.GameManager;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class ClimbSkill implements Skill{
//-----------------------------------------------------------------------------
	private static final float CLIMB_SPEED = -0.01f;
	private static final int LAST_PHASE_TIME = 1600;
	private static final String NAME = "climber";
	private static AnimatedGif[][] anims;
	private boolean startPhase = true;
	private boolean lastPhase = false;
	
	private long timer = 0;
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public ClimbSkill(Lemming lemming) {
		this.lemming = lemming;
		anims = new AnimatedGif[2][2];
		loadImages();
		init(lemming);
	}
//-----------------------------------------------------------------------------
	private void init(Lemming l){
		l.setVelocityX(0);
		l.setVelocityY(CLIMB_SPEED);
		l.setAnimation(anims[0][l.getDirection()]);
		l.setOnGround(false);
		l.setGetsGravity(false);
	}
//-----------------------------------------------------------------------------
	private void init(GameManager gm){
		ObjectBounds bounds = lemming.getObjectBounds();
		Rectangle right = bounds.getRight(lemming.getXVal(), lemming.getYVal());
		float shiftedX = right.x + 10;

		Point tile = getTileCollision(gm, right, shiftedX, right.y);
		if(tile==null){
			this.done = true;
		} else {
			lemming.setX(CollisionDetector.tilesToPixels(tile.x) - lemming.getWidth());
		}
		
		Rectangle rect = new Rectangle(5, 4, 5, 5);
		lemming.setObjectBounds(new ObjectBounds(rect, null, null, null));
		
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		if(startPhase){ init(gm); startPhase = false; }
		
		if(lastPhase){
			timer += elapsedTime;
			if(timer>=LAST_PHASE_TIME){
				lemming.setGetsGravity(true);
				this.done = true;
			}
		} else {//not in the last phase
			//Get the bounding boxes for this lemming
			ObjectBounds bounds = lemming.getObjectBounds(); 
		
//			Rectangle right = bounds.getRight(lemming.getXVal(), lemming.getYVal());
//			float shiftedX = right.x + 10;
			Rectangle right = bounds.getTop(lemming.getXVal(), lemming.getYVal());
			float shiftedX = right.x + 10;
			
			Point tile = getTileCollision(gm, right, shiftedX, right.y);
			if(tile==null){
				//System.out.printf("DONE\n");
				lemming.setAnimation(anims[1][lemming.getDirection()]);
				lemming.setX(lemming.getXVal() + 5);
				lastPhase = true;
				//this.done = true;
			}
		}

	}
//-----------------------------------------------------------------------------
	public Point getTileCollision(GameManager gm, Rectangle rect, float newX, float newY){
		char[][] grid = gm.getLevel().getGrid();
		
		//Get the pixel locations
	    float fromX = Math.min(rect.x, newX);
	    float fromY = Math.min(rect.y, newY);
	    float toX = Math.max(rect.x, newX);
	    float toY = Math.max(rect.y, newY);
	
	    //Get the tile locations
	    int fromTileX = CollisionDetector.pixelsToTiles(fromX);
	    int fromTileY = CollisionDetector.pixelsToTiles(fromY);
	    int toTileX = CollisionDetector.pixelsToTiles(toX + rect.width);
	    int toTileY = CollisionDetector.pixelsToTiles(toY + rect.height);
	    
	    //Check each tile for a collision
	    for(int c=fromTileX; c<=toTileX; c++){
	    	for(int r=fromTileY; r<=toTileY; r++){
	            if(c < 0 || c >= gm.getLevel().getMapWidth() || grid[r][c]!='a'){
	            	//collision found, return the tile
	            	return (new Point(c, r));
	            }
	        }
	    }
	    //No collision found
	    return null;
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		String[] biDirLemNames = { "climb",  "climbtop" };
		
		//Add all the bidirectional lemmings
		int i = 0;
		for(String bi : biDirLemNames){
			anims[i][0] =  ImageHelper.getLemmingAnim("lemming_"+bi+"_l");
			anims[i][1] = ImageHelper.getLemmingAnim("lemming_"+bi+"_r");
			i++;
		}

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
