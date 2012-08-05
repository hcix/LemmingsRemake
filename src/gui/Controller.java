package gui;

import game.GameManager;
import game.GameModel;
import game.Level;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import net.miginfocom.swing.MigLayout;
import objects.Lemming;

import org.swixml.SwingEngine;

import skills.Skill;
//-----------------------------------------------------------------------------
@SuppressWarnings("serial")
public class Controller extends JPanel implements MouseInputListener{//, ActionListener {
//-----------------------------------------------------------------------------
	/** Integers representing the various game controls */
	public static final int INCREASE_RR_CONTROL = 9;
	public static final int DECREASE_RR_CONTROL = 10;
	public static final int PAUSE_CONTROL = 11;
	public static final int FAST_FORWARD_CONTROL = 12;
	public static final int NUKE_CONTROL = 13;
	
	public static final String SKILL = "SKILL";
	public static final String CONTROL = "CONTROL";

	private boolean active;
	private int selectedSkill;
	private Point mouse;
	private ControlButton selected;
	private JPanel controlButtons;
	private GameManager gm;
	private GameModel model;
	private Timer timer;
	private SwingEngine engine;
//-----------------------------------------------------------------------------
	public Controller(GameManager gm, GameModel model){
		this.setLayout(new MigLayout());
		this.gm = gm;
		this.model = model;
		active = false;
		selectedSkill = -1;
		mouse = new Point();
		
		try{
			engine = new SwingEngine(this);
			engine.getTaglib().registerTag("ControlButton", ControlButton.class);
			engine.render("xmlLayouts/controller.xml");
			this.add(engine.getRootComponent());
			engine.setMouseListener(controlButtons, this);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
//-----------------------------------------------------------------------------
	/**
	 * Checks if the skill matching the specified index is available and decrements
	 * its count if it is. Returns true if after decrementing the number of the skill
	 * type available, the result is grater or equal to zero. Returns false
	 * otherwise.
	 * @param skillId - the id of the corresponding skill to check
	 * @return whether or not the specified skill is available for use
	 */
	private boolean checkSkillAvaliability(int skillId){
		Level l = gm.getLevel();
		
		switch(skillId){
			case Skill.BASH_SKILL:
				return (l.useBasher());
			case Skill.BLOCK_SKILL:
				return (l.useBlocker());
			case Skill.BUILD_SKILL:
				return (l.useBuilder());
			case Skill.CLIMB_SKILL:
				return (l.useClimber());
			case Skill.DIG_SKILL:
				return (l.useDigger());
			case Skill.EXPLODE_SKILL:
				return (l.useExploder());
			case Skill.FLOAT_SKILL:
				return (l.useFloater());
			case Skill.MINE_SKILL:
				return (l.useMiner());
			default: return false;
		}
	}
//-----------------------------------------------------------------------------
	public void activate(){
		//Set lemming counts on the skill buttons
		setLemmingCounts(gm.getLevel());
		setStats();
		active = true;
	}
//-----------------------------------------------------------------------------
	public void deactivate(){
		//Unclick the selected button
		if(selected!=null){
			selected.setPressed(false);
			selected = null;
		}
		//Set the selected skill to no skill
		selectedSkill = Skill.NO_SKILL;
		active = false;
	}
//-----------------------------------------------------------------------------
	public boolean isActive(){
		return active;
	}
//-----------------------------------------------------------------------------
	private void setLemmingCounts(Level level){
		((ControlButton)engine.find("climbButton")).setCount(level.getClimbers());
		((ControlButton)engine.find("floatButton")).setCount(level.getFloaters());
		((ControlButton)engine.find("explodeButton")).setCount(level.getExploders());
		((ControlButton)engine.find("blockButton")).setCount(level.getBlockers());
		((ControlButton)engine.find("buildButton")).setCount(level.getBuilders());
		((ControlButton)engine.find("bashButton")).setCount(level.getBashers());
		((ControlButton)engine.find("mineButton")).setCount(level.getMiners());
		((ControlButton)engine.find("digButton")).setCount(level.getDiggers());
		((ControlButton)engine.find("moreButton")).setCount(level.getRate());
		((ControlButton)engine.find("lessButton")).setCount(level.getMinRate());
		
		this.revalidate();
		this.repaint();
	}
//-----------------------------------------------------------------------------
	private void setStats(){
		final JTextField outField = ((JTextField)engine.find("outField"));
	    final JTextField homeField = ((JTextField)engine.find("homeField"));
		final JTextField timeField = ((JTextField)engine.find("timeField"));
		int time = gm.getLevel().getPlayTime();
		int min = time/60;
    	timeField.setText(min + ":00");
    	
    	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
            	final Level level = gm.getLevel();
                //Call complicated code here
            	timer = new Timer(500, new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                    	//messageField.setText(t)
                    	outField.setText(gm.getLemmingsOut()+"");
                    	homeField.setText(gm.getLemmingsRescued()+"");
                    	timeField.setText(level.getClock());
                    }
                });
            	timer.start();
            	return null;
            }
            protected void done() {  }
        };
	    worker.execute();
	     
	}
//-----------------------------------------------------------------------------
	private void handleControlButtonClick(int controlId){
		switch(controlId){
			//Pause a game in progress, or resume a paused game
			case PAUSE_CONTROL:
				if(model.getGameState()==GameModel.GAMESTATE_PAUSED){
					model.resumeGame();
				} else{
					model.pauseGame();
				}
				break;
			//Put the game into fast forward mode
			case FAST_FORWARD_CONTROL:
				if(((ControlButton)engine.find("speedButton")).isPressed()){//increase game speed
					model.fastForwardGameplay(); 
					gm.fastForward();
				} else{//reduce game speed
					model.resumeNormalSpeed(); 
					gm.restoreNormalSpeed();
				}
				break;
			//Increase the lemming's release rate
			case INCREASE_RR_CONTROL:
				if(gm.getLevel().getRate()<99){
					gm.getLevel().setRate(
							((ControlButton)engine.find("moreButton")).incrementCount());
				}
				break;
			//Decrease the lemming's release rate
			case DECREASE_RR_CONTROL:
				if(gm.getLevel().getRate()>gm.getLevel().getMinRate()){
					gm.getLevel().setRate(
							((ControlButton)engine.find("moreButton")).decrementCount());
				}
				break;
			//Nuke all the lemmings & end the level
			case NUKE_CONTROL:
				gm.nukeThemAll();
				break;
		default: break;
		}
	}
//-----------------------------------------------------------------------------
	private void handleSkillButtonClick(int skillId){
		if(selectedSkill==skillId){//skill is currently selected 
			//Unselect the skill
			selectedSkill = -1;
		} else {//skill is not currently selected 
			this.selectedSkill = skillId;
		}
		
	}
//-----------------------------------------------------------------------------
	public void actionPerformed(ActionEvent ev) {
		Integer command = 0;
		
		//Determine what happened and route it accordingly
		try{
			command = Integer.decode(ev.getActionCommand());
		} catch(Exception ec){ System.out.printf("InputManager invalid action occured\n"); }

		if(command<9){
			handleSkillButtonClick(command);
		} else {
			handleControlButtonClick(command);
		}

	}
//-----------------------------------------------------------------------------
	public Point getMouseLocation(){
		return mouse;
	}
//=============================================================================
////MouseInputListener Methods////
//-----------------------------------------------------------------------------
	public void mouseClicked(MouseEvent e) {
		
		//Handle a click on one of the buttons on the controller
		if(e.getSource() instanceof ControlButton){
			ControlButton cb = (ControlButton)e.getSource();
			
			if(cb.getControlId()==INCREASE_RR_CONTROL || cb.getControlId()==DECREASE_RR_CONTROL){
				//do nothing, state info is already handled
			} else if(cb.isPressed()){//'button' is already pressed
				//Unpress the 'button'
				if(selected==cb){ selected = null; }
				cb.setPressed(false);
			} else if(cb.getControlId()<9 && selected!=null){//a different skill is currently selected
				//Unpress the other 'button' 
				selected.setPressed(false);
				//Press the button that triggered this event
				cb.setPressed(true);
				selected = cb;
			} else{//no skill currently selected or button is not a skill button
				//Press the button that triggered this event
				if(cb.getName().equals(SKILL)){ 
					selected = cb; 
				}
				cb.setPressed(true);
			}

			ActionEvent event = new ActionEvent(this, 
					ActionEvent.ACTION_PERFORMED, (cb.getControlId()+""));
			actionPerformed(event);
		}
		
		//Handle a click on the game screen
		else if(e.getSource() instanceof JLayeredPane){
			//If there's a skill selected and a lemming targeted,apply the skill to the lemming
			Lemming targetedLemming = gm.getTargetedLemming();
			if( (targetedLemming!=null) && (selectedSkill>=0) ){
				if(checkSkillAvaliability(selectedSkill)){
					targetedLemming.assignSkill(selectedSkill);
					setLemmingCounts(gm.getLevel());
				}
			}
		}
		
	}	
//-----------------------------------------------------------------------------
	public void mouseEntered(MouseEvent e) {
		if(e.getSource() instanceof JLayeredPane){
			//Save the mouse's new location
			mouse = e.getPoint();
		}
	}
//-----------------------------------------------------------------------------
	public void mouseMoved(MouseEvent e) {
		if(e.getSource() instanceof JLayeredPane){
			//Save the mouse's new location
			mouse = e.getPoint();
		}
		
	}
//-----------------------------------------------------------------------------
	public void mousePressed(MouseEvent e) { 
		if(e.getSource() instanceof ControlButton){
			final ControlButton src = (ControlButton)e.getSource();
			//src.setPressed(true);
			
			if(src.getControlId()==INCREASE_RR_CONTROL || src.getControlId()==DECREASE_RR_CONTROL){
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			        public Void doInBackground() {
			            //Call complicated code here
			        	timer = new Timer(100, new ActionListener() {
			                public void actionPerformed(ActionEvent e) {
			                	handleControlButtonClick(src.getControlId());
			                }
			            });
			        	timer.start();
			        	return null;
			        }
			        protected void done() {  }
			    };
			    worker.execute();
			} else{
				src.animate();
			}
			
		}

	}
//-----------------------------------------------------------------------------
	public void mouseReleased(MouseEvent e) { 
		if(e.getSource() instanceof ControlButton){
			ControlButton src = (ControlButton)e.getSource();
			//src.setPressed(false);
			
			if(src.getControlId()==INCREASE_RR_CONTROL || src.getControlId()==DECREASE_RR_CONTROL){
				if(timer!=null){ timer.stop(); }
				src.setPressed(false);
			}
		}
	}
//-----------------------------------------------------------------------------
	public void mouseDragged(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
//-----------------------------------------------------------------------------
}
