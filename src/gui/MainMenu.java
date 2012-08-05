package gui;

import game.GameManager;
import game.ScreenManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.swixml.SwingEngine;

@SuppressWarnings("serial")
//-----------------------------------------------------------------------------
public class MainMenu extends JPanel implements ActionListener{
//-----------------------------------------------------------------------------
	private SwingEngine swix;
	private ScreenManager sm;
	JPanel mainMenu;
//-----------------------------------------------------------------------------
	public MainMenu(ScreenManager sm){
		this.sm = sm;
		try{
			swix = new SwingEngine(this);
		    swix.render("xmlLayouts/mainMenu.xml");
		    this.add(swix.getRootComponent());
		    swix.setActionListener(mainMenu, this);
		} catch(Exception e){
			e.printStackTrace();
		}
//		
//		JPanel p = (JPanel)swix.find("labelPanel");
//		p.add(new JLabel(new ImageIcon("/Users/heatherciechowski/workspace/LemmingsRemake" +
//				"/")));
	}
//-----------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();

		if(command.equals(GameManager.FUN)) {
			sm.displayFunLevels();
		} else if(command.equals(GameManager.TAXING)){
			sm.displayTaxingLevels();
		} else if(command.equals(GameManager.TRICKY)){
			sm.displayTrickyLevels();
		} else if(command.equals(GameManager.MAYHEM)){
			sm.displayMayhemLevels();
		}
	}
//-----------------------------------------------------------------------------
}
