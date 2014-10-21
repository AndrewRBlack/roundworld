package roundWorld.entity.enemy;

import java.awt.Rectangle;

import roundWorld.graphics.Screen;
import roundWorld.stage.Stage;

/**
 * An aggressive enemy who follows the player and makes attacks against him.
 * He is difficult to jump over. The player should block his attack and then
 * jump over him while he is recovering.
 * 
 * @author Andrew Aitken
 *
 */
public class Scorpion extends Enemy {

	public static final int SPRITE_WIDTH = 100, SPRITE_HEIGHT = 50, BASE_SPEED = 1;
	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Scorpion(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		BLUE_ROW_OFFSET = 2;
		
		name = "Scorpion";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 6;

		hitBoxWidth = 45;
		hitBoxHeight = 50;
		hitBoxOffsetX = 15;
		hitBoxOffsetY = 0;
	}
	
	/**
	 * Controls the Scorpion's atifical intelligence, including
	 * its movement, determining when it is close enough to the player
	 * to attack, and idling between an attack and walking again.
	 */
	@Override
	public void aiUpdate(double playerLocation) {
		switch (action) {
		case IDLE:
			state.lockReverse();
			if (state.getAnimationCount() > 132) {
				action = Action.WALK;
			}
			break;
		case WALK:
			turnAroundTime--;
			double distance = rotation / Stage.RADIAN_PER_PIXEL;

			while (distance < 0) {
				distance += 5024;
			}
			while (distance > 5024) {
				distance -= 5024;
			}

			if ((distance > 4964 && state.getDirectionState() == Direction.LEFT) ||
				(distance < 60 && state.getDirectionState() == Direction.RIGHT)) {
				
				action = Action.ATTACK;
				turnAroundTime = 300 + (int) (Math.random() * 10);
				break;
			}
			if (turnAroundTime <= 0) {
				if (distance > 250 && distance < 500 && state.getDirectionState() == Direction.LEFT) {
					state.moveRight();
					turnAroundTime = 300 + (int) (Math.random() * 10);
					break;
				}
				
				if (distance < 4774 && distance > 4524 && state.getDirectionState() == Direction.RIGHT) {
					state.moveLeft();
					turnAroundTime = 300 + (int) (Math.random() * 10);
					break;
				}
				
				if (distance > 500 && distance < 4524) {
					turnAroundTime = 300 + (int) (Math.random() * 20);
					state.switchDirection();
				}
			}
			
			move(state.getDirectionState());
			break;
		case ATTACK:
			state.lockReverse();
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

		columnRowType[2] = Screen.SCORPION;
		int framesPerSprite;
		
		switch (action) {
		case IDLE:
			columnRowType[1] = 1;
			if (animationCount < 120) {
				columnRowType[0] = 4;
			} else if (animationCount < 126) {
				columnRowType[0] = 3;
			} else {
				columnRowType[0] = 1;
			}
			break;
		case WALK:
			columnRowType[1] = 2;
			framesPerSprite = 10;
			if (animationCount < framesPerSprite) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 2) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 3) {
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 4) {
				columnRowType[0] = 4;
			} else {
				columnRowType[0] = 1;
				state.resetAnimationCount();
			}
			break;
		case ATTACK:
			columnRowType[1] = 1;
			framesPerSprite = 6;
			if (animationCount < framesPerSprite) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 2) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 3) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 4) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 5) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 6) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 7) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 8) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 9) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 10) {
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 12) {
				columnRowType[0] = 4;
			} else {
				action = Action.IDLE;
				columnRowType[0] = 4;
			}
			break;

		default:
			columnRowType[0] = 1;
			columnRowType[1] = 1;
		}
		

		return columnRowType;
	}
	
	/**
	 * Causes the Enemy to change its direction. Reverse is locked out for a
	 * short time after being called to prevent infinitely looping reversals
	 * between two enemies (hopefully)
	 */
	@Override
	public void reverse() {
		if (state.isReverseLocked()) {
			return;
		}

		turnAroundTime += 300 + (int)(Math.random() * 20);
		state.lockReverse();
		Direction direction = state.getDirectionState();
		if (direction == Direction.LEFT) {
			state.moveRight();
			for (int i = 0; i < 75; i++) move(state.getDirectionState());
			return;
		}

		if (direction == Direction.RIGHT) {
			state.moveLeft();
			for (int i = 0; i < 75; i++) move(state.getDirectionState());
			return;
		}
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
		case IDLE:
			if (getDirection() == Direction.LEFT) {
				hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - 50 - 34);
			}
		
			if (getDirection() == Direction.RIGHT) {
				hitBoxX = (int) (x - (spriteWidth / 2)) + 34;
			}
		
			hitBoxY = (int) (y - spriteHeight) + 17;
			hitBox = new Rectangle((int) hitBoxX, hitBoxY, 50, 34);
			return hitBox;
		case ATTACK:
			if (state.getAnimationCount() >= 6 * 11 && state.getAnimationCount() < 6*13) {
				if (getDirection() == Direction.LEFT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - 67 - 34);
				}
			
				if (getDirection() == Direction.RIGHT) {
					hitBoxX = (int) (x - (spriteWidth / 2)) + 34;
				}
			
				hitBoxY = (int) (y - spriteHeight) + 17;
				hitBox = new Rectangle((int) hitBoxX, hitBoxY, 67, 34);
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

}
