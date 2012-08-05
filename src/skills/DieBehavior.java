package skills;

import game.GameManager;
import objects.Lemming;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class DieBehavior implements Behavior {
//-----------------------------------------------------------------------------
	public static final int DROWN_DEATH = 0;
	public static final int SPLUT_DEATH = 1;
	public static final int FRY_DEATH = 2;
	
	private static final int DROWN_TIME = 1600;
	private static final int SPLUT_TIME = 1600;
	private static final int FRY_TIME = 1400;
	
	private static final String DROWN = "drown";
	private static final String SPLUT = "splut";
	private static final String FRY = "fry";
	private AnimatedGif[] dyingAnims = new AnimatedGif[3];
	
	/** How long it takes the Lemming to die, based on how he's dying */
	private int dieTime;
	/** Keeps track of time passed */
	private long timer;
	private boolean done = false;
	private String name;
	/** The Lemming assigned this behavior */
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public DieBehavior(Lemming lemming, int deathType){
		this.lemming = lemming;
		loadImages();
		init(deathType);
		timer = 0;
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime){
		timer += elapsedTime;
		if(timer>=dieTime){
			lemming.kill();
		}
	}
//-----------------------------------------------------------------------------
	public boolean isDone(){
		return done;
	}
//-----------------------------------------------------------------------------
	public String getName(){
		return name;
	}
//-----------------------------------------------------------------------------
	private void init(int deathType){
		switch(deathType){
			case DROWN_DEATH:
				name = DROWN;
				dieTime = DROWN_TIME;
				break;
			case SPLUT_DEATH:
				name = SPLUT;
				dieTime = SPLUT_TIME;
				break;
			case FRY_DEATH:
				name = FRY;
				dieTime = FRY_TIME;
				break;
			default: break;
		}
		lemming.setVelocityX(0);
		lemming.setVelocityY(0);
		lemming.setGetsGravity(false);
		lemming.setAnimation(dyingAnims[deathType]);
		lemming.setState(Lemming.STATE_DYING);

	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		String[] lems = { "drown", "splut", "fry" };
		
		//Add all the lemmings images
		int i = 0;
		for(String l : lems){
			dyingAnims[i] = ImageHelper.getLemmingAnim("lemming_"+l+"_s");
			i++;
		}
		
	}
//-----------------------------------------------------------------------------
}
