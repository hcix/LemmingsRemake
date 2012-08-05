package game;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import objects.GameObject;
import objects.Lemming;
import objects.ObjectBounds;
import objects.Trap;
import utilities.AnimatedGif;
import utilities.FileHelper;
import utilities.ImageHelper;

public class Level {
	private static final String TITLE = "title";
	private static final String PASSWORD = "password";
	private static final String RATE = "rate";
	private static final String LEMMINGS = "lemmings";
	private static final String GOAL = "goal";
	private static final String PLAY_TIME = "playTime";
	private static final String LEMMING_TYPES = "lemmingTypes";
	private static final String DOOR = "door";
	private static final String EXIT = "exit";
	private static final String OBJECT = "object";
	private static final String TRAP = "trap";
	private static final String MAP = "map";
	private static final String LAYER = "layer";
	private static final String XVAL = "xVal";
	private static final String YVAL = "yVal";
	private static final String FILENAME = "filename";
	private static final String DIR = "dir";
	
	private String title;
	private String password;
	private int minRate;
	private int rate;
	private int lemmings;
	private int goal;
	/** The time limit in seconds */
	private int playTime;
	/** The number of climbing lemming types */
	private int climbers;
	/** The number of floating lemming types */
	private int floaters;
	/** The number of exploding lemming types */
	private int exploders;
	/** The number of blocking lemming types */
	private int blockers;
	/** The number of building lemming types */
	private int builders;
	/** The number of bashing lemming types */
	private int bashers;
	/** The number of mining lemming types */
	private int miners;
	/** The number of digging lemming types */
	private int diggers;

	private int levelNum;
	private Point start;
	private int mapHeight;
	private int mapWidth;
	private String category;
	private char[][] grid;
	private Image bgImage = null;
	private int lemmingsRescued;
	public boolean doneLoadingGrid = false;
	private GameManager gm;
	private int startX;
	private int mins;
	private int secs;
	private int time;
//-----------------------------------------------------------------------------
	public Level(String category, int levelNum, GameManager gm){
		this.gm = gm;
		this.category = category;
		this.levelNum = levelNum;
		start = new Point();
		loadBackgroundImage();
		load(category, levelNum);
	}
//-----------------------------------------------------------------------------
	public boolean update(long elapsedTime){
		time+=elapsedTime;
		if(time>=1000){
			time = time % 1000;
			playTime--;
			if(--secs<0){
				mins--;
				secs = 59;
			}
			if(mins<0){//times up
				return true;
			}
		}
		return false;
	}
//-----------------------------------------------------------------------------
	public void setBgImage(Image bgImage){
		this.bgImage = bgImage;
	}
//-----------------------------------------------------------------------------
	public Image getBgImage(){
		return bgImage;
	}
//-----------------------------------------------------------------------------
	public char[][] getGrid(){ 
		return grid; 
	}
//-----------------------------------------------------------------------------
	public String getTitle() {
		return title;
	}
//-----------------------------------------------------------------------------	
	public void setTitle(String title) {
		this.title = title;
	}
//-----------------------------------------------------------------------------
	public String getPassword() {
		return password;
	}
//-----------------------------------------------------------------------------
	public void setPassword(String password) {
		this.password = password;
	}
//-----------------------------------------------------------------------------
	public int getRate() {
		return rate;
	}
//-----------------------------------------------------------------------------
	public void setRate(int rate) {
		this.rate = rate;
		gm.setReleaseRate(rate);
	}
//-----------------------------------------------------------------------------
	public int getMinRate(){
		return minRate;
	}
//-----------------------------------------------------------------------------
	public void setMinRate(int minRate){
		this.minRate = minRate;
	}
//-----------------------------------------------------------------------------	
	public int getLemmings() {
		return lemmings;
	}
//-----------------------------------------------------------------------------	
	public void setLemmings(int lemmings) {
		this.lemmings = lemmings;
	}
//-----------------------------------------------------------------------------
	public int getGoal() {
		return goal;
	}
//-----------------------------------------------------------------------------
	public void setGoal(int goal) {
		this.goal = goal;
	}
//-----------------------------------------------------------------------------
	public int getPlayTime() {
		return playTime;
	}
//-----------------------------------------------------------------------------
	public void setPlayTime(int playTime) {
		this.playTime = playTime;
		mins = playTime / 60;
		secs = playTime - (mins*60);
	}
//-----------------------------------------------------------------------------
	public String getClock(){
		String clock = mins + ":";
		String sec = secs + "";
		if(sec.length()==1){ clock += "0"; }
		clock+=sec;
		return clock;
	}
//-----------------------------------------------------------------------------
	public int getClimbers() {
		return climbers;
	}
//-----------------------------------------------------------------------------
	public void setClimbers(int climbers) {
		this.climbers = climbers;
	}
//-----------------------------------------------------------------------------
	public boolean useClimber(){
		if(--climbers>=0){ 
			return true;
		} else {
			climbers++;
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getFloaters() {
		return floaters;
	}
//-----------------------------------------------------------------------------
	public void setFloaters(int floaters) {
		this.floaters = floaters;
	}
//-----------------------------------------------------------------------------
	public boolean useFloater(){
		if(--floaters>=0){ 
			return true;
		} else {
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getExploders() {
		return exploders;
	}
//-----------------------------------------------------------------------------
	public void setExploders(int exploders) {
		this.exploders = exploders;
	}
//-----------------------------------------------------------------------------
	public boolean useExploder(){
		if(--exploders>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getBlockers() {
		return blockers;
	}
//-----------------------------------------------------------------------------
	public void setBlockers(int blockers) {
		this.blockers = blockers;
	}
//-----------------------------------------------------------------------------
	public boolean useBlocker(){
		if(--blockers>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getBuilders() {
		return builders;
	}
//-----------------------------------------------------------------------------
	public void setBuilders(int builders) {
		this.builders = builders;
	}
//-----------------------------------------------------------------------------
	public boolean useBuilder(){
		if(--builders>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getBashers() {
		return bashers;
	}
//-----------------------------------------------------------------------------
	public void setBashers(int bashers) {
		this.bashers = bashers;
	}
//-----------------------------------------------------------------------------
	public boolean useBasher(){
		if(--bashers>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getMiners() {
		return miners;
	}
//-----------------------------------------------------------------------------
	public void setMiners(int miners) {
		this.miners = miners;
	}
//-----------------------------------------------------------------------------
	public boolean useMiner(){
		if(--miners>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public int getDiggers() {
		return diggers;
	}
//-----------------------------------------------------------------------------
	public void setDiggers(int diggers) {
		this.diggers = diggers;
	}
//-----------------------------------------------------------------------------
	public boolean useDigger(){
		if(--diggers>=0){
			return true;
		} else { 
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public String getCategory() {
		return category;
	}
//-----------------------------------------------------------------------------
	public void setCategory(String category) {
		this.category = category;
	}
//-----------------------------------------------------------------------------
	public int getLevelNum() {
		return levelNum;
	}
//-----------------------------------------------------------------------------
	public void setLevelNum(int levelNum) {
		this.levelNum = levelNum;
	}
//-----------------------------------------------------------------------------
	public Point getStart(){
		return start;
	}
//-----------------------------------------------------------------------------
	public void setStart(Point start){
		this.start = start;
	}
//-----------------------------------------------------------------------------
	public void setStartX(int x){
		start.setLocation(x, start.y);
	}
//-----------------------------------------------------------------------------
	public void setStartY(int y){
		start.setLocation(start.x, y);
	}
//-----------------------------------------------------------------------------
	public int getMapWidth() {
		return mapWidth;
	}
//-----------------------------------------------------------------------------
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
//-----------------------------------------------------------------------------
	public int getMapHeight() {
		return mapHeight;
	}
//-----------------------------------------------------------------------------
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
//-----------------------------------------------------------------------------
	public char getTileAt(int r, int c){
		return (grid[r][c]);
	}
//-----------------------------------------------------------------------------
	public void setTileAt(int r, int c, char newChar){
		this.grid[r][c] = newChar;
	}
//-----------------------------------------------------------------------------
	public void setLemmingsRescued(int lemmingsRescued){
		this.lemmingsRescued = lemmingsRescued;
	}
//-----------------------------------------------------------------------------
	public int getLemmingsRescued(){
		return lemmingsRescued;
	}
//-----------------------------------------------------------------------------
//	public void setStartX(int startX){
//		this.startX = startX;
//	}
//-----------------------------------------------------------------------------
	public int getStartX(){
		return startX;
	}
//-----------------------------------------------------------------------------
	/**
	 * Loads the background image for the specified map.
	 */
	  public void loadBackgroundImage(){
	  	Image bgImg = ImageHelper.getLevelBackgroundImage(category, levelNum).getImage();
	  	setBgImage(bgImg);
	  	
	  	this.mapWidth = bgImg.getWidth(null) / 2;
	  	this.mapHeight = bgImg.getHeight(null) / 2;
	  }
//-----------------------------------------------------------------------------
    /** 
     * Loads the appropriate level from the level file.
     * @param category
     * @param levelNum
     */
    public void load(String category, int levelNum){
  		String levelFile = FileHelper.getLevelFileName(category, levelNum);
  		
  		try{
  			//Setup for XML parsing
  			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
  			InputStream in = new FileInputStream(levelFile);
  			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
  			
  			//Parse the XML document
  			GameObject object = null;
  			Trap trap = null;
  			String dir = null;
  			while (eventReader.hasNext()) {
  				XMLEvent event = eventReader.nextEvent();
  
  				//Process XML start element tag
  				if(event.isStartElement()) {
  					StartElement startElement = event.asStartElement();
  					String startName = startElement.getName().getLocalPart();
  					if (startName.equals(DOOR)){
  						object = new GameObject();
  						object.setName(DOOR);
  					} else if(startName.equals(EXIT)){
  						object = new GameObject();
  						object.setName(EXIT);
  					}else if(startName.equals(OBJECT)){
  						object = new GameObject();
  						object.setName(OBJECT);
  					} else if(startName.equals(TRAP)){
  						trap = new Trap();
  						object = new GameObject();
  						object.setName(TRAP);
  					} 
  					if(startName.equals(FILENAME)){
  						event = eventReader.nextEvent();
  						//Get the directory the file is in from the element's attribute
  						for(Iterator i = startElement.getAttributes(); i.hasNext();){
  							Attribute attribute = (Attribute) i.next();
  							String attrName = attribute.getName().getLocalPart();
  							if(attrName.equals(DIR)){
  								dir = attribute.getValue();
  							}
  						}
  						//Get the directory and filename to create & add object's image
  						if(event.isCharacters()){
  							if(dir!=null){
  								if(dir.equals("anims") || dir.equals("traps")){
									AnimatedGif anim = ImageHelper.getLevelAnimation( dir,
											event.asCharacters().getData() );
									ObjectBounds b = new ObjectBounds(anim.getIconWidth(), 
											anim.getIconHeight());
		  							object.setAnimIcon(anim);
		  							object.setObjectBounds(b);
  								} else{
  									ImageIcon imgIcn = ImageHelper.getLevelResource(dir, 
  											event.asCharacters().getData() );
  									ObjectBounds b = new ObjectBounds(imgIcn.getIconWidth(), 
  											imgIcn.getIconHeight());
  		  							object.setImageIcon(imgIcn);
  		  							object.setObjectBounds(b);
  								}
  							}
  						}
  						continue;
  					}
  					if(startName.equals(LAYER)){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int layer = Integer.decode(event.asCharacters().getData());
  							object.setLayer(layer);
  						}
  						continue;
  					}
  					if(startName.equals(XVAL)){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int xVal = Integer.decode(event.asCharacters().getData());
  							if(object.getName().equals(DOOR)){//object is the door
  								int w = object.getWidth();
  								int lemW = 12;//width of a lemming falling from door
  								if(w>0){
  									//x value of start point is in the middle of image
  									int doorMidX = xVal + (w/2);
  									setStartX(doorMidX - (lemW/2));
  								}
  							}
  							object.setX(xVal); 
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals(YVAL)){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int yVal = Integer.decode(event.asCharacters().getData());
  							if(object.getName().equals(DOOR)){//object is door
  								//Use the door object's location to set level's start pt
  								int h = object.getHeight();
  								if(h>0){
  									//y value of start point is just above bottom of door
  									setStartY((yVal+4));
  								}
  								//Make sure door appears in front of lemming so lemming 
  								//  appear so fall out of door rather than just appear in 
  								//  front of it
  								//object.setLayer(GameScreen.LEMMINGS_LAYER + 1);
  							}
  							object.setY(yVal);
  						}
  						continue;
  					}
  					if(startName.equals("trapX")){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int x = Integer.decode(event.asCharacters().getData());
  							trap.setTrapX(x);
  						}
  						continue;
  					}
  					if(startName.equals("trapY")){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int y = Integer.decode(event.asCharacters().getData());
  							trap.setTrapY(y);
  						}
  						continue;
  					}
  					if (startElement.getName().getLocalPart().equals(TITLE)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							setTitle(event.asCharacters().getData());
  						}
  						continue;
  					}
  					if (startElement.getName().getLocalPart().equals(PASSWORD)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters())
  							setPassword(event.asCharacters().getData());
  						continue;
  					}
  					else if(startElement.getName().getLocalPart().equals("start")){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int start = Integer.decode(event.asCharacters().getData());
  							startX = start;
  						}
  						continue;
  					}
  					if (startElement.getName().getLocalPart().equals(RATE)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int rate = Integer.decode(event.asCharacters().getData());
  							setMinRate(rate);
  							setRate(rate);
  							gm.setReleaseRate(rate);
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals(LEMMINGS)){
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int lemmings = Integer.decode(event.asCharacters().getData());
  							setLemmings(lemmings);
							//Add the specified number of lemmings to the list
							while(lemmings>0){
								gm.addLemming(new Lemming());
								//System.out.printf("Level: lemming added to gm\n");
								lemmings--;
							}
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals(GOAL)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int goal = Integer.decode(event.asCharacters().getData());
  							setGoal(goal);
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals("time")) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int time = Integer.decode(event.asCharacters().getData());
  							trap.setExecutionTime(time);
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals(PLAY_TIME)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							int playTime = Integer.decode(event.asCharacters().getData());
  							setPlayTime(playTime);
  						}
  						continue;
  					}
  					if (startElement.getName().getLocalPart().equals(MAP)) {
  						event = eventReader.nextEvent();
  						if(event.isCharacters()){
  							createGrid(event.asCharacters().getData());
  						}
  						continue;
  					}
  					if(startElement.getName().getLocalPart().equals(LEMMING_TYPES)){
  			  			String name = "";
  			  			while(!name.equals(LEMMING_TYPES)){
  			  				event = eventReader.nextEvent();
  			  				if(event.isStartElement()) {
  			  					StartElement stElmnt = event.asStartElement();
  			  					name = stElmnt.getName().getLocalPart();
  			  					event = eventReader.nextEvent();
  			  					int num = Integer.decode(event.asCharacters().getData());
  			  					if (name.equals("climbers")){
  			  						setClimbers(num);
  			  					} else if(name.equals("floaters")){
  			  						setFloaters(num);
  			  					} else if(name.equals("exploders")){
  			  						setExploders(num);
  			  					} else if(name.equals("blockers")){
  			  						setBlockers(num);
  			  					} else if(name.equals("builders")){
  			  						setBuilders(num);
  			  					} else if(name.equals("bashers")){
  			  						setBashers(num);
  			  					} else if(name.equals("miners")){
  			  						setMiners(num);
  			  					} else if(name.equals("diggers")){
  			  						setDiggers(num);
  			  					}
		  					} else if(event.isEndElement()){	
		  						//event = eventReader.nextEvent();
		  						EndElement endElmnt = event.asEndElement();
		  						name = endElmnt.getName().getLocalPart();
		  					}
		  				}
  			  			continue;
  					}
  				}
  				
  				//Process xml end element tag
  				if(event.isEndElement()) {
  					EndElement endElement = event.asEndElement();
  					String name = endElement.getName().getLocalPart();
  					if(name.equals(DOOR) | name.equals(EXIT) | name.equals(OBJECT)){
  						if(object!=null){
  							if(name.equals(DOOR)){ object.getAnimIcon().setLoops(1); }
  							//Add this object to the objects list
  							gm.addObject(object);
  							//DEBUG System.out.printf("Level: %s added to gm  \n", object.getName());
  						} 
  					} else if(name.equals(TRAP)){
  						trap.setX(object.getXVal());
  						trap.setY(object.getYVal());
  						trap.setAnimIcon(object.getAnimIcon());
  						//trap.setImageIcon(object.getImageIcon());
						trap.setObjectBounds(object.getObjectBounds());
						gm.addTrap(trap);
  					}
  				}
  				
  			}//the entire xml doc has been processed
  			
  		} catch(FileNotFoundException e){ System.err.printf("levelFile '%s' not found \n", levelFile);
  		} catch(XMLStreamException e){ System.err.println("XMLStreamException has occured\n"); }
  		
  		doneLoadingGrid = true;
  	}
//-----------------------------------------------------------------------------
    /**
	 * Processes and decompresses the input according to the following 
	 * level description.
	 * Rules:
	 * 	, = empty till end of row (all 0)
	 * 	,x = repeat line x times
	 * 	xxx = write xxx to level
	 * 	xy = repeat x y times
	 * 
	 * 	a =  empty (lemming can walk)
	 * 	b =  soft material (lemming can bash, mine or dig)
	 *  c =  soft material that can only be destroyed left to right
	 *  d =  soft material that can only be destroyed right to left
	 *  e =  hard material (lemming cannot get through)
	 *  f =  water (lemming will drown)
	 *  g =  fire / some traps (lemming will burn)
	 *  h =  trap
	 *  i =  exit
	 *  j =  shredder
	 *  
	 */
  	private void createGrid(String map){
  		//System.out.printf("mapHeight = %d ; mapWidth = %d\n", mapHeight, mapWidth);
  		grid = new char[mapHeight][mapWidth];
  		Pattern p = Pattern.compile("([a-z]*[0-9]*)*,?[0-9]*");
  		Matcher matcher = p.matcher(map);

  		int c = 0;
		int r = 0;
		char ch = 'z';
		char nc = 'z';
		int n = 0;
		int i = 0;

  		while(matcher.find()){  
  			String s = matcher.group();
  			i=0;
  			c=0;
  			try{
	  			while(i<s.length()){
	  				nc = s.charAt(i);
	  				if(!Character.isDigit(nc)){
	  					ch = s.charAt(i);
	  				}
	  				if(Character.isDigit(nc)){
						String num = "";
						while(Character.isDigit(nc)){
							num += nc;
							i++;
							if(i<s.length()){
								nc = s.charAt(i);
							} else{
								nc = 'z';
							}
							
						}
						n = Integer.parseInt(num);
						n--;
						n = Math.min((c+n), mapWidth);
						while(c<n){
							grid[r][c] = ch;
							c++;
						}
					} else if(Character.isLetter(nc)){
						grid[r][c] = ch;
						c++;
						if(c==mapWidth){ c=0; r++; }
						i++;
					} else if(Character.isWhitespace(nc)){
						i++;
					} else if(nc==','){
						//check if number comes after and if so repeat row
						i++;
						//fill the remainder of the row with a's
						while(c<mapWidth){
							grid[r][c] = 'a';
							c++;
						}
						r++;
						c=0;
						if(i<s.length()){
							nc = s.charAt(i);
							if(Character.isDigit(nc)){
								String num = "";
								while(Character.isDigit(nc)){
									num += nc;
									i++;
									if(i<s.length()){
										nc = s.charAt(i);
									} else{
										nc = 'z';
									}
								}//got all the digit of the num
								n = Integer.parseInt(num);
								int count = 1;//int count = 2;
									while(count<n){
										grid[r] = grid[(r-1)];
										//System.out.printf("r = %d\n", r);
										r++;
										c=0;
										count++;
									}
							}//end of if next char is digit
						}//end of if(i<s.length())
					}//end of else if(nc==',')
				}//end of while i<s.length()
  			} catch(ArrayIndexOutOfBoundsException e){
  				//System.out.printf("n = %d, count = %d, r = %d\n", n, count, r);
  			}
	  	}//while matches still exist
  		
  	
  		//doneLoadingGrid = true;
  			
  	}
//-----------------------------------------------------------------------------
}
