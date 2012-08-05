package game;

import java.awt.Point;
import java.awt.Rectangle;

import objects.Lemming;

public class CollisionDetector {
//-----------------------------------------------------------------------------
    private static final int TILE_SIZE_BITS = 1;
    Level currentLevel;
//	private int mapWidth;
//	private int mapHeight;
//-----------------------------------------------------------------------------
	public CollisionDetector(Level currentLevel){ 
		this.currentLevel = currentLevel;
//		char[][] grid = currentLevel.getGrid();
//		mapHeight = grid.length;
//		mapWidth = grid[0].length;//this is ok since all rows have the same length
	}
//-----------------------------------------------------------------------------
/*	public boolean isCollision(Lemming l, float newX, float newY){
		char[][] grid = level.getGrid();
		
		//Get the pixel locations
	    float fromX = Math.min(l.getX(), newX);
	    float fromY = Math.min(l.getY(), newY);
	    float toX = Math.max(l.getX(), newX);
	    float toY = Math.max(l.getY(), newY);
	
	    //Get the tile locations
	    int fromTileX = pixelsToTiles(fromX);
	    int fromTileY = pixelsToTiles(fromY);
	    int toTileX = pixelsToTiles(toX + l.getWidth());
	    int toTileY = pixelsToTiles(toY + l.getHeight());
	    
	    //Check each tile for a collision
	    for(int c=fromTileX; c<=toTileX; c++){
	    	for(int r=fromTileY; r<=toTileY; r++){
	            if(c < 0 || c >= mapWidth || grid[r][c]!='a'){
	            	//collision found
	            	return true;
	            }
	        }
	    }
	    //No collision found
	    return false;	
	}*/
//-----------------------------------------------------------------------------
	public static Tile getTileCollision(Level level, Rectangle rect, float newX, float newY){
		char[][] grid = level.getGrid();
		int h = grid.length;
		int w = grid[0].length;
		
		//Get the pixel locations
	    float fromX = Math.min(rect.x, newX);
	    float fromY = Math.min(rect.y, newY);
	    float toX = Math.max(rect.x, newX);
	    float toY = Math.max(rect.y, newY);
	
	    //Get the tile locations
	    int fromTileX = pixelsToTiles(fromX);
	    int fromTileY = pixelsToTiles(fromY);
	    int toTileX = pixelsToTiles(toX + rect.width);
	    int toTileY = pixelsToTiles(toY + rect.height);

	    //Check each tile for a collision
	    for(int c=fromTileX; c<=toTileX; c++){
	    	for(int r=fromTileY; r<=toTileY; r++){
	    		try{
		            if(c<0 || c>=w || grid[r][c]!='a'){
		            	return (new Tile( (new Point(c, r)), grid[r][c]) );//collision found
		            	//return (new Point(c, r));//collision found
		            }
		    	} catch(ArrayIndexOutOfBoundsException e){
		    		System.out.printf("ArrayIndexOutOfBoundsException \n");
					if(r > h){ return (new Tile((new Point(c, h)), grid[r][c])); }
					else if(c > w){ return (new Tile((new Point(w, r)), grid[r][c])); }
					else { return (new Tile((new Point(c, r)), grid[r][c])); }
				}
	        }
	    }
	    //No collision found
	    return null;
	}
//-----------------------------------------------------------------------------
	public Tile getTileCollision(Rectangle rect, float newX, float newY){
		return (getTileCollision(currentLevel, rect, newX, newY));
	}
//-----------------------------------------------------------------------------
	public static Tile getTileCollision(Level level, Lemming l, float newX, float newY){
		char[][] grid = level.getGrid();
		//int h = grid.length;
		int w = grid[0].length;
		
		//Get the pixel locations
	    float fromX = Math.min(l.getX(), newX);
	    float fromY = Math.min(l.getY(), newY);
	    float toX = Math.max(l.getX(), newX);
	    float toY = Math.max(l.getY(), newY);
	
	    //Get the tile locations
	    int fromTileX = pixelsToTiles(fromX);
	    int fromTileY = pixelsToTiles(fromY);
	    int toTileX = pixelsToTiles(toX + l.getWidth());
	    int toTileY = pixelsToTiles(toY + l.getHeight());
	    
	    //Check each tile for a collision
	    for(int c=fromTileX; c<=toTileX; c++){
	    	for(int r=fromTileY; r<=toTileY; r++){
	            if(c < 0 || c >= w || grid[r][c]!='a'){
	            	//collision found, return the tile
	            	return (new Tile((new Point(c, r)), grid[r][c]));//return (new Point(c, r));
	            }
	        }
	    }
	    //No collision found
	    return null;
	}
//-----------------------------------------------------------------------------
    /**
	 * Converts a tile position to a pixel position.
	*/
	public static int tilesToPixels(int numTiles) {
		return numTiles << TILE_SIZE_BITS;
	}
//-----------------------------------------------------------------------------
	/**
	 * Converts a pixel position to a tile position.
	*/
	public static int pixelsToTiles(float pixels) {
	    return pixelsToTiles(Math.round(pixels));
	}
//-----------------------------------------------------------------------------
	/**
	 * Converts a pixel position to a tile position.
	*/
	public static int pixelsToTiles(int pixels) {
		//Use shifting to get correct values for negative pixels
	    return pixels >> TILE_SIZE_BITS;
	}
//-----------------------------------------------------------------------------
}
