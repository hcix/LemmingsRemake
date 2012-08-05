package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalButtonUI;

import utilities.AnimatedGif;
import utilities.ImageHelper;

@SuppressWarnings("serial")
public class ControlButton extends JButton{
//-----------------------------------------------------------------------------
	private AnimatedGif animation;
	private ImageIcon image;
	private boolean pressed;
	private int controlId;
	private Integer count;
	private ButtonModel model;
//-----------------------------------------------------------------------------
	public ControlButton(){
		super(); 
		count = null;
		this.pressed = false;
		this.setSelected(false);
		model = new ControlModel();
		this.setModel(model);
		this.setPreferredSize(new Dimension(63, 100));//this.setPreferredSize(new Dimension(60, 100));
		//this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//{setOpaque(true);}
		//this.setBackground(Color.DARK_GRAY);
		//this.setForeground(Color.DARK_GRAY);
		//this.setContentAreaFilled(true);
		//this.setUI(new MetalButtonUI());
	}
//-----------------------------------------------------------------------------
	public void setCount(int num){
		count = num;
	}
//-----------------------------------------------------------------------------
	public int decrementCount(){
		if((count-1) >= 0){ count--; }
		this.repaint();
		return count;
	}
//-----------------------------------------------------------------------------
	public int incrementCount(){
		count++;
		this.repaint();
		return count;
	}
//-----------------------------------------------------------------------------
	public int getControlId(){
		return this.controlId;
	}
//-----------------------------------------------------------------------------
	public void setControlId(int controlId){
		this.controlId = controlId;
		if(controlId<9){ this.setName("SKILL"); }
		else{ this.setName("CONTROL"); }
	}
//-----------------------------------------------------------------------------
	public boolean isPressed(){
		return pressed;
	}
//-----------------------------------------------------------------------------
	public void setPressed(boolean pressed){
		this.pressed = pressed;
		if(animation!=null){
			if(!pressed){ animation.pause(0); }
		}
		
		this.repaint();
	}
//-----------------------------------------------------------------------------
	public void animate(){
		if(animation!=null){
			animation.animate(); 
		}
	}
//-----------------------------------------------------------------------------
	public void setImage(String filename){
		image = ImageHelper.getImage(filename);
		this.setIcon(image);
	}
//-----------------------------------------------------------------------------
	public void setUnpressedImg(String filename){ }
//-----------------------------------------------------------------------------
	public void setAnimation(String filename){
		animation = ImageHelper.getImageAnim(filename);
		animation.pause(0);
		this.setIcon(animation);
	}
//-----------------------------------------------------------------------------
//	public void paint(Graphics g){
//		super.paint(g);
//		g.setColor(Color.GRAY);
//		//g.fillRect(0, 0, 63, 100);
//	}
//-----------------------------------------------------------------------------
	public void paintComponent(Graphics g){
		//g.setColor(Color.LIGHT_GRAY);
		
		
		if(pressed){ 
			//g.setColor(Color.GRAY);
			//g.fillRect(0, 0, 63, 100);
			this.setBackground(Color.DARK_GRAY);
			//this.setForeground(Color.DARK_GRAY);
		}
		super.paintComponent(g);
		
//		if(animation!=null){
//			animation.paintIcon(this, g, 10, 30);
//		} else{
//			image.paintIcon(this, g, 10, 30);
//		}
		
		
		

		if(count!=null){
			g.setColor(Color.BLACK);
			g.drawString(count+"", 25, 20);
		}
	}
//-----------------------------------------------------------------------------
	private class ControlButtonUI extends MetalButtonUI{
		public void paint(Graphics g, JComponent c){
			
			super.paint(g, c);
		}
	}
//=============================================================================
	private class ControlModel extends DefaultButtonModel{
	      public void setPressed(boolean b) {
	    	  if(pressed){ super.setPressed(true); }
	      }
	}
//=============================================================================
}
