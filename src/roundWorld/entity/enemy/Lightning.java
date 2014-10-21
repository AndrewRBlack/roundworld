package roundWorld.entity.enemy;


import java.awt.Rectangle;

import roundWorld.graphics.Screen;

/**
 * The Lighting cloud is summoned by the Witch. It forms at the player's location
 * (at time of instantiation), and strikes the spot with a lightning bolt,
 * damaging the player if they're within its slim hitbox.
 * The attack is unblockable, and the cloud disappears after one attack.
 * 
 * @author Andrew Aitken
 *
 */
public class Lightning extends Enemy {
	
	public static final int SPRITE_WIDTH = 100, SPRITE_HEIGHT = 200, BASE_SPEED = 1;

	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Lightning(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		name = "Lightning";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 300;

		hitBoxWidth = 20;
		hitBoxHeight = 200;
		hitBoxOffsetX = 40;
		hitBoxOffsetY = 0;
		
		blockable = false;
		
		filter = NOFLASH;
		
	}
	
	/**
	 * The fireball only careens forward, its health decrementing every frame
	 * like a timer. Once it reaches zero, it destroys itself.
	 * @param playerLocation Unused.
	 */
	public void aiUpdate(double playerLocation) {
		state.lockReverse();
		switch (action) {
		case IDLE:
			if (state.getAnimationCount() > 3 * 12) {
				action = Action.ATTACK;
				state.resetAnimationCount();
			}
			break;
		case ATTACK:
			if (state.getAnimationCount() > 3 * 4) {
				action = Action.DYING;
				filter = HURT;
			}
			break;
		case DYING:
			break;
		default:
			action = Action.IDLE;
		}
	}
	
	/**
	 * Overridden by child classes to return sprite information
	 */
	public int[] getColumnRowType(int animationCount) {
		int framesPerSprite = 3;
		int columnRowType[] = new int[3];

		columnRowType[2] = Screen.LIGHTNING;
		
		
		switch (action) {
		case IDLE:
			if (state.getAnimationCount() < framesPerSprite) {
				columnRowType[1] = 1;
				columnRowType[0] = 1;
			} else if (state.getAnimationCount() < framesPerSprite * 2) {
				columnRowType[1] = 1;
				columnRowType[0] = 2;
			} else if (state.getAnimationCount() < framesPerSprite * 3) {
				columnRowType[1] = 1;
				columnRowType[0] = 3;
			} else if (state.getAnimationCount() < framesPerSprite * 4) {
				columnRowType[1] = 1;
				columnRowType[0] = 4;
			} else if (state.getAnimationCount() < framesPerSprite * 5) {
				columnRowType[1] = 2;
				columnRowType[0] = 1;
			} else if (state.getAnimationCount() < framesPerSprite * 6) {
				columnRowType[1] = 2;
				columnRowType[0] = 2;
			} else if (state.getAnimationCount() < framesPerSprite * 7) {
				columnRowType[1] = 2;
				columnRowType[0] = 3;
			} else if (state.getAnimationCount() < framesPerSprite * 8) {
				columnRowType[1] = 2;
				columnRowType[0] = 4;
			} else if (state.getAnimationCount() < framesPerSprite * 9) {
				columnRowType[1] = 3;
				columnRowType[0] = 1;
			} else if (state.getAnimationCount() < framesPerSprite * 10) {
				columnRowType[1] = 3;
				columnRowType[0] = 2;
			} else if (state.getAnimationCount() < framesPerSprite * 11) {
				columnRowType[1] = 3;
				columnRowType[0] = 3;
			} else {
				columnRowType[1] = 3;
				columnRowType[0] = 4;
			}
			break;
		case ATTACK:
			if (state.getAnimationCount() < framesPerSprite) {
				columnRowType[1] = 4;
				columnRowType[0] = 1;
			} else if (state.getAnimationCount() < framesPerSprite * 2) {
				columnRowType[1] = 4;
				columnRowType[0] = 2;
			} else if (state.getAnimationCount() < framesPerSprite * 3) {
				columnRowType[1] = 4;
				columnRowType[0] = 3;
			} else if (state.getAnimationCount() < framesPerSprite * 4) {
				columnRowType[1] = 4;
				columnRowType[0] = 4;
			} else {
				columnRowType[1] = 4;
				columnRowType[0] = 4;
			}
			break;
		default:
			columnRowType[1] = 1;
			columnRowType[0] = 4;
			break;
		}
		
		return columnRowType;

	}
	
	/**
	 * Calculates an Entities hit box and positions it correctly based on
	 * current variables
	 * 
	 * @return Rectangle to check for intersection with another entities hit box
	 */
	@Override
	public Rectangle getHitBox() {
		if (action != Action.ATTACK) {
			return new Rectangle((int) 0, 0, 0, 0);
		}
		
		int hitBoxX = 0;
		int hitBoxY = 0;
		Rectangle hitBox = null;
		
		if (getDirection() == Direction.LEFT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - hitBoxWidth - hitBoxOffsetX);
		}
		if (getDirection() == Direction.RIGHT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + hitBoxOffsetX;
		}

		hitBoxY = (int) (y - spriteHeight) + hitBoxOffsetY;
		hitBox = new Rectangle((int) hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);

		return hitBox;
	}
}
