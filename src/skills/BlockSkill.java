package skills;

import game.CollisionDetector;
import game.GameManager;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class BlockSkill implements Skill{ // extends Skilled {
//-----------------------------------------------------------------------------
	private static final ObjectBounds BOUNDS = new ObjectBounds(19, 19);
	private static final String NAME = "block";
	private static AnimatedGif leftAnim;
	private static AnimatedGif rightAnim;
	private boolean startPhase;
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public BlockSkill(Lemming lemming) {
		this.lemming = lemming;
		loadImages();
		lemming.setObjectBounds(BOUNDS);
		init(lemming);
		startPhase = true;
	}
//-----------------------------------------------------------------------------
	private void init(Lemming l){
		l.setVelocityX(0);
		l.setVelocityY(0);
		if(l.getDirection()==Lemming.DIR_RIGHT){
			l.setAnimation(rightAnim);
		} else {
			l.setAnimation(leftAnim);
		}
		l.setOnGround(true);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		if(startPhase){
			Rectangle top = lemming.getObjectBounds().getTop(lemming.getXVal(), lemming.getYVal());
			int fromTileX = CollisionDetector.pixelsToTiles(top.x);
			int fromTileY = CollisionDetector.pixelsToTiles(top.y);
			int toTileX = CollisionDetector.pixelsToTiles(top.x + top.width);
			int toTileY = CollisionDetector.pixelsToTiles(top.y + top.height);
			
			for(int r=fromTileY; r<=toTileY; r++){
				for(int c=fromTileX; c<=toTileX; c++){
					if(gm.getLevel().getTileAt(r, c)!='e'){
						gm.getLevel().setTileAt(r, c, 'e');
					}
				}
			}
			startPhase = false;
		} 
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		String blockImg = "block";
		leftAnim =  ImageHelper.getLemmingAnim("lemming_"+blockImg+"_l");
		rightAnim = ImageHelper.getLemmingAnim("lemming_"+blockImg+"_r");
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
