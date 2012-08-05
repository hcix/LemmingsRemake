package objects;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import utilities.AnimatedGif;

//-----------------------------------------------------------------------------
public class GameObject extends JLabel{
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	protected ImageIcon imageIcon;
	protected AnimatedGif animIcon;
	protected ObjectBounds bounds;
	protected float x;
	protected float y;
	private Integer layer = 1;
	protected boolean paused = true;
//-----------------------------------------------------------------------------
	public GameObject(){
		this.imageIcon = null;
		this.animIcon = null;
	}
//-----------------------------------------------------------------------------
	public GameObject(ImageIcon imageIcon){
		setImageIcon(imageIcon);
	}
//-----------------------------------------------------------------------------
	public GameObject(AnimatedGif animIcon){
		this.animIcon = animIcon;
	}
//-----------------------------------------------------------------------------
	public ImageIcon getImageIcon(){
		return imageIcon;
	}
//-----------------------------------------------------------------------------
	public AnimatedGif getAnimIcon(){
		return animIcon;
	}
//-----------------------------------------------------------------------------
	public void setImageIcon(ImageIcon imageIcon){
		this.imageIcon = imageIcon;
		this.setIcon(imageIcon);
		this.setSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
	}
//-----------------------------------------------------------------------------
	public void setAnimIcon(AnimatedGif animIcon){
		this.animIcon = animIcon;
		this.setIcon(animIcon);
		this.setSize(new Dimension(animIcon.getIconWidth(), animIcon.getIconHeight()));
	}
//-----------------------------------------------------------------------------
	public ObjectBounds getObjectBounds(){
		return bounds;
	}
//-----------------------------------------------------------------------------
	public void setObjectBounds(ObjectBounds bounds){
		this.bounds = bounds;
	}
//-----------------------------------------------------------------------------
	public float getXVal(){
		return x;
	}
//-----------------------------------------------------------------------------
	public void setX(float x){
		this.x = x;
		int xVal = Math.round(x);
		int yVal = Math.round(this.y);
		this.setLocation(new Point(xVal, yVal));
	}
//-----------------------------------------------------------------------------
	public float getYVal(){
		return y;
	}
//-----------------------------------------------------------------------------
	public void setY(float y){
		this.y = y;
		int xVal = Math.round(this.x);
		int yVal = (int)y;
		this.setLocation(new Point(xVal, yVal));
	}
//-----------------------------------------------------------------------------
	public void setLayer(int layer){
		this.layer = new Integer(layer);
	}
//-----------------------------------------------------------------------------
	public Integer getLayer(){
		return layer;
	}
//-----------------------------------------------------------------------------
	public void togglePause(){
		paused = !paused;
		if(animIcon!=null){ 
			if(paused){ animIcon.pause(); }
			else{ animIcon.animate(); }
		}
	}
//-----------------------------------------------------------------------------
	public void makeInvisible(){
		this.removeAll();
		this.setIcon(null);
	}
//-----------------------------------------------------------------------------
	public void makeVisible(){
		this.setIcon(imageIcon);
		this.setSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
	}
//-----------------------------------------------------------------------------
}
