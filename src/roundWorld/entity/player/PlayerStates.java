package roundWorld.entity.player;

import roundWorld.entity.Entity.Action;
import roundWorld.entity.Entity.Direction;
import roundWorld.entity.Entity.Speed;

/**
 * A state machine that handles all possible actions performed by the Player
 * class. This class must be utilized for Player to do anything. It also
 * contains counter variables and their applicable getters/setters
 * 
 * @author Andrew Black, Andrew Aitken
 * 
 */
public class PlayerStates {
	/**
	 * For keeping track of the direction that the Player is facing
	 */
	private Direction directionState;
	/**
	 * Holds the current Action state for the Player
	 */
	private Action actionState;
	/**
	 * Flags that determine if directions are in queue or locked, and whether or
	 * not an attack combo will be performed
	 */
	private boolean holdingLeft, holdingRight, lockedLeft, lockedRight, holdingBlock, attackCombo;
	/**
	 * Keeps track of the speed that Player will move at while jumping/rolling
	 */
	private Speed jumpSpeedState, rollSpeedState;
	/**
	 * Counters that ensure actions such as the rolling are finite
	 */
	private int animationCount, rollStateCount, attackStateCount, damageImmunity;

	/**
	 * Constructor simply initializes variables to starting values. Action and
	 * Direction state are set based on arguments
	 * 
	 * @param inDirectionState
	 *            What direction will the player start off facing?
	 * @param inActionState
	 *            What action will the Player start off doing?
	 */
	public PlayerStates(Direction inDirectionState, Action inActionState) {
		directionState = inDirectionState;
		actionState = inActionState;

		holdingLeft = false;
		holdingRight = false;
		lockedLeft = false;
		lockedRight = false;
		attackCombo = false;

		jumpSpeedState = null;
		rollSpeedState = null;

		animationCount = 0;
		rollStateCount = 0;
		attackStateCount = 0;
		damageImmunity = 0;
	}

	/**
	 * Private convenience setter for resetting the animation count and changing
	 * Action state at the same time (usually desired)
	 * 
	 * @param newState
	 *            The new current action state
	 */
	private void setActionState(Action newState) {
		actionState = newState;
		animationCount = 0;
	}

	/**
	 * @return The current direction that Player is facing
	 */
	public Direction getDirectionState() {
		if (directionState == Direction.LEFT) {
			if (lockedRight) {
				return Direction.RIGHT;
			}
			return Direction.LEFT;
		} else {
			if (lockedLeft) {
				return Direction.LEFT;
			}
			return Direction.RIGHT;
		}
	}

	/**
	 * Returns true if the Player is currently immune to damage. This is called
	 * whenever attacked, so that Player cannot be attacked multiple times due
	 * to collision anomalies
	 * 
	 * @return Is the player currently immune to damage?
	 */
	public boolean isImmune() {
		if (damageImmunity == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Called with each subsequent update after receiving damage, so that the
	 * immunity can run out
	 */
	public void decrementImmunity() {
		damageImmunity--;
	}



	/**
	 * @return The current action state that the Player is set to
	 */
	public Action getActionState() {
		return actionState;
	}

	/**
	 * JumpSpeedState tells the Player class how quickly to move while in air
	 * 
	 * @return The current jump speed
	 */
	public Speed getJumpSpeedState() {
		return jumpSpeedState;
	}

	/**
	 * RollSpeedState will tell Player how fast to move while rolling
	 * 
	 * @return The current roll speed
	 */
	public Speed getRollSpeedState() {
		return rollSpeedState;
	}

	/**
	 * Will determine the current direction, and set its locked flag
	 */
	private void lockDirection() {
		if (directionState == Direction.LEFT) {
			lockedLeft = true;
		}

		if (directionState == Direction.RIGHT) {
			lockedRight = true;
		}
	}

	/**
	 * Releases any direction locks and returns the Player to the appropriate
	 * grounded action after landing from a jump or attack
	 */
	public void land() {
		lockedLeft = false;
		lockedRight = false;

		if (holdingLeft || holdingRight) {
			setActionState(Action.WALK);
			return;
		}

		setActionState(Action.IDLE);
	}

	/**
	 * Counts down the roll state with each call. If the count reaches 0 then
	 * any locks are released, and the Player is returned to a walking state if
	 * they are holding left/right, otherwise idle
	 */
	public void decrementRollStateCount() {
		rollStateCount--;

		if (rollStateCount == 0) {
			lockedLeft = false;
			lockedRight = false;
			
			if (holdingBlock) {
				setActionState(Action.BLOCK);
				return;
			}

			if (holdingLeft || holdingRight) {
				setActionState(Action.WALK);
				return;
			}

			setActionState(Action.IDLE);
		}
	}

	/**
	 * Counts down the attack state with each call. If the count reaches 0 then
	 * any locks are released, and the Player is returned to a walking state if
	 * they are holding left/right, otherwise idle. If the attackCombo flag is
	 * set, then the attack count is reset, and the Player continues attacking
	 */
	public void decrementAttackStateCount() {
		attackStateCount--;

		if (attackStateCount == 0) {
			if (attackCombo) {
				attackStateCount = 24;
				attackCombo = false;
				return;
			}

			lockedLeft = false;
			lockedRight = false;

			if (holdingBlock) {
				setActionState(Action.BLOCK);
				return;
			}
			if (holdingLeft || holdingRight) {
				setActionState(Action.WALK);
				return;
			}
			
			setActionState(Action.IDLE);
		}
	}

	/**
	 * Gets the animation count which is used for deciding which sprite to
	 * render. Player contains the code necessary for comparing this number, and
	 * making that decision
	 * 
	 * @return the numeric animation count
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
	 * Resets the animation count to 0. Generally used for looping back to the
	 * first frame when an animation has ended
	 */
	public void resetAnimationCount() {
		animationCount = 0;
	}

	/**
	 * Unconditionally sets the holdingLeft flag, and changes the direction
	 * state. Uses a switch to determine what effect it will have on the Player
	 */
	public void moveLeft() {
		switch (actionState) {
		case IDLE:
			setActionState(Action.WALK);
			break;
		case JUMP:
			if (jumpSpeedState == Speed.STILL) {
				jumpSpeedState = Speed.FAST;
				break;
			}

			if (directionState == Direction.RIGHT) {
				jumpSpeedState = Speed.SLOW;
				break;
			}

			jumpSpeedState = Speed.FAST;
			break;
		case ROLL:
			if (lockedLeft) {
				rollSpeedState = Speed.FAST;
				break;
			}

			if (lockedRight) {
				rollSpeedState = Speed.SLOW;
				break;
			}
			break;
		default:
			break;
		}

		holdingLeft = true;
		directionState = Direction.LEFT;
	}

	/**
	 * Unconditionally sets the holdingRight flag, and changes the direction
	 * state. Uses a switch to determine what effect it will have on the Player
	 */
	public void moveRight() {
		switch (actionState) {
		case IDLE:
			setActionState(Action.WALK);
			break;
		case JUMP:
			if (jumpSpeedState == Speed.STILL) {
				jumpSpeedState = Speed.FAST;
				break;
			}

			if (directionState == Direction.LEFT) {
				jumpSpeedState = Speed.SLOW;
				break;
			}

			jumpSpeedState = Speed.FAST;
			break;
		case ROLL:
			if (lockedRight) {
				rollSpeedState = Speed.FAST;
				break;
			}

			if (lockedLeft) {
				rollSpeedState = Speed.SLOW;
				break;
			}
			break;
		default:
			break;
		}

		holdingRight = true;
		directionState = Direction.RIGHT;
	}

	/**
	 * If standing idle or walking it causes the player to enter the jump state
	 * at a speed dependent on current action
	 */
	public void jump() {
		switch (actionState) {
		case IDLE:
			setActionState(Action.JUMP);
			jumpSpeedState = Speed.STILL;
			break;
		case WALK:
			setActionState(Action.JUMP);
			jumpSpeedState = Speed.FAST;
			break;
		default:
			break;
		}
	}

	/**
	 * Begins a fine roll state if the player is idle or walking. The counter is
	 * set and speed determined by current action
	 */
	public void roll() {
		switch (actionState) {
		case IDLE:
			lockDirection();
			setActionState(Action.ROLL);
			rollSpeedState = Speed.SLOW;
			rollStateCount = 60;
			break;
		case WALK:
			lockDirection();
			setActionState(Action.ROLL);
			rollSpeedState = Speed.FAST;
			rollStateCount = 60;
			break;
		default:
			break;
		}
	}

	/**
	 * Puts the Player into the attack state, or sets the attackCombo flag if
	 * already attacking
	 */
	public void attack() {
		switch (actionState) {
		case IDLE:
		case WALK:
			lockDirection();
			setActionState(Action.ATTACK);
			attackStateCount = 24;
			break;
		case ATTACK:
			attackCombo = true;
			break;
		default:
			break;
		}
	}

	/**
	 * Causes the player to enter the block state if idle or walking. State does
	 * not end until released, or forced out of it
	 */
	public void block() {
		switch (actionState) {
		case IDLE:
		case WALK:
			lockDirection();
			setActionState(Action.BLOCK);
			break;
		default:
			break;
		}
		holdingBlock = true;
	}

	/**
	 * Sets the holdingLeft flag to false and checks other variables to
	 * determine action after left direction state has ended. This in
	 * conjunction with the flags allows directions to be queued
	 */
	public void releaseLeft() {
		switch (actionState) {
		case WALK:
			if (!holdingRight) {
				setActionState(Action.IDLE);
			}
			break;
		case JUMP:
			if (directionState == Direction.LEFT) {
				jumpSpeedState = Speed.SLOW;
			}
			break;
		case ROLL:
			if (directionState == Direction.LEFT) {
				rollSpeedState = Speed.SLOW;
			}
			break;
		default:
			break;
		}

		if (holdingRight) {
			directionState = Direction.RIGHT;
		}
		holdingLeft = false;
	}

	/**
	 * Sets the holdingRight flag to false and checks other variables to
	 * determine action after right direction state has ended. This in
	 * conjunction with the flags allows directions to be queued
	 */
	public void releaseRight() {
		switch (actionState) {
		case WALK:
			if (!holdingLeft) {
				setActionState(Action.IDLE);
			}
			break;
		case JUMP:
			if (directionState == Direction.RIGHT) {
				jumpSpeedState = Speed.SLOW;
			}
			break;
		case ROLL:
			if (directionState == Direction.RIGHT) {
				rollSpeedState = Speed.SLOW;
			}
			break;
		default:
			break;
		}

		if (holdingLeft) {
			directionState = Direction.LEFT;
		}
		holdingRight = false;
	}

	/**
	 * Ends the blocking state and returns Player to appropriate action
	 */
	public void releaseBlock() {
		switch (actionState) {
		case BLOCK:
			lockedLeft = false;
			lockedRight = false;
			if (holdingLeft || holdingRight) {
				setActionState(Action.WALK);
				holdingBlock = false;
				return;
			}

			setActionState(Action.IDLE);
			break;
		default:
			break;
		}
		holdingBlock = false;
	}

	/**
	 * Called if damage has been received from a coordinate in front of the
	 * Player. This triggers a unique animation in render, causes a knock back
	 * effect, and sets the immunity counter
	 */
	public void damagedFront() {
		lockDirection();
		damageImmunity = 40;
		setActionState(Action.DAMAGED_FRONT);
	}

	/**
	 * Called if damage has been received from a coordinate behind the Player.
	 * This triggers a unique animation in render, causes a knock back effect,
	 * and sets the immunity counter
	 */
	public void damagedBack() {
		lockDirection();
		damageImmunity = 40;
		setActionState(Action.DAMAGED_BACK);
	}
	
	/**
	 * Called after the last enemy has been defeated. This sets the player
	 * state to his victory state.
	 */
	public void victoryState() {
		setActionState(Action.VICTORY);
	}
	
	/**
	 * Forces the player into the IDLE action state and falsifies all booleans.
	 * Fixes a bug in Stage 5 where reenabling input after the victory stance
	 * can result in movement or other actions without any keys being pressed.
	 */
	public void idle() {
		setActionState(Action.IDLE);
		holdingLeft = false;
		holdingRight = false;
		holdingBlock = false;
		lockedLeft = false;
		lockedRight = false;
		attackCombo = false;
	}
	
}