package gui;

import game.Level;
import game.ScreenManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
//-----------------------------------------------------------------------------
public class GameOverScreen extends JPanel{
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
private static final String TOP_TITLE = "All lemmings\u2122 accounted for";
	private static final String PERFECT_TITLE = "Superb!";
	private static final String WON_TITLE = "Golly!";
	private static final String LOST_TITLE = "Rock Bottom!";
	private static final String PERFECT_MESSAGE = 
			"You rescued every lemming\u2122 on that level. Can you do it again... ?";
	private static final String WON_MESSAGE = 
			"You totally stormed that level! Let's see if you can storm the next...";
	private static final String LOST_MESSAGE = 
			"I hope for your sake that you nuked that level!";
	
	private static final Font BIG_FONT = new Font("Serif", Font.BOLD, 48);
	private static final Font MEDIUM_FONT = new Font("Serif", Font.BOLD, 38);
	private static final Font SMALL_FONT = new Font("Serif", Font.BOLD, 18);
	
	private ScreenManager sm;
	private Level completedLevel;
	private JPanel statsPanel;
	private JPanel controls;
	private boolean won;
	private boolean active;
//-----------------------------------------------------------------------------
	public GameOverScreen(ScreenManager sm){
		this.sm = sm;
		this.setLayout(new MigLayout());
		this.setBackground(Color.GREEN);//this.setBackground(Color.BLACK);
		active = false;
		completedLevel = null;
		statsPanel = new JPanel();
		statsPanel.setPreferredSize(new Dimension(500, 300));
		controls = createControlsPanel(won);
	}
//-----------------------------------------------------------------------------
	public boolean isActive(){
		return active;
	}
//-----------------------------------------------------------------------------
	public void activate(Level completedLevel){
		this.completedLevel = completedLevel;
		active = true;
		//createLayout();
		statsPanel.add(createStatsPanel());
		this.add(statsPanel, "wrap");
		this.add(controls);
	}
//-----------------------------------------------------------------------------
	public void deactivate(){
		//DEBUG System.out.printf("GameOverScreen: deactivate()  \n");
		completedLevel = null;
		active = false;
		statsPanel.removeAll();
		this.removeAll();
	}
//-----------------------------------------------------------------------------
	private JPanel createStatsPanel(){
		JPanel statsPanel = new JPanel(new MigLayout());
		JLabel title = new JLabel(TOP_TITLE, JLabel.CENTER);
		
		int percentNeeded = 
			calculatePercentage(completedLevel.getGoal(), completedLevel.getLemmings());
		int percentRescued = 
			calculatePercentage(completedLevel.getLemmingsRescued(), completedLevel.getLemmings());
		JLabel youNeeded = new JLabel("You needed "+percentNeeded+"%");
		JLabel youRescued = new JLabel("You rescued "+percentRescued+"%");
		
		if(percentRescued>=percentNeeded){ won = true; } 
		else { won = false; }

		title.setFont(BIG_FONT);
		youNeeded.setFont(SMALL_FONT);
		youRescued.setFont(SMALL_FONT);
		JLabel wonLostLabel;
		JLabel wonLostMessage;
		
		if(percentRescued==100){
			wonLostLabel = new JLabel(PERFECT_TITLE);
			wonLostMessage = new JLabel(PERFECT_MESSAGE);
		} else if(won){
			wonLostLabel = new JLabel(WON_TITLE);
			wonLostMessage = new JLabel(WON_MESSAGE);
		} else{
			wonLostLabel = new JLabel(LOST_TITLE);
			wonLostMessage = new JLabel(LOST_MESSAGE);
		}
		wonLostLabel.setFont(MEDIUM_FONT);
		wonLostMessage.setFont(SMALL_FONT);
		
		statsPanel.add(title, "wrap");
		statsPanel.add(youNeeded, "wrap");
		statsPanel.add(youRescued, "wrap");
		statsPanel.add(wonLostLabel, "wrap");
		statsPanel.add(wonLostMessage, "wrap");

		return statsPanel;
	}
//-----------------------------------------------------------------------------
	private int calculatePercentage(double number, double outOf){
		double percentage = number / outOf;
		//DEBUG System.out.printf("number = %f ; outOf = %f ; percentage = %f \n", number, outOf, percentage);
		
		return ((int)(percentage*100));
	}
//-----------------------------------------------------------------------------
	private JPanel createControlsPanel(boolean won){
		JPanel controls = new JPanel();
		
		JButton playAgainButton = new JButton("Play Again");
		playAgainButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String cat = completedLevel.getCategory();
				int level = completedLevel.getLevelNum();
				sm.showGameplayScreen(cat, level);
			}
		});
		controls.add(playAgainButton);
		
		//Only add the next level button if the player won this level & there
		//  is a next level
	//if(won && (completedLevel.getLevelNum()<10) ){
			JButton nextLevelButton = new JButton("Next Level");
			nextLevelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String cat = completedLevel.getCategory();
					int nextLevel = completedLevel.getLevelNum() + 1;
					sm.showGameplayScreen(cat, nextLevel);
				}
			});
			controls.add(nextLevelButton);
	//	}
		
		JButton menuButton = new JButton("Menu");
		menuButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sm.showMainMenu();
			}
		});
		controls.add(menuButton);

		return controls;
	}
//-----------------------------------------------------------------------------
}
