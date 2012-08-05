package skills;

import game.CollisionDetector;
import game.GameManager;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class BuildSkill implements Skill{
//-----------------------------------------------------------------------------
	private static final int ONE_COUNT = 3200;
	private static final int CONFUSED_TIME = 1600;
	private static final String NAME = "builder";
	private int dx = 4;
	private ObjectBounds BUILDER_BOUNDS = new ObjectBounds(null, new Rectangle(2, 10, 4, 8),
			new Rectangle(10, 10, 4, 8), new Rectangle(6, 18, 4, 6) );
	private AnimatedGif[] buildAnims = new AnimatedGif[2];
	private AnimatedGif[] confusedAnims = new AnimatedGif[2];
	private int counter = 0;
	private long timer = 0;
	private int phase = 0;
	private boolean done;
	private Lemming lemming;
//-----------------------------------------------------------------------------
	public BuildSkill(Lemming lemming){
		this.lemming = lemming;
		loadImages();
		init();
	}
//-----------------------------------------------------------------------------
	public void init(){
		lemming.setVelocityX(0);
		lemming.setAnimation(buildAnims[lemming.getDirection()]);
		lemming.setObjectBounds(BUILDER_BOUNDS);
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime){
		timer += elapsedTime;
		switch(phase){
			case 0:
				if(timer>=ONE_COUNT){
					timer = timer % ONE_COUNT;
					makeStep(gm);
					lemming.setY(lemming.getYVal()-2);
					lemming.setX(lemming.getXVal()+4);
					if(++counter==12){
						phase++; 
						timer = 0;
						lemming.setAnimation(confusedAnims[lemming.getDirection()]);
					}
				}
				break;
			case 1:
				if(timer>=CONFUSED_TIME){
					this.done = true;
				}
				break;
			default: break;
		}
	}
//-----------------------------------------------------------------------------
	private void makeStep(GameManager gm){
		ObjectBounds bounds = lemming.getObjectBounds();
		int stepY = lemming.getY() + bounds.getHeight() - 2;
		int stepX = 0;
		if(lemming.getDirection()==Lemming.DIR_RIGHT){
			Rectangle rect = bounds.getRight(lemming.getXVal(), lemming.getYVal());
			//stepX = rect.x + rect.width;
			stepX = lemming.getX() + 6;
		}
		
		ImageIcon stepImg = ImageHelper.getLevelObjectImage("step");
		GameObject step = new GameObject(stepImg);
		step.setSize(new Dimension(13, 2));
		step.setObjectBounds(new ObjectBounds(13, 2));
		step.setName("step");
		step.setX(stepX);
		step.setY(stepY);
		gm.addObject(step);
		
		int tileStartX = CollisionDetector.pixelsToTiles(stepX);
		int tileStartY = CollisionDetector.pixelsToTiles(stepY);
		int tileEndX = CollisionDetector.pixelsToTiles(stepX) + 6;
		int tileEndY = CollisionDetector.pixelsToTiles(stepY);
		
		for(int r=tileStartY; r<=tileEndY; r++){
			for(int c=tileStartX; c<=tileEndX; c++){
				gm.getLevel().setTileAt(r, c, 'b');
			}
		}
		
		
	}
//-----------------------------------------------------------------------------
	private void loadImages(){
		buildAnims[0] = ImageHelper.getLemmingAnim("lemming_"+NAME+"_l");
		buildAnims[1] = ImageHelper.getLemmingAnim("lemming_"+NAME+"_r");
		
		confusedAnims[0] = ImageHelper.getLemmingAnim("lemming_confused_l");
		confusedAnims[1] = ImageHelper.getLemmingAnim("lemming_confused_r");
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
