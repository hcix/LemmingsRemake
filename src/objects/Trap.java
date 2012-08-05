package objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import utilities.AnimatedGif;

public class Trap extends GameObject {
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	private boolean inAction = false;	
	private int executionTime;// = 1500;
	private long timer = 0;
	private int trapX;
	private int trapY;
	//DEBUG
	private static final boolean DEBUG = false;
//-----------------------------------------------------------------------------
	public Trap(){
		super();
		//this.paused = true;
		this.setName("trap");
	}
//-----------------------------------------------------------------------------
	public Trap(AnimatedGif anim){
		super(anim);
		//this.paused = true;
		this.setName("trap");
	}
//-----------------------------------------------------------------------------
//	public Trap(int executionTime){
//		super();
//		this.executionTime = executionTime;
//	}
//-----------------------------------------------------------------------------
	public int getTrapX(){
		return trapX;
	}
//-----------------------------------------------------------------------------
	public void setTrapX(int trapX){
		this.trapX = trapX;
	}
//-----------------------------------------------------------------------------
	public int getTrapY(){
		return trapY;
	}
//-----------------------------------------------------------------------------
	public void setTrapY(int trapY){
		this.trapY = trapY;
	}
//-----------------------------------------------------------------------------
	public void setExecutionTime(int executionTime){
		this.executionTime = executionTime;
	}
//-----------------------------------------------------------------------------
	public boolean isInAction(){
		return inAction;
	}
//-----------------------------------------------------------------------------
	public void trigger(){
		//System.out.printf("Trap trigger()-ed \n");
		inAction = true;
		this.paused = false;
		this.getAnimIcon().animate();
	}
//-----------------------------------------------------------------------------
	public void update(long elapsedTime){
		if(inAction){
			timer+=elapsedTime;
			if(timer>=executionTime){
				this.paused = true;
				animIcon.pause(animIcon.getTotalFrames()-1);
				inAction = false;
				timer = 0;
			}
		}
	}
//-----------------------------------------------------------------------------
	public void setAnimIcon(AnimatedGif animIcon){
		animIcon.pause(animIcon.getTotalFrames()-1);
		//animIcon.setLoops(1);
		super.setAnimIcon(animIcon);
	}
//-----------------------------------------------------------------------------
	//DEBUG
//	public void paintComponent(Graphics g){
//		if(DEBUG){ 
//			if(g instanceof Graphics2D){
//				Graphics2D g2 = (Graphics2D)g;
//				g2.setColor(Color.GREEN);
//				Rectangle rect = new Rectangle(this.getWidth(), this.getHeight());
//				g2.fill(rect);
//				g2.dispose();
//			}
//		}
//	}	
//-----------------------------------------------------------------------------
}
