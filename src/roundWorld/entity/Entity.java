package roundWorld.entity;

import java.awt.Rectangle;
import roundWorld.stage.Stage;

/**
 * Super class for the Player and all enemies. Handles movement, hit boxes, and
 * declares all common variables
 * 
 * @author Andrew Black
 * 
 */
public class Entity {
	/**
	 * The directions that an Entity can move
	 */
	public static enum Direction {
		LEFT, RIGHT
	};

	/**
	 * All actions that can be performed by an Entity
	 */
	public static enum Action {
		IDLE, WALK, ATTACK, ROLL, JUMP, BLOCK, DAMAGED_FRONT, DAMAGED_BACK, VICTORY, DYING
	};

	/**
	 * The various speeds that an Entity can move at. Default is no speed
	 */
	public static enum Speed {
		STILL, SLOW, FAST
	};

	/**
	 * The rotation for determining how sprites should be rendered, and X and Y
	 * coordinate system for hit box placement
	 */
	protected double rotation, x, y;
	/**
	 * Value that is incremented when an Entity is in a jumping state,
	 * eventually overcoming the jump force and pulling it to the ground
	 */
	protected double gravityAccel;
	/**
	 * Variables assigned by each Entity to uniquely identify the properties of
	 * their hit boxes
	 */
	protected int hitBoxWidth, hitBoxHeight, spriteWidth, spriteHeight, hitBoxOffsetX, hitBoxOffsetY;
	/**
	 * Variable assigned by each entity to determine movement speed
	 */
	protected double speed;
	
	/**
	 * Generic action object.
	 */
	protected Action action;

	/**
	 * Constructor initializes common variables, and calculates coordinates
	 */
	public Entity(Direction direction, Action inAction, int position) {
		rotation = -Stage.RADIAN_PER_PIXEL * position;
		x = position;
		
		y = 0;
		
		action = inAction;
		gravityAccel = 0;
	}

	/**
	 * Called by an Entity to move it in a given direction. Amount of movement
	 * is based on the current speed.
	 * 
	 * @param direction
	 *            The direction to move
	 */
	protected void move(Direction direction) {
		if (direction == Direction.LEFT) {
			rotation += Stage.RADIAN_PER_PIXEL * speed;
			x -= Stage.UNIT_PER_PIXEL * speed;
			if (x < 0) {
				x += Stage.CIRCUMFERENCE;
			}

			return;
		}

		if (direction == Direction.RIGHT) {
			rotation -= Stage.RADIAN_PER_PIXEL * speed;

			x += Stage.UNIT_PER_PIXEL * speed;
			if (x > Stage.CIRCUMFERENCE) {
				x -= Stage.CIRCUMFERENCE;
			}

			return;
		}
	}

	/**
	 * Increments and applies gravity acceleration to an entity that is jumping.
	 * Value is reset after it has landed
	 */
	protected void gravity() {
		if (y >= 0) {
			gravityAccel = 0;
			y = 0;
			return;
		}

		gravityAccel += .5;
		y += gravityAccel;
	}

	/**
	 * Calculates an Entities hit box and positions it correctly based on
	 * current variables
	 * 
	 * @return Rectangle to check for intersection with another entities hit box
	 */
	public Rectangle getHitBox() {
		if (action == Action.DYING) {
			return new Rectangle((int) 0, 0, 0, 0);
		}
		int hitBoxX = 0;

		if (getDirection() == Direction.LEFT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - hitBoxWidth - hitBoxOffsetX);
		}

		if (getDirection() == Direction.RIGHT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + hitBoxOffsetX;
		}

		int hitBoxY = (int) (y - spriteHeight) + hitBoxOffsetY;

		Rectangle hitBox = new Rectangle((int) hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);
		return hitBox;
	}

	/**
	 * Overridden by child classes to return their direction
	 */
	protected Direction getDirection() {
		return null;
	}

}