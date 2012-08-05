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

public class MineSkill implements Skill{
//-----------------------------------------------------------------------------
	private static final int INITIAL_DELAY = 1600;
	private static final int WAIT_INTERVAL = 4800;//1600;
	private static final String NAME = "miner";
	private static final ObjectBounds BOUNDS = new ObjectBounds(null, null, null,
			new Rectangle(0, 15, 10, 10) );
	private boolean startPhase = true;
	private long timer = 0;
	private static final int dx = 10;
	private static final int dy = 10;
	private static AnimatedGif[] anims;
	private static ImageIcon[] holeImg;
	private static final int[][] RIGHT_HOLE = {
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 }, 
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
		{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
	};
	private static final int[][] LEFT_HOLE = {
		{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, 
		{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		{ 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
		{ 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
	};
	
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public MineSkill(Lemming lemming){
		this.lemming = lemming;
		anims = new AnimatedGif[2];
		holeImg = new ImageIcon[2];
		loadImages();
		lemming.setObjectBounds(BOUNDS);
		init(lemming);
	}
//-----------------------------------------------------------------------------
	private void init(Lemming lemming){
		lemming.setVelocityX(0);
		lemming.setVelocityY(0);
		lemming.setAnimation(anims[lemming.getDirection()]);
		lemming.setOnGround(true);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) { 
		if(!lemming.isOnGround()){ this.done = false; return; }
		
		timer += elapsedTime;
		if(startPhase){
			if(timer>=INITIAL_DELAY){
				timer = timer % INITIAL_DELAY;
				mine(lemming, gm);
				startPhase = false;
			}
		} else {
			if(timer>=WAIT_INTERVAL){
				timer = timer % WAIT_INTERVAL;
				this.done = mine(lemming, gm);
			}
		}
	}
//-----------------------------------------------------------------------------
	private boolean mine(Lemming lemming, GameManager gm){
		char[][] grid = gm.getLevel().getGrid();
		boolean doneWithHole = true;
		
		//Create the hole object
		float newX = lemming.getXVal() + dx;
		float newY = lemming.getYVal() + dy;
		ImageIcon holeImage = holeImg[lemming.getDirection()];
		GameObject hole = new GameObject(holeImage);
		hole.setX(lemming.getXVal());
		hole.setY(newY);
		ObjectBounds holeBounds = new ObjectBounds(holeImage.getIconWidth(), holeImage.getIconHeight());
		hole.setObjectBounds(holeBounds);
		
		hole.setName("miner_hole");
		
		
		//Get the positions to be checked
		Rectangle holeRect = holeBounds.getTop(hole.getXVal(), hole.getYVal());
		int startX = holeRect.x;
		int startY = holeRect.y;
		
		//Get the positions in terms of tiles
		int tileStartX = CollisionDetector.pixelsToTiles(startX);
		int tileStartY = CollisionDetector.pixelsToTiles(startY);
		
		//int tileEndY = tileStartY + RIGHT_HOLE.length;
		//int tileEndX = tileStartX + RIGHT_HOLE[0].length;
		

		for(int r=0; r<RIGHT_HOLE.length; r++){
			for(int c=0; c<RIGHT_HOLE[0].length; c++){
				if(RIGHT_HOLE[r][c]==1){
					int gridR = tileStartY + r;
					int gridC = tileStartX + c;
					if(grid[gridR][gridC]!='a'){ doneWithHole = false; }
					gm.getLevel().setTileAt(gridR, gridC, 'a');
				}
			}
		}
			
		if(!doneWithHole){ 
			gm.addObject(hole);
			lemming.setX(newX-4);
			//lemming.setY(newY);
		}
		if(!lemming.isOnGround()){ 
			System.out.printf("\t*!!not on ground!  \n");
			doneWithHole = true;
		}

		return doneWithHole;
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		//Load the lemming images
		anims[0] =  ImageHelper.getLemmingAnim("lemming_"+NAME+"_l");
		anims[1] = ImageHelper.getLemmingAnim("lemming_"+NAME+"_r");

		//Load the hole images
		holeImg[0] = ImageHelper.getImage("hole_"+NAME+"_l.gif");
		holeImg[1] = ImageHelper.getImage("hole_"+NAME+"_r.gif");
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
