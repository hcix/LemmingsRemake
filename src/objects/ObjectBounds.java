package objects;

import java.awt.Rectangle;
//-----------------------------------------------------------------------------
/**
 * Bounding boxes representing the top, right, left, and bottom areas of a 
 * GameObject to be considered when performing collision detection tests.
 * Top and bottom bounds are tested for vertical collisions, and right and
 * left bounds are tested for horizontal collisions. The x and y values of
 * the bounding boxes are relative to the GameObject's x and y value.
 * Note: not all boxes exist for all GameObjects. Stationary GameObjects have
 * only a top box, with a width and height matching that of the GameObject.
 * Lemming's which have no possibility of horizontal collisions (typically
 * those with dx=0) do not need left an right bounds. Likewise Lemmings with 
 * no chance of colliding from the top have no top bounding boxes, and those
 * with no chance of collision from the bottom have no bottom bounding boxes.
 * When constructing a GameObject that does not have all 4 bounding boxes, use
 * either the constructor for stationary objects which assigns a width and height
 * to only the top box, or use the constructor with all 4 bounding box parameters
 * providing null as the argument for any unused bounding boxes.
 */
public class ObjectBounds {
//-----------------------------------------------------------------------------
	protected Rectangle top;
	protected Rectangle bottom;
	protected Rectangle left;
	protected Rectangle right;
//-----------------------------------------------------------------------------
	public ObjectBounds(){
		this.top = new Rectangle(0, 0);
		this.bottom = new Rectangle(0, 0);
		this.left = new Rectangle(0, 0);
		this.right = new Rectangle(0, 0);
	}
//-----------------------------------------------------------------------------
	/** 
	 * To be used by objects with a single bounding rectangle.
	 * @param width - the width of the bounding rectangle
	 * @param height - the height of the bounding rectangle
	 */
	public ObjectBounds(int width, int height){
		this();
		this.top = new Rectangle(width, height);
	}
//-----------------------------------------------------------------------------
	public ObjectBounds(Rectangle top, Rectangle left, Rectangle right, Rectangle bottom){
		this();
		if(top!=null){ this.top = top; }
		if(bottom!=null){ this.bottom = bottom; }
		if(left!=null){ this.left = left; }
		if(right!=null){ this.right = right; }
	}
//-----------------------------------------------------------------------------
	public int getHeight(){
		int floor = bottom.y + bottom.height;
		int height = floor - top.y;
		return height;
	}
//-----------------------------------------------------------------------------
	public int getWidth(){
		int width1 = Math.max(top.width, bottom.width);
		int width2 = left.width+right.width;
		return (Math.max(width1, width2));
	}
//-----------------------------------------------------------------------------
	public Rectangle getTop(float x, float y) {
		float topX = x + top.x;
		float topY = y + top.y;
		return (new Rectangle((int)topX, (int)topY, top.width, top.height));
	}
//-----------------------------------------------------------------------------
	public void setTop(Rectangle top) {
		this.top = top;
	}
//-----------------------------------------------------------------------------
	public Rectangle getBottom(float x, float y) {
		float bottomX = x + bottom.x;
		float bottomY = y + bottom.y;
		return (new Rectangle((int)bottomX, (int)bottomY, bottom.width, bottom.height));
	}
//-----------------------------------------------------------------------------
	public void setBottom(Rectangle bottom) {
		this.bottom = bottom;
	}
//-----------------------------------------------------------------------------
	public Rectangle getLeft(float x, float y) {
		float leftX = x + left.x;
		float leftY = y + left.y;
		return (new Rectangle((int)leftX, (int)leftY, left.width, left.height));
	}
//-----------------------------------------------------------------------------
	public void setLeft(Rectangle left) {
		this.left = left;
	}
//-----------------------------------------------------------------------------
	public Rectangle getRight(float x, float y) {
		float rightX = x + right.x;
		float rightY = y + right.y;
		return (new Rectangle((int)rightX, (int)rightY, right.width, right.height));
	}
//-----------------------------------------------------------------------------
	public void setRight(Rectangle right) {
		this.right = right;
	}
//-----------------------------------------------------------------------------
	public Rectangle[] getRects(){
		//Order is always top, left, right, bottom
		Rectangle[] rects = { top, left, right, bottom };
		return rects;
	}
//-----------------------------------------------------------------------------
}
