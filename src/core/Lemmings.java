package core;

import game.GameModel;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
//-----------------------------------------------------------------------------
public class Lemmings extends GameCore {
private static final long serialVersionUID = 1L;
//-----------------------------------------------------------------------------
	private static JPanel screen;
	private static GameModel model;
//-----------------------------------------------------------------------------
	/**
	 * Main method to start the thread that will run the main program.
	 */
	public static void main(String[] args) {
		new Lemmings().init();
	}
//-----------------------------------------------------------------------------
	/**
	 * Creates the GUI and puts it on the screen.
	 */
	private static void createAndShowGUI() {
		Toolkit toolkit =  Toolkit.getDefaultToolkit();
		Dimension screenDim = toolkit.getScreenSize();
		int w = (int)(screenDim.width*0.8);
	//System.out.printf("w = %d  \n", w);
		//int h = screenDim.height / 2;
		int h = 600;
		
	    //Create and set up the window
		JFrame frame = new JFrame("Lemmings");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		//Uses platform specific method for opening frame
		frame.setLocationByPlatform(true);
		//Set the size
		frame.setSize(w, h);
		frame.setPreferredSize(new Dimension(w, h));
		
		//Add the main game window
		//frame.add(new MainWindow(gm, gs));
		frame.add(screen);
		
		//Position the window in the middle of the screen
		frame.setLocationRelativeTo(null);
	    frame.pack();
	    //Show it
	    frame.setVisible(true);  
	}
//-----------------------------------------------------------------------------
	@Override
	public void initialize() {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		} catch(Exception e){
       		e.printStackTrace();
       	}
		screen = new JPanel();
		model = new GameModel(screen);
		createAndShowGUI();
	}
//-----------------------------------------------------------------------------
	@Override
	public void update(long elapsedMS) {
		model.update(elapsedMS);
	}
//-----------------------------------------------------------------------------
	@Override
	public void render() {
        repaint();
	}
//-----------------------------------------------------------------------------
}
