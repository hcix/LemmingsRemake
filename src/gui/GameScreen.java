package gui;

import game.CollisionDetector;
import game.GameManager;
import game.ScreenManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import utilities.ImageHelper;
//-----------------------------------------------------------------------------
public class GameScreen extends JPanel{
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	private int SCREEN_WIDTH = 980;
//	private int SCREEN_HEIGHT = 500;

	public static final Integer BACKGROUND_LAYER = 0;
	public static final Integer OBJECTS_LAYER_1 = 1;
	public static final Integer OBJECTS_LAYER_2 = 2;
	public static final Integer OBJECTS_LAYER_3 = 3;
	public static final Integer OBJECTS_LAYER_4 = 3;
	public static final Integer LEMMINGS_LAYER = 5;
	public static final Integer TARGET_LAYER = 6;

	ScreenManager screenManager;
	GameManager gameManager;
	PlayScreen playScreen;
	Controller controller;
	private Image bgImage;
	private GameObject target;
	private boolean targetAcquired;
	private boolean active;
	
	private static final boolean DEBUG = false;//DEBUG
//-----------------------------------------------------------------------------
	public GameScreen(ScreenManager sm, GameManager gm, Controller controller){
		super(new MigLayout("", "[]", "[][]"));
		this.screenManager = sm;
		this.gameManager = gm;
		this.controller = controller;
		this.active = false;
		
		//Create playscreen where animation takes place
		playScreen = new PlayScreen();
		playScreen.setName("playscreen");
		playScreen.addMouseListener(controller);
		playScreen.addMouseMotionListener(controller);
		//Create the target to show which lemming is being targeted, if any
		ImageIcon targetImg = ImageHelper.getImage("target.gif");
		target = new GameObject(targetImg);
		ObjectBounds b = new ObjectBounds(targetImg.getIconWidth(), targetImg.getIconHeight());
		target.setObjectBounds(b);
		targetAcquired = false;

		this.revalidate();
		this.repaint();
	}
//-----------------------------------------------------------------------------
	public void render(){
		if(!active){ return; }//do render if not in active state
		
			//Add any newly created objects to the screen
			ArrayList<GameObject> newlyCreated = gameManager.getNewlyCreated();
			Iterator<GameObject> objects = newlyCreated.iterator();
			while(objects.hasNext()){
				GameObject obj = objects.next();
				if(obj.getName()!=null){
					if(obj.getName().equals("lemming")){
						playScreen.add(obj, LEMMINGS_LAYER);
					} else{
						if(!DEBUG) playScreen.add(obj, obj.getLayer());
						if(obj.getName()!="trap"){ obj.togglePause(); }
					}
				}
				objects.remove();
			}
			
			int close = 13;
			targetAcquired = false;
			ArrayList<Lemming> lemmingList = gameManager.getLemmingList();
			//Place the target over the lemming it is closest to, if it is close to any
			Iterator<Lemming> ll = lemmingList.iterator();
			while(ll.hasNext()){//for(Lemming lemming : lemmingList){
				Lemming l = ll.next();
				int state = l.getState();
				if(state==Lemming.STATE_DEAD || state==Lemming.STATE_HOME){
					playScreen.remove(l);//remove the lemming from the screen
					ll.remove();//remove the lemming from the gm's list
					if(state==Lemming.STATE_HOME){ gameManager.upLemmingsRescuedCount(); }
					continue;
				}
				//Point mouse = input.getMouseLocation();
				Point mouse = controller.getMouseLocation();
				int distance = (int)mouse.distance(l.getX(), l.getY());
				if( (!targetAcquired) && (distance<close) ){
					close = distance;
					targetAcquired = true;
					target.setX(l.getX()-12);
					target.setY((l.getY()-7));
					gameManager.setTargetedLemming(l);
				} 
			}
			
			//If a lemming is targeted, make the target visible
			if(targetAcquired){ 
				target.makeVisible();
			} else{
				target.makeInvisible();
				gameManager.setTargetedLemming(null);
			}

		//controller.update();
		playScreen.revalidate();
		playScreen.repaint();
	}
//-----------------------------------------------------------------------------
	public void activate(int start){
		bgImage = gameManager.getLevel().getBgImage();
		int w = bgImage.getWidth(null);
		int h = bgImage.getHeight(null);
		
		//Setup the screen where all the action will take place
		playScreen.setBackground();
		playScreen.add(target, TARGET_LAYER);
		target.makeInvisible();
		playScreen.setPreferredSize(new Dimension(w, h));
		playScreen.setSize(new Dimension(w, h));
				
		//Put the game play screen into a scroller
		JScrollPane scrollPane = new JScrollPane(playScreen);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		playScreen.scrollRectToVisible(new Rectangle(start, 0, 1, 1));
		
		//Adjust the game play scroller screen size according to image dimensions
		if(w<SCREEN_WIDTH){
			this.add(scrollPane, "wrap, width "+(w+5)+", height "+(h+20) );
		} else {
			this.add(scrollPane, "wrap, width "+SCREEN_WIDTH+", height "+(h+20) );
		}
		
		//Add and activate the game controller
		controller.activate();
		this.add(controller);
				
		active = true;//this.setActive(true);
		this.revalidate();
	}
//-----------------------------------------------------------------------------
	public void deactivate(){
		//DEBUG System.out.printf("GameScreen: deactivate()  \n");
		controller.deactivate();
		playScreen.removeAll();
		this.removeAll();
		active = false; //this.setActive(false);
	}
//-----------------------------------------------------------------------------
	public boolean isActive(){
		return active;
	}
//=============================================================================
	@SuppressWarnings("serial")
	private class PlayScreen extends JLayeredPane{
		PlayScreen(){ }
		
		public void setBackground(){
			if(DEBUG){
				debugBackground();
			} else {
				ImageIcon bg = new ImageIcon(bgImage);
				JLabel background = new JLabel(bg);
				background.setSize(new Dimension(bgImage.getWidth(null), bgImage.getHeight(null)));
				this.setSize(new Dimension(bgImage.getWidth(null), bgImage.getHeight(null)));
				this.add(background, BACKGROUND_LAYER);
			}
		}
		public void debugBackground(){
			DebugPanel panel = new DebugPanel();
			panel.setSize(new Dimension(bgImage.getWidth(null), bgImage.getHeight(null)));
			add(panel, 0);
		}
/*		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}
		public void setMaxUnitIncrement(int pixels) {
			maxUnitIncrement = pixels;
		}
		public int getScrollableUnitIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			//Get the current position.
			int currentPosition = 0;
			if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
			} else {
			currentPosition = visibleRect.y;
			}

			//Return the number of pixels between currentPosition
			//and the nearest tick mark in the indicated direction.
			if (direction < 0) {
			int newPosition = currentPosition -
			   (currentPosition / maxUnitIncrement)
			    * maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
			} else {
			return ((currentPosition / maxUnitIncrement) + 1)
			* maxUnitIncrement
			- currentPosition;
			}
		}
		public int getScrollableBlockIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			if (orientation == SwingConstants.HORIZONTAL) {
				return visibleRect.width - maxUnitIncrement;
			} else {
				return visibleRect.height - maxUnitIncrement;
			}
		}
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
*/
	}
//=============================================================================
	@SuppressWarnings("serial")
	private class DebugPanel extends JPanel{
		DebugPanel(){
			{setOpaque(true);}
		}
		public void paintComponent(Graphics g){
			if(bgImage!=null){
				//g.drawImage(bgImage, 0, 0, null);				
				if(gameManager.getLevel()!=null){
					if(gameManager.getLevel().doneLoadingGrid){
						char[][] grid = gameManager.getLevel().getGrid();
						for(int r=0; r<grid.length; r++){
							int len = grid[r].length;
							for(int c=0; c<len; c++){
								char cur = grid[r][c];
								if(cur=='a'){ 
									g.setColor(Color.BLACK);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='b'){
									g.setColor(Color.GREEN);
									g.drawRect(CollisionDetector.tilesToPixels(c),
											CollisionDetector.tilesToPixels(r), 2, 2);
								//	g.fillRect(CollisionDetector.tilesToPixels(c), 
								//			   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='c'){
									g.setColor(Color.GREEN);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='d'){
									g.setColor(Color.GREEN);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='e'){
									g.setColor(Color.GREEN);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='f'){
									g.setColor(Color.BLUE);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='g'){
									g.setColor(Color.YELLOW);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='h'){
									g.setColor(Color.RED);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='i'){
									g.setColor(Color.YELLOW);
									g.fillRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								} else if(cur=='j'){
									g.setColor(Color.GRAY);
									g.drawRect(CollisionDetector.tilesToPixels(c), 
											   CollisionDetector.tilesToPixels(r), 2, 2);
								}
							}
						}
					}
				}
			}
		}//end of paintComponent method
	}
//=============================================================================
//-----------------------------------------------------------------------------
}
