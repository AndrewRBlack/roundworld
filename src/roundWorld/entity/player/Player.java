package roundWorld.entity.player;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import roundWorld.entity.Entity;
import roundWorld.entity.enemy.Enemy;
import roundWorld.graphics.Screen;

/**
 * Contains all actions, manages input and updates animations required for the
 * Player Entity during game play
 * 
 * @author Andrew Black, Andrew Aitken
 * 
 */
public class Player extends Entity {
	/**
	 * Constant values for the width/height of sprites, and default speed
	 */
	public static final int SPRITE_WIDTH = 200, SPRITE_HEIGHT = 135, BASE_SPEED = 5;

	/**
	 * For managing and limiting player actions and animations
	 */
	private PlayerStates state;
	/**
	 * For tracking players current health numerically
	 */
	private int health;
	/**
	 * For preventing player input when not desired
	 */
	private boolean isAcceptingInput;
	/**
	 * To determine the direction that player will be launched when damaged
	 */
	private Direction damageDirection;
	/**
	 * Temporary immunity granted when a blockable attack is blocked.
	 * This fixes a bug with outofbound boxes, where the engine believes
	 * the player is being attacked from both sides simultaneously.
	 */
	private boolean immune;
	/**
	 * Mainly for debugging (and cheating) purposes.
	 * If activated on the title screen, the player never takes health damage.
	 */
	private boolean invincible;

	/**
	 * Constructor initializes state machine and sets variables to defaults
	 * 
	 * @param direction
	 *            Direction that player will face on game start
	 * @param action
	 *            Action to be performed at game start. Default is idle
	 * @param position
	 *            Location for player to stand at run time. Default is zero
	 */
	public Player(Direction direction, Action action, int position, boolean isCheating) {
		super(direction, action, position);
		state = new PlayerStates(direction, action);
		health = 5;
		isAcceptingInput = false;
		damageDirection = null;

		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;

		regularHitBox();
		
		invincible = isCheating;
	}

	/**
	 * Called by Level class to determine what the Player is doing
	 * 
	 * @return Action variable
	 */
	public Action getActionState() {
		return state.getActionState();
	}

	/**
	 * Called by the level to use as reference for the stage and enemies, so
	 * they can be rotated correctly
	 * 
	 * @return The radian value of Player rotation as reference
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Used by the screen class to determine which health bar sprite to draw
	 * 
	 * @return Numeric value of Player's current health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Increases the player's health, up to a maximum of five.
	 * @param amount the amount of health to increase the player's health by.
	 */
	public void increaseHealth(int amount) {
		if (amount < 0) {
			return;
		}
		health += amount;
		if (health > 5) {
			health = 5;
		}
	}

	/**
	 * Called by Level during game play, so player can respond to key events
	 */
	public void enableInput() {
		isAcceptingInput = true;
		state.idle();
	}
	
	/**
	 * Disables player input.
	 */
	public void disableInput() {
		isAcceptingInput = false;
		state.idle();
	}

	/**
	 * Builds a hit box to represent the sword attack area. Used to check
	 * intersection with enemies so they can be defeated
	 * 
	 * @return Rectangle object using same coordinate system as other hit boxes
	 */
	public Rectangle getAttackBox() {
		int animationCount = state.getAnimationCount();
		if (animationCount < 6 ||
			(animationCount >= 6*3 && animationCount < 6*5) ||
			animationCount >= 6*7) {
			return new Rectangle(0, 0, 0, 0);
		}
		int attackOffsetX = 118;
		int attackOffsetY = 48;
		int attackWidth = 80;
		int attackHeight = 88;
		int hitBoxX = 0;

		if (state.getDirectionState() == Direction.LEFT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + (spriteWidth - attackWidth - attackOffsetX);
		}

		if (state.getDirectionState() == Direction.RIGHT) {
			hitBoxX = (int) (x - (spriteWidth / 2)) + attackOffsetX;
		}

		int hitBoxY = (int) (y - spriteHeight) + attackOffsetY;

		return new Rectangle((int) hitBoxX, hitBoxY, attackWidth, attackHeight);
	}

	/**
	 * Called after each update by the Level class to check if the player died
	 * 
	 * @return a boolean containing the potentially tragic news
	 */
	public boolean isDead() {
		if (health != 0) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the Player's hit box dimensions to account for the reduced height
	 * while rolling
	 */
	private void rollingHitBox() {
		hitBoxWidth = 50;
		hitBoxHeight = 32;
		hitBoxOffsetX = 90;
		hitBoxOffsetY = 104;
	}

	/**
	 * Sets or resets the hit box dimensions to their regular values
	 */
	private void regularHitBox() {
		hitBoxWidth = 26;
		hitBoxHeight = 76;
		hitBoxOffsetX = 85;
		hitBoxOffsetY = 59;
	}

	/**
	 * Called by the Level Class. This uses values held in the state object to
	 * determine what actions the player will perform
	 */
	public void update() {
		immune = false;
		gravity();
		regularHitBox();
		speed = BASE_SPEED;
		Direction direction = state.getDirectionState();

		switch (state.getActionState()) {
		case WALK:
			move(direction);
			break;
		case JUMP:
			Speed jumpSpeedState = state.getJumpSpeedState();

			if (jumpSpeedState == Speed.SLOW) {
				speed = speed / 2;
				move(direction);
			}

			if (jumpSpeedState == Speed.FAST) {
				move(direction);
			}

			y -= 9;
			if (y >= 0) {
				state.land();
			}
			break;
		case ROLL:
			rollingHitBox();
			Speed rollSpeedState = state.getRollSpeedState();
			if (rollSpeedState == Speed.SLOW) {
				speed = speed / 2;
				move(direction);
			}

			if (rollSpeedState == Speed.FAST) {
				speed = speed * 1.5;
				move(direction);
			}

			state.decrementRollStateCount();
			break;
		case ATTACK:
			state.decrementAttackStateCount();
			break;
		case DAMAGED_FRONT:
		case DAMAGED_BACK:
			state.decrementImmunity();
			if (health == 0) {
				return;
			}
			y -= 5;
			move(damageDirection);

			if (!state.isImmune() ) {
				state.land();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Called by the Level class. The values within the state object determine
	 * which sprite to draw for this update and where to draw it on the Y axis.
	 * Animation is simulated by an incrementing counter called by conditionals
	 * 
	 * @param screen
	 *            The Screen object for sending the values for the determined
	 *            sprite and position to be drawn
	 */
	public void render(Screen screen) {
		int animationCount = state.getAnimationCount();
		state.incrementAnimationCount();

		int direction = 0;
		if (state.getDirectionState() == Direction.LEFT) {
			direction = Screen.LEFT;
		}

		if (state.getDirectionState() == Direction.RIGHT) {
			direction = Screen.RIGHT;
		}

		int framesPerSprite;
		int column = 0;
		int row = 0;

		switch (state.getActionState()) {
		case IDLE:
			framesPerSprite = 30;
			row = 1;

			if (animationCount < framesPerSprite) {
				column = 1;
			} else if (animationCount < framesPerSprite * 2) {
				column = 2;
			} else if (animationCount < framesPerSprite * 3) {
				column = 3;
			} else if (animationCount < framesPerSprite * 4) {
				column = 2;
			} else {
				column = 1;
				state.resetAnimationCount();
			}
			break;
		case WALK:
			framesPerSprite = 10;
			row = 2;

			if (animationCount < framesPerSprite) {
				column = 1;
			} else if (animationCount < framesPerSprite * 2) {
				column = 2;
			} else if (animationCount < framesPerSprite * 3) {
				column = 3;
			} else if (animationCount < framesPerSprite * 4) {
				column = 4;
			} else if (animationCount < framesPerSprite * 5) {
				column = 5;
			} else if (animationCount < framesPerSprite * 6) {
				column = 6;
			} else {
				column = 1;
				state.resetAnimationCount();
			}
			break;
		case JUMP:
			column = 6;
			row = 3;
			break;
		case ROLL:
			framesPerSprite = 10;
			row = 5;

			if (animationCount < framesPerSprite) {
				column = 1;
			} else if (animationCount < framesPerSprite * 2) {
				column = 2;
			} else if (animationCount < framesPerSprite * 3) {
				column = 3;
			} else if (animationCount < framesPerSprite * 4) {
				column = 4;
			} else if (animationCount < framesPerSprite * 5) {
				column = 5;
			} else {
				column = 6;
			}
			break;
		case ATTACK:
			framesPerSprite = 6;

			if (animationCount < framesPerSprite) {
				column = 1;
				row = 3;
			} else if (animationCount < framesPerSprite * 2) {
				column = 2;
				row = 3;
			} else if (animationCount < framesPerSprite * 3) {
				column = 3;
				row = 3;
			} else if (animationCount < framesPerSprite * 4) {
				column = 4;
				row = 3;
			} else if (animationCount < framesPerSprite * 5) {
				column = 1;
				row = 4;
			} else if (animationCount < framesPerSprite * 6) {
				column = 2;
				row = 4;
			} else if (animationCount < framesPerSprite * 7) {
				column = 3;
				row = 4;
			} else if (animationCount < framesPerSprite * 8) {
				column = 4;
				row = 4;
			} else {
				column = 1;
				row = 3;
				state.resetAnimationCount();
			}
			break;
		case BLOCK:
			row = 3;
			column = 5;
			break;
		case DAMAGED_FRONT:
			row = 1;
			column = 4;
			break;
		case DAMAGED_BACK:
			row = 1;
			column = 5;
			break;
		case VICTORY:
			framesPerSprite = 10;
			row = 6;
			
			if (animationCount < framesPerSprite) {
				column = 1;
			} else if (animationCount < framesPerSprite * 2) {
				column = 2;
			} else if (animationCount < framesPerSprite * 3) {
				column = 3;
			} else if (animationCount < framesPerSprite * 4) {
				column = 2;
			} else if (animationCount < framesPerSprite * 5) {
				column = 3;
			} else if (animationCount < framesPerSprite * 6) {
				column = 2;
			} else if (animationCount < framesPerSprite * 7) {
				column = 3;
			} else if (animationCount < framesPerSprite * 8) {
				column = 2;
			} else if (animationCount < framesPerSprite * 9) {
				column = 4;
			} else {
				column = 5;
				screen.drawBeams(animationCount);
			}
		default:
			break;
		}

		screen.drawSprite(Screen.PLAYER, direction, column, row, y, 0);
	}
	
	/**
	 * A second render used when the player is defeated.
	 * The filter matches the Level's timer; as it counts, so does the
	 * player fade from view. The actual alpha value used is twice the
	 * value of filter.
	 * 
	 * @param screen  
	 * 			  The Screen object for sending the values for the determined
	 *            sprite and position to be drawn
	 * @param filter The filter to be used for the alpha fading.
	 */
	public void render(Screen screen, int filter) {
		if (filter == 0) {
			return;
		}
		
		int direction = 0;
		if (state.getDirectionState() == Direction.LEFT) {
			direction = Screen.LEFT;
		}

		if (state.getDirectionState() == Direction.RIGHT) {
			direction = Screen.RIGHT;
		}
		
		int column = 0;
		int row = 0;

		switch (state.getActionState()) {
		case DAMAGED_FRONT:
			row = 1;
			column = 4;
			break;
		case DAMAGED_BACK:
			row = 1;
			column = 5;
			break;
		default:
			break;
		}


		screen.drawSprite(Screen.PLAYER, direction, column, row, y, filter);
	}

	/**
	 * Triggers action events in the State object. Does not do anything is input
	 * is not being accepted
	 * 
	 * @param keyCode
	 *            Numerical representation of the key that has been pressed
	 */
	public void keyPressed(int keyCode) {
		if (!isAcceptingInput) {
			return;
		}
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			state.moveLeft();
			break;
		case KeyEvent.VK_RIGHT:
			state.moveRight();
			break;
		case KeyEvent.VK_C:
			state.jump();
			break;
		case KeyEvent.VK_Z:
			state.roll();
			break;
		case KeyEvent.VK_X:
			state.attack();
			break;
		case KeyEvent.VK_UP:
			state.block();
			break;
		default:
			break;
		}
	}

	/**
	 * Tell the State object that a key has been released so that it can halt
	 * actions, and/or allow queued actions to occur
	 * 
	 * @param keyCode
	 *            Numerical representation of the key that was released
	 */
	public void keyReleased(int keyCode) {
		if (!isAcceptingInput) {
			return;
		}
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			state.releaseLeft();
			break;
		case KeyEvent.VK_RIGHT:
			state.releaseRight();
			break;
		case KeyEvent.VK_UP:
			state.releaseBlock();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Called once all enemies on the screen are defeated. The Player
	 * will stop accepting input and do his victory pose.
	 */
	public void victoryState() {
		isAcceptingInput = false;
		state.resetAnimationCount();
		state.victoryState();
	}

	/**
	 * This is called when the player has intersected with, or been attacked by
	 * an enemy. The location of the enemy is used to determine if it was an
	 * attack from the front or behind. Damage direction variable is used to
	 * tell the render and update methods which direction to launch the player.
	 * There is immunity after every attack to prevent chained attacks. If
	 * immunity is activated then this method does nothing
	 * 
	 * @param anEnemy
	 * 			  The attacking enemy type. If this type is a golem, it may
	 * 			  not damage the enemy if the player is currently jumping over
	 *  	      his vulnerable state.
	 * @param enemyX
	 *            The X coord of the attacking enemy, used for to find out which
	 *            direction the attack came from
	 * @return Was the player hit, or has he been hit?
	 */
	public boolean playerHit(Enemy anEnemy, double enemyX) {
		if (state.isImmune()) {
			return false;
		}
		
		if (anEnemy.getName() == "Golem") {
			if (anEnemy.isThreatening() == false && getActionState() == Action.JUMP) {
				gravityAccel = gravityAccel / 2;
				return false;
			}
			
		}
		
		if (anEnemy.isThreatening() == false) {
			return false;
		}

		Direction direction = state.getDirectionState();
		if (enemyX >= x) {
			if (direction == Direction.LEFT) {
				if (state.getActionState() == Action.BLOCK && immune && anEnemy.isBlockable()) {
					immune = true;
					return false;
				}
				damageDirection = Direction.LEFT;
				if (! invincible) {
					health--;
				}
				state.damagedBack();
				return true;
			}
			if (direction == Direction.RIGHT) {
				if ((state.getActionState() == Action.BLOCK || immune) && anEnemy.isBlockable()) {
					immune = true;
					return false;
				}
				damageDirection = Direction.LEFT;
				if (! invincible) {
					health--;
				}
				state.damagedFront();
				return true;
			}
		}

		if (enemyX <= x) {
			if (direction == Direction.LEFT) {
				if ((state.getActionState() == Action.BLOCK || immune) && anEnemy.isBlockable()) {
					immune = true;
					return false;
				}
				damageDirection = Direction.RIGHT;
				if (! invincible) {
					health--;
				}
				state.damagedFront();
				return true;
			}
			if (direction == Direction.RIGHT) {
				if (state.getActionState() == Action.BLOCK && immune && anEnemy.isBlockable()) {
					immune = true;
					return false;
				}
				damageDirection = Direction.RIGHT;
				if (! invincible) {
					health--;
				}
				state.damagedBack();
				return true;
			}
		}
		return false;
	}

	/**
	 * Allows other Classes to see what direction the player is facing
	 */
	@Override
	public Direction getDirection() {
		return state.getDirectionState();
	}
	
	/**
	 * Returns the current x location of the player.
	 * @return the x coordinate of the player.
	 */
	public double getX() {
		return x;
	}



}