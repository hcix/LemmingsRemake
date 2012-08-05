package skills;

import game.GameManager;
import objects.Lemming;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class FallBehavior implements Behavior {
//-----------------------------------------------------------------------------
	private static final String NAME = "fall";
	AnimatedGif[] anims;//ImageIcon[] anims;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public FallBehavior(Lemming lemming){
		this.lemming = lemming;
		loadImages();
	}
//-----------------------------------------------------------------------------
	private void init(){
		lemming.setVelocityX(0);
		lemming.setAnimation(anims[lemming.getDirection()]);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime) {
		
	}
//-----------------------------------------------------------------------------
	public String getName() {
		return NAME;
	}
//-----------------------------------------------------------------------------
	public boolean isDone() {
		return false;
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		anims[0] = ImageHelper.getLemmingAnim("lemming_"+NAME+"_l");
		anims[1] = ImageHelper.getLemmingAnim("lemming_"+NAME+"_r");
	}
//-----------------------------------------------------------------------------
}
