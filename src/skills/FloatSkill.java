package skills;

import game.GameManager;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class FloatSkill implements Skill {
//-----------------------------------------------------------------------------
	private static final float FLOAT_SPEED = 0.03f;
	private static final int WAIT_INTERVAL = 1200;
	private static final String NAME = "floater";
	private long timer = 0;
	private static AnimatedGif[][] anims;
	private boolean startPhase = true;
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public FloatSkill(Lemming lemming) {
		this.lemming = lemming;
		anims = new AnimatedGif[2][2];
		loadImages();
		lemming.setObjectBounds( new ObjectBounds(null, null, null, 
				new Rectangle(4, 19, 8, 6)) );
		init(lemming);
	}
//-----------------------------------------------------------------------------
	private void init(Lemming l){
		l.setVelocityX(0);
		l.setVelocityY(FLOAT_SPEED);
		l.setAnimation(anims[0][l.getDirection()]);
		l.setOnGround(false);
		l.setGetsGravity(false);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		if(startPhase){
			timer += elapsedTime;
			if(timer>=WAIT_INTERVAL){
				startPhase = false;
				lemming.setAnimation(anims[1][lemming.getDirection()]);
			}
		} else {
			done = lemming.isOnGround();
			if(done){ lemming.setGetsGravity(true); }
		}
		
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		String[] biDirLemImgNames = { "floatstart", "float" };
		
		//Add all the bidirectional lemmings
		int i = 0;
		for(String bi : biDirLemImgNames){
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
