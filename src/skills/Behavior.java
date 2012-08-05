package skills;

import game.GameManager;

public interface Behavior {
	public static final int DIE_BEHAVIOR = 9;
	public static final int HOME_BEHAVIOR = 10;
	/** 
	 * Performs any updates required of the behavior 
	 */ 
	void update(GameManager gm, long elapsedTime);
	/**
	 * Returns the name of the behavior 
	 * @return the name of this behavior
	 */
	String getName();
	/** 
	 * Returns true if the behavior should be removed from the lemming it is
	 * attached to; otherwise returns false.
	 * @return whether or not the behavior should be removed from the lemming it is
	 * attached to
	 */
	boolean isDone();
}
