package roundWorld.entity.enemy;

import roundWorld.entity.Entity.Direction;

/**
 * Manages the various states that an Enemy can be in. Tracks counters and
 * returns their values for use in other classes
 * 
 * @author Andrew Black
 * 
 */
public class EnemyStates {
	
	/**
	 * For keeping track of the direction that the Enemy is facing
	 */
	private Direction directionState;
	/**
	 * Counters for tracking which sprite to load, and when Enemy can reverse
	 */
	private int animationCount, reverseLock;

	/**
	 * Instantiates variables to starting values
	 * 
	 * @param inDirectionState
	 *            Passed a direction value to set the initial direction to face
	 */
	public EnemyStates(Direction inDirectionState) {
		directionState = inDirectionState;
		animationCount = 0;
		reverseLock = 0;
	}

	/**
	 * @return The current direction that Enemy is facing
	 */
	public Direction getDirectionState() {
		return directionState;
	}

	/**
	 * Used by the parent class for determining which sprite to load
	 * 
	 * @return a numeric counter for comparison
	 */
	public int getAnimationCount() {
		return animationCount;
	}

	/**
	 * Increments the animation count by 1
	 */
	public void incrementAnimationCount() {
		animationCount++;
	}

	/**
	 * Resets the animation count to 0
	 */
	public void resetAnimationCount() {
		animationCount = 0;
	}

	/**
	 * Unconditionally changes the current direction to Left
	 */
	public void moveLeft() {
		directionState = Direction.LEFT;
	}

	/**
	 * Unconditionally changes the current direction to Right
	 */
	public void moveRight() {
		directionState = Direction.RIGHT;
	}
	
	/**
	 * Unconditionally switches the current direction.
	 */
	public void switchDirection() {
		if (directionState == Direction.LEFT) {
			directionState = Direction.RIGHT;
		} else {
			directionState = Direction.LEFT;
		}
	}

	/**
	 * Adds a positive value to reverseLock
	 */
	public void lockReverse() {
		reverseLock = 5;
	}

	/**
	 * Decrements the value of reverseLock by 1. Called during each update to
	 * act as a timeout. If the number becomes negative, it will be reset to 0
	 */
	public void decrementReverseLock() {
		reverseLock--;
		if (reverseLock < 0) {
			reverseLock = 0;
		}
	}

	/**
	 * If reverseLock has a positive value then this will return true. A locked
	 * reverse method ensures that the Enemy will not be able to change
	 * direction after collisions. This can prevent two enemies from getting
	 * stuck inside each other as they reverse infinitely
	 * 
	 * @return A boolean indication if the reverse method is locked
	 */
	public boolean isReverseLocked() {
		if (reverseLock == 0) {
			return false;
		}
		return true;
	}
	


}