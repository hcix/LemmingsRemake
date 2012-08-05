package gui;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;

import utilities.FileHelper;

@SuppressWarnings("serial")
public class LevelsList extends JPanel{
//-----------------------------------------------------------------------------
	JList<String> levelsList;
//-----------------------------------------------------------------------------
	LevelsList(String category){
		//Load levels list into list model
		DefaultListModel<String> listModel = loadLevelDir(category);
		
		//Create and setup the list from the list model
		levelsList = new JList<String>(listModel);
		levelsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		levelsList.setVisibleRowCount(-1);
		//Set up the list so only one level can be selected at a time
		levelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Set the first level to be initially selected
		levelsList.setSelectedIndex(0);

		JScrollPane levelsScroller = new JScrollPane(levelsList);
		levelsScroller.setPreferredSize(new Dimension(400, 200));
		this.add(levelsScroller);
	}
//-----------------------------------------------------------------------------
	public int getSelectedLevel(){
		return (levelsList.getSelectedIndex() + 1);
	}
//-----------------------------------------------------------------------------
	private DefaultListModel<String> loadLevelDir(String category){
		String dirFile = FileHelper.getDirectoryFileName(category);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		
		try {
			//Setup to parse the XML
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(dirFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			
			//Parse the XML
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				String level = "";
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("level")){
						event = eventReader.nextEvent();
						if(event.isCharacters()){
							level = event.asCharacters().getData();
							listModel.addElement(level);
						}
						continue;
					}	
				}
			}
		} catch(FileNotFoundException e){
			System.out.printf("directory file '%s' not found \n", dirFile);
			e.printStackTrace();
		} catch(XMLStreamException e){
			System.out.printf("XMLStreamException has occured\n");
			e.printStackTrace();
		}
		
		return listModel;
	}
//-----------------------------------------------------------------------------
}
