package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
//-----------------------------------------------------------------------------
	public static String getProgramDirectoryName(){
		File file = new File(".");
		
		String progDir = null;
		try {
			progDir = file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return progDir;
	}
//-----------------------------------------------------------------------------
	public static String getLevelBackgroundFileName(String category, int level){
		Path path = Paths.get(getProgramDirectoryName(), "levels", 
				"bgs", (category+level+".gif") );
    	
		return path.toString();
	}
//-----------------------------------------------------------------------------
	/**
	 * 
	 * @param img
	 * @return
	 */
	public static String getImageFileName(String img){
		Path path = Paths.get(getProgramDirectoryName(), "graphics", "images", img);
		
		return path.toString();
	}
//-----------------------------------------------------------------------------
	public static String getLevelResourceFileName(String dir, String resource){
		Path path = Paths.get(getProgramDirectoryName(), "levels", dir, 
				(resource+".gif") );
		
		return path.toString();
	}
//-----------------------------------------------------------------------------
	public static String getAnimFileName(String img){
		Path path = Paths.get(getProgramDirectoryName(), "levels", "anims", 
				(img+".gif") );
		
		return path.toString();
	}
//-----------------------------------------------------------------------------
	public static String getLemmingAnimFileName(String img){
		Path path = Paths.get(getProgramDirectoryName(), "graphics", 
				"lemming_anims", (img+".gif"));
		return path.toString();
	}
//-----------------------------------------------------------------------------
	public static String getLevelObjectImgFileName(String obj){
		Path path = Paths.get(getProgramDirectoryName(), "levels", "objects",
				(obj+".gif"));

		return (path.toString());
	}
//-----------------------------------------------------------------------------
	public static String getLevelFileName(String category, int level){
		Path path = Paths.get(getProgramDirectoryName(), "levels", "data",
				category, ("level"+level+".xml") );

		return path.toString();
	}
//-----------------------------------------------------------------------------
	public static String getDirectoryFileName(String category){
		Path path = Paths.get(getProgramDirectoryName(), "levels", "data", 
				category, "dir.xml");

		return path.toString();
	}
//-----------------------------------------------------------------------------
}
