package roundWorld.entity.enemy;

import java.awt.Rectangle;
import roundWorld.graphics.Screen;
import roundWorld.stage.Stage;

/**
 * A large enemy that lumbers slowly forward, but is unable to turn. When it
 * approaches the player, it smashes the ground, then stays still for about
 * a second. The player can use this time to jump on and over his back to get
 * on the other side of him.
 * 
 * @author Andrew Aitken
 *
 */
public class Golem extends Enemy {

	public static final int SPRITE_WIDTH = 124, SPRITE_HEIGHT = 168, BASE_SPEED = 1;
	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Golem(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		BLUE_ROW_OFFSET = 5;
		
		name = "Golem";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 12;

		hitBoxWidth = 53;
		hitBoxHeight = 145;
		hitBoxOffsetX = 18;
		hitBoxOffsetY = 23;
	}
	
	/**
	 * The Golem's AI to determine his next move. It determines its distance
	 * from the player. If the player is close enough, it will attack,
	 * otherwise it will step forward. The Golem is not able to turn around.
	 * 
	 * @param playerLocation The location of the player to determine current distance.
	 */
	@Override
	public void aiUpdate(double playerLocation) {
		double distance = rotation / Stage.RADIAN_PER_PIXEL;
		while (distance < 0) {
			distance += 5024;
		}
		while (distance > 5024) {
			distance -= 5024;
		}
		state.lockReverse();
		switch (action) {
		case IDLE:

			if ((distance > 4924 && state.getDirectionState() == Direction.LEFT) ||
					(distance < 100 && state.getDirectionState() == Direction.RIGHT)) {
					state.resetAnimationCount();
					action = Action.ATTACK;
					break;
			}
			if (state.getAnimationCount() > 60 && action == Action.IDLE) {
				action = Action.WALK;
				state.resetAnimationCount();
			}
			break;
		case WALK:
			move(state.getDirectionState());
			break;
		case ATTACK:
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * Uses information from the state class to animate between sprites and load
	 * necessary values into an array which will be sent back to the parents
	 * render method
	 * 
	 * @return an Array containing information needed to load the correct sprite
	 */
	@Override
	public int[] getColumnRowType(int animationCount) {
		int columnRowType[] = new int[3];

		columnRowType[2] = Screen.GOLEM;
		int framesPerSprite;
		
		switch (action) {
		case IDLE:
			columnRowType[1] = 1;
			columnRowType[0] = 1;
			break;
		case WALK:
			framesPerSprite = 10;
			if (animationCount < framesPerSprite) {
				columnRowType[1] = 1;
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 2) {
				columnRowType[1] = 1;
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 3) {
				columnRowType[1] = 1;
				columnRowType[0] = 4;
			} else if (animationCount < framesPerSprite * 4) {
				columnRowType[1] = 2;
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 5) {
				columnRowType[1] = 2;
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 6) {
				columnRowType[1] = 2;
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 7) {
				columnRowType[1] = 2;
				columnRowType[0] = 4;
			} else {
				columnRowType[1] = 1;
				columnRowType[0] = 1;
				state.resetAnimationCount();
				action = Action.IDLE;
			}
			break;
		case ATTACK:
			framesPerSprite = 6;

			if (animationCount < framesPerSprite) {
				columnRowType[0] = 1;
				columnRowType[1] = 3;
			} else if (animationCount < framesPerSprite * 2) {
				columnRowType[0] = 2;
				columnRowType[1] = 3;
			} else if (animationCount < framesPerSprite * 3) {
				columnRowType[0] = 3;
				columnRowType[1] = 3;
			} else if (animationCount < framesPerSprite * 4) {
				columnRowType[0] = 4;
				columnRowType[1] = 3;
			} else if (animationCount < framesPerSprite * 5) {
				columnRowType[0] = 1;
				columnRowType[1] = 4;
			} else if (animationCount < framesPerSprite * 6) {
				//Hitbox 1
				blockable = false;
				columnRowType[0] = 2;
				columnRowType[1] = 4;
			} else if (animationCount < framesPerSprite * 7) {
				//Hitbox 2
				columnRowType[0] = 3;
				columnRowType[1] = 4;
			} else if (animationCount < framesPerSprite * 17) {
				//Hitbox 2
				blockable = true;
				columnRowType[0] = 4;
				columnRowType[1] = 4;
			} else if (animationCount < framesPerSprite * 18) {
				//Hitbox 2
				columnRowType[0] = 1;
				columnRowType[1] = 5;
			} else if (animationCount < framesPerSprite * 19) {
				//Hitbox 1
				columnRowType[0] = 2;
				columnRowType[1] = 5;
			} else if (animationCount < framesPerSprite * 20) {
				columnRowType[0] = 3;
				columnRowType[1] = 5;
			} else {
				columnRowType[0] = 4;
				columnRowType[1] = 5;
				state.resetAnimationCount();
				action = Action.IDLE;
			}
			
			break;

		default:
			columnRowType[0] = 1;
			columnRowType[1] = 1;
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
		if (action == Action.DYING) {
			return new Rectangle((int) 0, 0, 0, 0);
		}
		int hitBoxX = 0;
		int hitBoxY = 0;
		Rectangle hitBox = null;
		switch (action) {
		case ATTACK:
			//Attack hitbox 1
			if ((state.getAnimationCount() >= 6 * 5 && state.getAnimationCount() < 6 * 6) ||
				(state.getAnimationCount() >= 6 * 18 && state.getAnimationCount() < 6 * 19)) {
				if (getDirection() == Direction.LEFT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - 115 - 5);
				}
			
				if (getDirection() == Direction.RIGHT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + 5;
				}
			
				hitBoxY = (int) (y - spriteHeight) + 48;
				hitBox = new Rectangle((int) hitBoxX, hitBoxY, 115, 120);
				return hitBox;
			}
			
			//Attack hitbox2
			if (state.getAnimationCount() >= 6 * 6 && state.getAnimationCount() < 6 * 18) {
				if (getDirection() == Direction.LEFT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - 104 - 15);
				}
			
				if (getDirection() == Direction.RIGHT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + 15;
				}
			
				hitBoxY = (int) (y - spriteHeight) + 111;
				hitBox = new Rectangle((int) hitBoxX, hitBoxY, 104, 57);
				return hitBox;
			}
		default:
			if (getDirection() == Direction.LEFT) {
				hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - hitBoxWidth - hitBoxOffsetX);
			}

			if (getDirection() == Direction.RIGHT) {
				hitBoxX = (int) (x - (spriteWidth / 2)) + hitBoxOffsetX;
			}

			hitBoxY = (int) (y - spriteHeight) + hitBoxOffsetY;
			hitBox = new Rectangle((int) hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);
			break;
		}
		

		return hitBox;
	}
	
	/**
	 * Returns whether or not the enemy is threatening the player.
	 * That if, is by touching the enemy the player will be damaged.
	 * Generally always true. Should be overwritten by exceptional
	 * circumstances.
	 * @return Whether the player is damaged on contact.
	 */
	@Override
	public boolean isThreatening() {
		if (state.getAnimationCount() >= 6 * 8 && state.getAnimationCount() < 6 * 17) {
			return false;
		}
		
		return true;
	}

}
