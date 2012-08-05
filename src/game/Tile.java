package game;

import java.awt.Point;
//-----------------------------------------------------------------------------
/**
 * Represents a tile within a level.
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
public class Tile {
//-----------------------------------------------------------------------------
	private static final char EMPTY_TILE = 'a';
	private static final char WATER_TILE = 'f';
	private static final char FIRE_TILE = 'g';
	private static final char TRAP_TILE = 'h';
	private static final char EXIT_TILE = 'i';
	Point p;
	char c;
//-----------------------------------------------------------------------------
	public Tile(Point p, char c){
		this.p = p;
		this.c = c;
	}
//-----------------------------------------------------------------------------
	public Tile(Point p){
		this.p = p;
	}
//-----------------------------------------------------------------------------
	public Tile(char c){
		this.c = c;
	}
//-----------------------------------------------------------------------------
	public Tile(){ }
//-----------------------------------------------------------------------------
	public Point getPoint(){
		return p;
	}
//-----------------------------------------------------------------------------
	public void setPoint(Point p){
		this.p = p;
	}
//-----------------------------------------------------------------------------
	public int getRow(){
		return p.y;
	}
//-----------------------------------------------------------------------------
	public int getColumn(){
		return p.x;
	}
//-----------------------------------------------------------------------------
	public int getX(){
		return (CollisionDetector.tilesToPixels(p.x));
	}
//-----------------------------------------------------------------------------
	public int getY(){
		return (CollisionDetector.tilesToPixels(p.y));
	}
//-----------------------------------------------------------------------------
	public char getChar(){
		return c;
	}
//-----------------------------------------------------------------------------
	public void setChar(char c){
		this.c = c;
	}
//-----------------------------------------------------------------------------
	public boolean killsLemming(){
		switch(c){
			case EMPTY_TILE:
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case EXIT_TILE: return false;
			case WATER_TILE:
			case FIRE_TILE:
			case TRAP_TILE:
			case 'j': return true;
			default: 
				System.out.printf("Error: invalid tile \n");
				return true;
		}
	}
//-----------------------------------------------------------------------------
	public boolean isWater(){
		if(c==WATER_TILE){
			return true;
		} else{
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public boolean isTrap(){
		if(c==TRAP_TILE){
			//DEBUG System.out.printf("Tile: isTrap() p.x = %d, p.y = %d  \n", p.x, p.y);
			return true;
		} else{
			return false;
		}
	}
//-----------------------------------------------------------------------------
	public boolean isExit(){
		if(c==EXIT_TILE){ 
			return true; 
		} else{ 
			return false; 
		}
	}
//-----------------------------------------------------------------------------
}
