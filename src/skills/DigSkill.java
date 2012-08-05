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

public class DigSkill implements Skill{
//-----------------------------------------------------------------------------
	private static final int WAIT_INTERVAL = 700;
	private static final String NAME = "digger";
	private static final ObjectBounds BOUNDS = new ObjectBounds(null, null, null, 
			new Rectangle(6, 15, 22, 8) );
			//new Rectangle(0, 15, 27, 6) );
	//private static AnimatedGif[][] anims;
	private static AnimatedGif leftAnim;
	private static AnimatedGif rightAnim;
	private static ImageIcon holeImg;
	private long timer = 0;
	
	private Lemming lemming;
	private boolean done;
//-----------------------------------------------------------------------------
	public DigSkill(Lemming lemming) {
		this.lemming = lemming;
		//anims = new AnimatedGif[1][2];
		loadImages();
		lemming.setObjectBounds(BOUNDS);
		init(lemming);
	}
//-----------------------------------------------------------------------------
	private void init(Lemming l){
		l.setVelocityX(0);
		//l.setAnimation(anims[0][l.getDirection()]);
		if(l.getDirection()==Lemming.DIR_LEFT){
			l.setAnimation(leftAnim);
		} else{
			l.setAnimation(rightAnim);
		}
		l.setOnGround(true);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		timer += elapsedTime;
		if(timer>=WAIT_INTERVAL){
			timer = timer % WAIT_INTERVAL;
			this.done = dig(lemming, gm);
		}
	}
//-----------------------------------------------------------------------------
	private boolean dig(Lemming l, GameManager gm){
		char[][] grid = gm.getLevel().getGrid();
		
		//Get the positions to be checked
		ObjectBounds bounds = l.getObjectBounds();
		Rectangle bottom = bounds.getBottom(l.getXVal(), l.getYVal());
		int startX = bottom.x;
		int startY = bottom.y;
		int endX = startX + bottom.width;
		int endY = startY + bottom.height;
		
		//Get the positions in terms of tiles
		int tileStartX = CollisionDetector.pixelsToTiles(startX);
		int tileStartY = CollisionDetector.pixelsToTiles(startY);
		int tileEndX = CollisionDetector.pixelsToTiles(endX);
		int tileEndY = CollisionDetector.pixelsToTiles(endY);
		
		//Create the hole object
		//ImageIcon holeImg = ImageHelper.getImage("hole_dig.png");
		GameObject hole = new GameObject(holeImg);
		hole.setName("digger_hole");
		hole.setX(startX + 1);

		try{
			//Check each tile starting from top to find first non-empty tile
			for(int r=tileStartY; r<=tileEndY; r++){
				for(int c=tileStartX; c<=tileEndX; c++){
					if(grid[r][c] != 'a' ){
						//non-empty tile found, lay this slice of the hole on this row
						int y = CollisionDetector.tilesToPixels(r);
						hole.setY(y);
						gm.addObject(hole);
						c=tileStartX;
						while(c<=tileEndX){
							gm.getLevel().setTileAt(r,  c, 'a');
							c++;
						}
						return false;
					}
				}
			}
		} catch(ArrayIndexOutOfBoundsException e){
			//Catches the exception that is thrown if lemming has dug to end
			// of map & returns true so that he stops digging
			return true;
		}
		//hole is complete
		return true;
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		String lemName = "dig";
		
		//Add all the bidirectional lemmings
		leftAnim =  ImageHelper.getLemmingAnim("lemming_"+lemName+"_l");
		rightAnim = ImageHelper.getLemmingAnim("lemming_"+lemName+"_r");
		
		//Create the hole image
		ImageIcon orig = ImageHelper.getImage("hole_dig.gif");
		holeImg = new ImageIcon( ImageHelper.getScaledImage(orig.getImage(), 
				orig.getIconWidth(), orig.getIconHeight()*2) );
//		holeImg = new ImageIcon( ImageHelper.getScaledImage(orig.getImage(), 
//				orig.getIconWidth()*2, orig.getIconHeight()*2) );
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
