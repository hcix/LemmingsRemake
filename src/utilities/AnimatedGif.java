package utilities;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;

import animatedGifLib.GifDecoder;

public class AnimatedGif implements Icon{
	private ArrayList<GifFrame> frames;
	private int currentFrame = 0;
	private int totalFrames;
	private BufferedImage image;
	private boolean paused = true;
	private Thread animator;
	private double speedFactor = 1;
	private int loops = -1;//<0 means it plays infinitely by default
//-----------------------------------------------------------------------------
	private class Animate implements Runnable{
		private long sleepTime = 60;//default initial sleep time
		private boolean run = true;

		private void update(){
			//Check if the animation is on it's last frame
			if((currentFrame+1)>=totalFrames){
				currentFrame = 0;//restart animation
				//If animation doesn't loop indefinitely, update the loops var accordingly
				if(loops>=0){
					if(loops==0){//animation is completed 
						run = false; 
						return; 
					} else{//animation still has loops remaining
						loops--;
					}
				}
			} else{//switch animation to next frame
				currentFrame++; 
			}
			GifFrame frame = frames.get(currentFrame);
			image = frame.image;
			sleepTime = (long)(frame.millisecs / speedFactor);
		}
		
		public void run() {
			while(run){
//				checkElapsedTime();
				if(!paused){ update(); }
				try{
					Thread.sleep(sleepTime);//assume OS sleep times are precise enough
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
	}
//-----------------------------------------------------------------------------
	public AnimatedGif(String filename){
		//System.out.printf("AnimatedGif(%s)  \n", filename);
		frames = new ArrayList<GifFrame>();
		loadFrames(filename);
		image = frames.get(currentFrame).image;
		animator = new Thread(new Animate());
		animator.start();
	}
//-----------------------------------------------------------------------------
	/**
	 * 
	 * @param filename
	 * @param frame - the frame number to begin animating from
	 */
	public AnimatedGif(String filename, int frame){
		frames = new ArrayList<GifFrame>();
		currentFrame = frame;
		loadFrames(filename);
		image = frames.get(currentFrame).image;
		animator = new Thread(new Animate());
		animator.start();
	}
//-----------------------------------------------------------------------------
	public AnimatedGif(String filename, boolean startAnimation){
		this(filename);
		paused = !startAnimation;
	}
//-----------------------------------------------------------------------------
	public void animate(){
		paused = false;
	}
//-----------------------------------------------------------------------------
	public void pause(){
		paused = true;
	}
//-----------------------------------------------------------------------------
	/**
	 * Pauses the animation on the specified frame.
	 * @param frame - the frame number to pause the animation on
	 */
	public void pause(int frame){
		currentFrame = frame;
		image = frames.get(currentFrame).image;
		paused = true;
	}
//-----------------------------------------------------------------------------
	public void setSpeedFactor(double speedFactor){
		this.speedFactor = speedFactor;
	}
//-----------------------------------------------------------------------------
	/**
	 * Adjusts the speed of the animation by the factor specified. For
	 * example, to double the animation speed, call adjustSpeed() with
	 * factor=2. To slow down the animation to half of it's current 
	 * speed, call adjustSpeed() with factor=0.5.
	 * @param factor - the factor by which to adjust the speed
	 */
	public void adjustSpeed(double factor){
		speedFactor = speedFactor * factor;
	}
//-----------------------------------------------------------------------------
	public int getLoops(){
		return loops;
	}
//-----------------------------------------------------------------------------
	public void setLoops(int loops){
		this.loops = --loops;
	}
//-----------------------------------------------------------------------------
	public int getTotalFrames(){
		return totalFrames;
	}
//-----------------------------------------------------------------------------
	public int getIconWidth(){
		if(image!=null){ return image.getWidth(); }
		else{ return frames.get(0).image.getWidth(); }
	}
//-----------------------------------------------------------------------------
	public int getIconHeight(){
		if(image!=null){ return image.getHeight(); }
		else{ return frames.get(0).image.getHeight(); }
	}
//-----------------------------------------------------------------------------
	public void paintIcon(Component c, Graphics g, int x, int y){
		g.drawImage(image, x, y, c);
		c.repaint();
	}
//-----------------------------------------------------------------------------
	private void loadFrames(String filename){
		GifDecoder d = new GifDecoder();
		d.read(filename);
		totalFrames = d.getFrameCount();
		for (int i = 0; i < totalFrames; i++) {
			BufferedImage img = d.getFrame(i);//frame i
			int ms = d.getDelay(i);//display duration of frame in milliseconds
			frames.add(new GifFrame(img, ms));
		}
	}
//=============================================================================
	private class GifFrame{
		BufferedImage image;
		int millisecs;
		GifFrame(BufferedImage image, int millisecs){ 
			this.image = image;
			this.millisecs = millisecs;
		}
	}
//=============================================================================/
}
