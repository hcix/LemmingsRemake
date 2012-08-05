package gui;

import game.ScreenManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.swixml.SwingEngine;

@SuppressWarnings("serial")
public class LevelsMenu extends JPanel {
//-----------------------------------------------------------------------------
	private SwingEngine swix;
	private ScreenManager sm;
	private String currentCategory;
	private LevelsList levelsList;
//-----------------------------------------------------------------------------
	public LevelsMenu(ScreenManager sm){
		this.sm = sm;
		
		try{
			swix = new SwingEngine(this);
			swix.getTaglib().registerTag("LevelsList", LevelsList.class);
		    swix.render("xmlLayouts/levelsMenu.xml");
		    this.setLayout(new MigLayout("fill"));
		    this.add(swix.getRootComponent(), "growx, growy, center");
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
//-----------------------------------------------------------------------------
	public void showLevelDirectory(String category){
		this.currentCategory = category;
		
		JLabel title= (JLabel)swix.find("title");
		title.setText("Choose a Level - rating:" + category);
		
		JPanel levels = (JPanel)swix.find("levels");
		levelsList = new LevelsList(category);
		levels.removeAll();
		levels.add(levelsList);
	
		this.revalidate();
		this.repaint();
	}
//-----------------------------------------------------------------------------
	public void play(){
		int selectedLevel = levelsList.getSelectedLevel();		
		sm.showGameplayScreen(currentCategory, selectedLevel);
	}
//-----------------------------------------------------------------------------
	public void backToMain(){
		sm.showMainMenu();
	}
//-----------------------------------------------------------------------------
}
