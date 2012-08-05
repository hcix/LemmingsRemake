package skills;

import game.CollisionDetector;
import game.GameManager;
import gui.GameScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class ExplodeSkill implements Skill {
//-----------------------------------------------------------------------------
	private static final int ONE_SEC = 1000;//1 second = 1000 ms
	private static final int EXPLODE_TIME = 3200;
	private static final int SHORTEN_TIME = 1200;
	private static final String NAME = "exploder";
	private ObjectBounds EXPLODE_BOUNDS = new ObjectBounds( null, null, null,
			new Rectangle(3, 8, 4, 6) );
	private AnimatedGif fireworkAnim;
	private AnimatedGif explodeAnim;
	private ImageIcon holeImage;
	private GameObject countdown;
	private int countdownSecs;
	private long timer = 0;
	private int phase;
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public ExplodeSkill(Lemming lemming) {
		this.lemming = lemming;
		loadImages();
		phase = 0;
	}
//-----------------------------------------------------------------------------
	private void init(GameManager gm){
		countdownSecs = 5;

		countdown = new GameObject();
		//make sure the count down appears on the same layer as its lemming
		countdown.setLayer(GameScreen.LEMMINGS_LAYER);
		countdown.setForeground(Color.white);
		countdown.setText(countdownSecs+"");
		countdown.setName("countdown");
		countdown.setObjectBounds(new ObjectBounds(10, 10));
		countdown.setSize(new Dimension(10, 10));
		countdown.setX(lemming.getXVal());
		countdown.setY(lemming.getYVal() - 12);
		gm.addObject(countdown);
		phase++;
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		switch(phase){
		case 0:
			init(gm);
			break;
		case 1:
			//Make sure the count down is still floating above its lemming
			countdown.setX(lemming.getXVal());
			countdown.setY(lemming.getYVal() - 12);
			//If 1 sec has passed, update the count down
			timer += elapsedTime;
			if(timer>=ONE_SEC){
				timer = timer % ONE_SEC;
				updateCountdown();
			}
			break;
		case 2:
			timer += elapsedTime;
			//Half way thru adjust the bounds bc the animation bends down
			if(timer>=SHORTEN_TIME){
				lemming.setObjectBounds(EXPLODE_BOUNDS);
				//timer = timer % SHORTEN_TIME;
				phase++;
			}
			break;
		case 3:
			timer += elapsedTime;
			if(timer>=EXPLODE_TIME){
				phase++;
			}
			break;
		case 4:
			explode(gm);
			//lemming.setState(Lemming.STATE_DYING);
			lemming.kill();
			break;
		default: break;
		}
		
		
	}
//-----------------------------------------------------------------------------
	private void updateCountdown(){
		//if(--countdownSecs>=0){
		if(--countdownSecs>0){
			countdown.setText(countdownSecs+"");
		} 
		//if(countdownSecs<0){
		else if(countdownSecs==0){
			countdown.setText("");
			countdown.removeAll();
			lemming.setVelocityX(0);
			lemming.setVelocityY(0);
			timer = 0;
			phase++;
			lemming.setAnimation(explodeAnim);
		}
	}
//-----------------------------------------------------------------------------
	private void explode(GameManager gm){
		char[][] grid = gm.getLevel().getGrid();

		ObjectBounds bounds = lemming.getObjectBounds();
		Rectangle bottom = bounds.getBottom(lemming.getXVal(), lemming.getYVal());
		
		int startX = lemming.getX() - 11;
		if(startX<0){ startX = 0; }
		int endX = startX + 32;
		if(endX>=(gm.getLevel().getMapWidth()*2)){ endX = gm.getLevel().getMapWidth() - 1; }
		int startY = lemming.getY() - 7;//24; //bottom.y;
		int endY = startY + 44;//startY + 15;
		if(endY>=(gm.getLevel().getMapHeight()*2)){ endY = gm.getLevel().getMapHeight() - 1; }
		
		int tileStartX = CollisionDetector.pixelsToTiles(startX);
		int tileEndX = CollisionDetector.pixelsToTiles(endX);
		int tileStartY = CollisionDetector.pixelsToTiles(startY);
		int tileEndY = CollisionDetector.pixelsToTiles(endY);
		
//		System.out.printf("map  \n");
//		System.out.printf("tileStartX = %d, tileStartY = %d ; tileEndX = %d, tileEndY = %d  \n", 
//				tileStartX, tileStartY, tileEndX, tileEndY);
		
		//Create the hole object
		GameObject hole = new GameObject(holeImage);
		hole.setX(startX);
		hole.setY(startY);
		hole.setName("exploder_hole");
		
		ObjectBounds holeBounds = new ObjectBounds(holeImage.getIconWidth(), holeImage.getIconHeight());
		hole.setObjectBounds(holeBounds);
		
		for(int r=tileStartY; r<=tileEndY; r++){
			for(int c=tileStartX; c<=tileEndX; c++){
				if(grid[r][c] != 'a' ){
					gm.getLevel().setTileAt(r, c, 'a');
				}
			}
		}
		
		gm.addObject(hole);
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		//fireworkAnim = 
		explodeAnim = ImageHelper.getLemmingAnim("lemming_"+NAME+"_s");
		holeImage = ImageHelper.getImage("hole_"+NAME+".gif");
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

