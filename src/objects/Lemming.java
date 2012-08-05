package objects;

import game.GameManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import skills.BashSkill;
import skills.Behavior;
import skills.BlockSkill;
import skills.BuildSkill;
import skills.ClimbSkill;
import skills.DieBehavior;
import skills.DigSkill;
import skills.ExplodeSkill;
import skills.FloatSkill;
import skills.HomeBehavior;
import skills.MineSkill;
import skills.Skill;
import utilities.AnimatedGif;
import utilities.ImageHelper;

public class Lemming extends GameObject {
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	/** Integers representing the possible states that a lemming can be in; a 
	 * lemming is always in exactly one state */
	public static final int STATE_UNBORN = -1;
	public static final int STATE_DEAD = -2;
	public static final int STATE_HOME = -3;
	public static final int STATE_FALLING = 0;
	public static final int STATE_DYING = 2;
	public static final int STATE_WALKING = 1;
//	public static final int STATE_SKILLED = 3;

	/**Integers representing right and left */
	public static final int DIR_LEFT = 0;
	public static final int DIR_RIGHT = 1;
	
	/** Lemmings that collide with the ground at speeds greater than this constant will die */
	private static final float KILL_SPEED = 0.12f;//0.1f; //0.15f;
	private static final float FALL_TRIGER = 0.05f;//0.03f;
	
//	private static final ObjectBounds WALK_BOUNDS = new ObjectBounds( new Rectangle(1, 0, 7, 2), 
//			new Rectangle(3, 0, 2, 8), new Rectangle(5, 0, 2, 8), new Rectangle(3, 8, 4, 9) );
	private static final ObjectBounds WALK_BOUNDS = new ObjectBounds( new Rectangle(1, 0, 7, 2), 
			new Rectangle(3, 0, 1, 10), new Rectangle(6, 0, 1, 10), new Rectangle(3, 10, 4, 6) );
	
	private static final ObjectBounds FALL_BOUNDS = new ObjectBounds( null, null, null,
			new Rectangle(3, 8, 4, 4) );

	/** Walking speed */
	private static final float WALKING_SPEED = 0.02f;
	/** The number of Lemming objects that have been created for this level */
	private static int numberOfLemmings = 0;
	
	/** Current state */
	private int state;
	/** The behavior assigned to this Lemming, if any */
	Behavior behavior;
	/** An list of all the animations, grouped by type w/ left & right together */
	private ArrayList<AnimatedGif[]> animations;
	
	protected boolean onGround=true;//lemmings initially fall from sky
	protected int direction=DIR_RIGHT;//usually lemmings initially face right
	protected float dx;
	protected float dy;
	private int id;
	private boolean getsGravity = true;
	private boolean fastForwardMode = false;
	
	//DEBUG
	public static final boolean DEBUG_LEM = false;
//-----------------------------------------------------------------------------
	public Lemming(){ 
		super();
		//super(FileHelper.getLemmingAnimFileName("lemming_walk_r"), true);
		this.setName("lemming");
		//Setup the default initial values
		setState(STATE_UNBORN);
		setDirection(DIR_RIGHT);
		setVelocityX(0);
		setVelocityY(0);
		setOnGround(false);
		setId(numberOfLemmings++);
		
		//Create the anims list
		animations = new ArrayList<AnimatedGif[]>();//new ArrayList<ImageIcon[]>();
		createAnimationList();
		
		if(DEBUG_LEM){ {setOpaque(false);} }//DEBUG
		this.paused = false;
	}
//-----------------------------------------------------------------------------
	public void update(GameManager gm, long elapsedTime){
		//If there is an assigned behavior, update it
		if(behavior!=null){
			behavior.update(gm, elapsedTime);
			if(behavior.isDone()){
				behavior = null; //skillId = -1;
				if(state>=0){
					if(this.isOnGround()){
						setState(STATE_WALKING);
						this.setObjectBounds(WALK_BOUNDS);
					} else{
						setState(STATE_FALLING);
						this.setObjectBounds(FALL_BOUNDS);
					}
				}
			}
		} 
		
		//Change the lemming's state to falling if appropriate
		if(!onGround && getVelocityY()>=FALL_TRIGER){
			setState(STATE_FALLING);
		}
	
	}
//-----------------------------------------------------------------------------
	/**
	 * Performs changes necessary after a lemming has had a horizontal collision.
	 * Reverses the lemming's direction and changes the animation to reflect the
	 * direction change.
	 */
	public void collideHorizontal(){
		//Change the velocity accordingly
		setVelocityX(-1*dx);
		
		//Flip the direction
		changeDirection();
		
		//Set the animation to reflect the direction change
		this.setAnimation(animations.get(state)[direction]);
	}
//-----------------------------------------------------------------------------
	public void collideVertical(){
		if(state==STATE_FALLING){ 
			if(getVelocityY()>KILL_SPEED){
				//DEBUG System.out.printf("splat\n");
				behavior = new DieBehavior(this, DieBehavior.SPLUT_DEATH);
			} else{
				setState(STATE_WALKING); 
				if(direction==DIR_RIGHT){ //right facing
					setVelocityX(WALKING_SPEED); 
				} else{//left facing 
					setVelocityX(-1*WALKING_SPEED); 
				}
			}
		}
		if(dy>0){
			setOnGround(true);
			setVelocityY(0);
		}
	}
//-----------------------------------------------------------------------------
	public void fastForward(){
		if(this.getAnimIcon()!=null){
			this.getAnimIcon().setSpeedFactor(2);//this.getAnimIcon().adjustSpeed(2);
		}
		fastForwardMode = true;
	}
//-----------------------------------------------------------------------------
	public void restoreNormalSpeed(){
		if(this.getAnimIcon()!=null){
			this.getAnimIcon().setSpeedFactor(1);//this.getAnimIcon().adjustSpeed(0.5);
		}
		fastForwardMode = false;
	}
//-----------------------------------------------------------------------------
	/**
	 * Kills this lemming. Removes its animation image and tags it for removal
	 * from the game.
	 */
	public void kill(){
		setState(STATE_DEAD);
//DEBUG System.out.printf("Lemming: kill() called \n");
		//Remove the animation
		this.removeAll();
	}
//-----------------------------------------------------------------------------
	/**
	 * Drowns this lemming.
	 */
	public void drown(){
		behavior = new DieBehavior(this, DieBehavior.DROWN_DEATH);
	}
//-----------------------------------------------------------------------------
	/**
	 * Sends this lemming home.
	 */
	public void sendHome(){
		behavior = new HomeBehavior(this);
	}
//-----------------------------------------------------------------------------
	/** 
	 * Reverses this lemming's horizontal direction.
	 */
	public void changeDirection(){		
		if(direction==DIR_RIGHT){
			setDirection(DIR_LEFT);
		} else {
			setDirection(DIR_RIGHT);
		}
	}
//-----------------------------------------------------------------------------
	/**
	 * Returns this lemmings current state.
	 * @return an integer representing this lemming's current state
	 */
	public int getState(){
		return this.state;
	}
//-----------------------------------------------------------------------------
	/**
	 * Sets the lemming's state.
	 * @param state - the new state value
	 */
	public void setState(int state){
		this.state = state;
		switch(state){
			case STATE_WALKING:
				this.setAnimation(animations.get(state)[direction]);
				this.setObjectBounds(WALK_BOUNDS);
				if(direction==DIR_RIGHT){//right facing
					setVelocityX(WALKING_SPEED); 
				} else{//left facing 
					setVelocityX(-1*WALKING_SPEED); 
				}
				break;
			case STATE_FALLING:
				setAnimation(animations.get(state)[direction]);
				this.setObjectBounds(FALL_BOUNDS);
				this.setVelocityX(0);
			default: break;
		}
	}
//-----------------------------------------------------------------------------
	public void assignSkill(int skillId){
		switch(skillId){
			case Skill.CLIMB_SKILL:
				behavior = new ClimbSkill(this);
				break;
			case Skill.DIG_SKILL:
				behavior = new DigSkill(this);
				break;
			case Skill.MINE_SKILL:
				behavior = new MineSkill(this);
				break;
			case Skill.BASH_SKILL:
				behavior = new BashSkill(this);
				break;
			case Skill.FLOAT_SKILL:
				behavior = new FloatSkill(this);
				break;
			case Skill.BLOCK_SKILL:
				behavior = new BlockSkill(this);
				break;
			case Skill.EXPLODE_SKILL:
				behavior = new ExplodeSkill(this);
				break;
			case Skill.BUILD_SKILL:
				behavior = new BuildSkill(this);
				break;
//			case Behavior.DIE_BEHAVIOR:
//				behavior = new DieBehavior(this, DieBehavior.SPLUT_DEATH);
//				break;
			default: break;
		}
		//this.setState(STATE_SKILLED);
	}
//-----------------------------------------------------------------------------
	/**
	 * Sets the appropriate animation to be displayed.
	 */
	public void setAnimation(AnimatedGif anim){
		if(fastForwardMode){ anim.setSpeedFactor(2); }//anim.adjustSpeed(2); }
		this.setAnimIcon(anim);
	}
//-----------------------------------------------------------------------------
	public void setAnimation(ImageIcon imgIcn){
		this.setImageIcon(imgIcn);
	}
//-----------------------------------------------------------------------------
	public int getId(){
		return id;
	}
//-----------------------------------------------------------------------------
	public void setId(int id){
		this.id = id;
	}
//-----------------------------------------------------------------------------
	public int getDirection(){
		return direction;
	}
//-----------------------------------------------------------------------------
	public void setDirection(int direction){
		this.direction = direction;
	}
//-----------------------------------------------------------------------------
	public float getVelocityX(){
		return dx;
	}
//-----------------------------------------------------------------------------
	public void setVelocityX(float dx){
		this.dx = dx;
	}
//-----------------------------------------------------------------------------
	public void setVelocityY(float dy){
		this.dy = dy;
	}
//-----------------------------------------------------------------------------
	public float getVelocityY(){
		return dy;
	}
//-----------------------------------------------------------------------------
	public boolean isOnGround(){
		return onGround;
	}
//-----------------------------------------------------------------------------
	public void setOnGround(boolean onGround){
		this.onGround=onGround;
	}
//-----------------------------------------------------------------------------
	public boolean shouldApplyGravity(){
		return getsGravity;
	}
//-----------------------------------------------------------------------------
	public void setGetsGravity(boolean getsGravity){
		this.getsGravity = getsGravity;
	}
//-----------------------------------------------------------------------------
	private void createAnimationList(){
		String[] lems = { "fall", "walk" };
		
		//Add all the lemmings animations
		for(String l : lems){
			AnimatedGif leftAnim = ImageHelper.getLemmingAnim("lemming_"+l+"_l");
			AnimatedGif rightAnim = ImageHelper.getLemmingAnim("lemming_"+l+"_r");
			AnimatedGif[] lemIcns = { leftAnim, rightAnim };
			animations.add(lemIcns);
		}

	}
//-----------------------------------------------------------------------------
	//DEBUG
	public void paintComponent(Graphics g){
		if(DEBUG_LEM){ 
			if(state<0){
				g.setColor(Color.GRAY);
			} else{
				if(g instanceof Graphics2D){
					Graphics2D g2 = (Graphics2D)g;
					g2.setColor(Color.GREEN);
					Rectangle top = bounds.top;//bounds.getTop(x, y);
					g2.fill(top);
					
					g2.setColor(Color.ORANGE);
					Rectangle bottom = bounds.bottom;//bounds.getBottom(x, y);
					g2.fill(bottom);
					
					g2.setColor(Color.RED);
					Rectangle left = bounds.left;//bounds.getLeft(x, y);
					g2.fill(left);
					
					g2.setColor(Color.BLUE);
					Rectangle right = bounds.right;
					g2.fill(right);

					g2.dispose();
				}
			} 
		} else{
			super.paintComponent(g); 
		}
	}	
//-----------------------------------------------------------------------------
}
