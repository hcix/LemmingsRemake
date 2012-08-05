package skills;

import game.GameManager;

import java.awt.Rectangle;

import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class HomeBehavior implements Behavior{
//-----------------------------------------------------------------------------
	private static final String NAME = "home";
	private static final int TIME = 1600;
	private static final ObjectBounds BOUNDS = new ObjectBounds( null, null, null,
			new Rectangle(3, 10, 2, 6) );
	private static AnimatedGif anim;
	private long timer = 0;
	private boolean done = false;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public HomeBehavior(Lemming lemming){
		this.lemming = lemming;
		loadImages();
		init();
	}
//-----------------------------------------------------------------------------
	private void init(){
		lemming.setOnGround(true);
		lemming.setVelocityX(0);
		lemming.setVelocityY(0);
		lemming.setAnimation(anim);
		lemming.setObjectBounds(BOUNDS);
		//lemming.setState(Lemming.STATE_SKILLED);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		timer+=elapsedTime;
		if(timer>=TIME){
			lemming.setState(Lemming.STATE_HOME);
			//Remove the animation
			lemming.removeAll();
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
	private void loadImages(){
		anim = ImageHelper.getLemmingAnim("lemming_"+NAME+"_s");
		anim.setLoops(1);
	}
//-----------------------------------------------------------------------------
}
