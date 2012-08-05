package utilities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
//-----------------------------------------------------------------------------
/**
 * Static helper methods for image related tasks.
 *
 */
public class ImageHelper {
//-----------------------------------------------------------------------------
	/**
	 * Gets the background image for the specified level. The background image is
	 * located in the program's levels/bgs directory.
	 * @param category
	 * @param level
	 * @return
	 */
	public static ImageIcon getLevelBackgroundImage(String category, int level){
		String filename = FileHelper.getLevelBackgroundFileName(category, level);
    	Image original = new ImageIcon(filename).getImage();
    	Image resized = getScaledImage(original, (original.getWidth(null)*2), 
    			(original.getHeight(null)*2));
    	
    	return (new ImageIcon(resized));
	}
//-----------------------------------------------------------------------------
	/**
	 * Gets an image located in the program's graphics/images directory.
	 * @param imgName
	 * @return
	 */
	public static ImageIcon getImage(String imgName){
		String filename = FileHelper.getImageFileName(imgName);
		
		return (new ImageIcon(filename));
	}
//-----------------------------------------------------------------------------
	public static AnimatedGif getImageAnim(String imgName){
		String filename = FileHelper.getImageFileName(imgName);
		
		return (new AnimatedGif(filename));
	}
//-----------------------------------------------------------------------------
	/**
	 * Gets an image or animation located in the specified directory within 
	 * the program's level directory.
	 */
	public static ImageIcon getLevelResource(String dir, String resource){
		String filename = FileHelper.getLevelResourceFileName(dir, resource);
		
		return (new ImageIcon(filename));
	}
//-----------------------------------------------------------------------------
	public static AnimatedGif getLevelAnimation(String dir, String resource){
		String filename = FileHelper.getLevelResourceFileName(dir, resource);
		
		return (new AnimatedGif(filename));
	}
//-----------------------------------------------------------------------------
	/**
	 * Gets an animated GIF image located in the program's levels/anims directory.
	 * @param animName
	 * @return
	 */
	public static ImageIcon getAnimation(String animName){
		String filename = FileHelper.getAnimFileName(animName);
		
		return (new ImageIcon(filename));
	}
//-----------------------------------------------------------------------------
	/**
	 * Gets an image located in the program's levels/objects directory.
	 * @param imgName
	 * @return
	 */
	public static ImageIcon getLevelObjectImage(String imgName){
		String filename = FileHelper.getLevelObjectImgFileName(imgName);
		//DEBUG System.out.printf("\ngetLevelObjectImage() filename = %s  \n", filename);
		//DEBUG if(new ImageIcon(filename)==null){ System.out.printf("null img \n"); }
		return (new ImageIcon(filename));
	}
//-----------------------------------------------------------------------------
	/**
	 * Gets an image located in the program's graphics/lemming_anims directory.
	 * @param animName
	 * @return
	 */
	public static AnimatedGif getLemmingAnim(String animName){
		String animFilename = FileHelper.getLemmingAnimFileName(animName);
		
		return (new AnimatedGif(animFilename, true));
	}
//	public static ImageIcon getLemmingAnim(String animName){
//		String animFilename = FileHelper.getLemmingAnimFileName(animName);
//		
//		return (new ImageIcon(animFilename));
//	}
//-----------------------------------------------------------------------------
	/** 
	 * Scales/Resizes a given <code>Image</code> to the specified size.
	 * @param srcImg - the <code>Image</code> to be resized
	 * @param w - the desired width
	 * @param h - the desired height 
	 * @return a scaled version of the given <code>Image</code>
	 */
    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
        		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
//-----------------------------------------------------------------------------
  	/** 
  	 * Scales/Resizes a given <code>ImageIcon</code> to the specified size.
  	 * @param srcImg - the <code>ImageIcon</code> to be resized
  	 * @param w - the desired width
  	 * @param h - the desired height 
  	 * @return a scaled version of the given <code>ImageIcon</code>
  	 */
      public static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h){
          BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
          Graphics2D g2 = resizedImg.createGraphics();
          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
          		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
          g2.drawImage(srcImg.getImage(), 0, 0, w, h, null);
          g2.dispose();
          return (new ImageIcon(resizedImg));
      }
//-----------------------------------------------------------------------------
    /**
     * Creates and returns a BufferedImage created from the specified
     * image file.
     * @param filename - the image file to create a BufferedImage from
     * @return a BufferedImage created from the specified image file
     */
    public static BufferedImage getBufferedImage(String filename){
    	Path path = Paths.get(filename);
    	
    	ImageIcon imgIcon = new ImageIcon(path.toString());
    	
    	BufferedImage image = new BufferedImage(imgIcon.getIconWidth(), imgIcon.getIconHeight(),
    			BufferedImage.TYPE_INT_RGB);
    	Graphics2D g2 = image.createGraphics();
    	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
        		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    	g2.drawImage(image, 0, 0, null);
    	g2.dispose();
    	
    	return image;
    }
//-----------------------------------------------------------------------------
}
