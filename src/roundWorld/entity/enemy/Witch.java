package roundWorld.entity.enemy;

import java.awt.Rectangle;

import roundWorld.graphics.Screen;
import roundWorld.stage.Level;
import roundWorld.stage.Stage;

/**
 * The Witch is the game's boss. She spends most of the time hovering
 * above the player's reach, summoning fireballs and lightning bolts
 * as the player deals with various other enemies. After defeating
 * said enemies, the player character automatically knocks her off
 * her broom, where he can proceed to hit her. After taking enough
 * damage, she returns to her broom and summons a new wave of enemies.
 * This is repeated two more times.
 * 
 * @author Andrew Aitken
 *
 */
public class Witch extends Enemy {

	public static final int SPRITE_WIDTH = 100, SPRITE_HEIGHT = 325, BASE_SPEED = 6;
	
	
	/**
	 * Used during the Witch's AI routine. It is used as a delay between her
	 * spellcasting. When she's not actively attacking, it ticks down every
	 * AI update. When it reaches zero, she casts a random spell (fire or lightning).
	 */
	private int spellDelay;
	
	/**
	 * Whether or not the Witch can cast magic. This is usually true,
	 * but when the phase changes and she's about to be made vulernable,
	 * this is used to prevent her from trying to cast a spell during
	 * the player's victory animation.
	 */
	private boolean canCast;
	
	/**
	 * Representation of the two spells she can cast to attack the player.
	 * Used when determing which cast animation to use.
	 */
	public static final int START_LEVEL = 0, FIREBALL = 1, LIGHTNING = 2;
	
	/**
	 * The next spell to be used. 
	 */
	private int nextSpell;
	
	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Witch(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		name = "Witch";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = 3;
		health = 21;

		hitBoxWidth = 72;
		hitBoxHeight = 44;
		hitBoxOffsetX = 16;
		hitBoxOffsetY = 280;
		
		nextSpell = START_LEVEL;
		
		spellDelay = 104;
		
		canCast = false;
		
		filter = NOCOLOUR;
	}
	
	/**
	 * Update for the Witch. It overrides the generic Enemy update
	 * to control her special death sequence.
	 * 
	 * @param playerLocation
	 *            Rotation value of the Player
	 */
	@Override
	public void update(double playerLocation) {

		state.decrementReverseLock();
		rotation -= playerLocation;
		
		aiUpdate(playerLocation);
		
		if (nextSpell == START_LEVEL && spellDelay > 44) {
			state.resetAnimationCount();
		}
		
		if (action == Action.BLOCK){
			filter -= 0x01000000;
			return;
		}
		
		if (invincibility_time > 0) {
			invincibility_time--;
		}        
		
		if (invincibility_time < 10) {
			filter = NOFLASH;
		}
		

		
	}
	
	/**
	 * The Witch's AI
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
		switch (action) {
		case IDLE:

			state.lockReverse();
			if (health == 14 || health == 7) {
				state.resetAnimationCount();
				action = Action.JUMP;
			}
			
			break;
		case WALK:
			if (spellDelay == 0) {
				Level.witchCastSpell(nextSpell);
				nextSpell = (Math.random() < 0.5 ? FIREBALL : LIGHTNING);
				spellDelay = health * 15;
			}
			if (state.getDirectionState() == Direction.LEFT) {
				if (distance > 150 && distance < 3200) {
					speed = 3;
				}
				if (distance > 200 && distance < 4804) {
					speed = BASE_SPEED;
					state.moveRight();
				}
			}
			
			if (state.getDirectionState() == Direction.RIGHT) {
				if (distance < 4874 && distance > 3220) {
					speed = 3;
				}
				if (distance < 4824 && distance > 3220) {
					speed = BASE_SPEED;
					state.moveLeft();
				}
			}

			move(state.getDirectionState());
			
			if (canCast) {
				spellDelay--;
			}
			if (spellDelay == 0) {
				if (nextSpell == START_LEVEL) {
					action = Action.JUMP;
					break;
				}
				state.resetAnimationCount();
				action = Action.ATTACK;
			}
			break;
			
		case ATTACK:
			if (! canCast) {
				action = Action.WALK;
				spellDelay = health * 15;
			}
			break;
			
		case ROLL:
			if ((state.getAnimationCount() >= 10 * 10)) {
				action = Action.IDLE;
				state.resetAnimationCount();
			}
		case JUMP:
			if (health == 0 && state.getAnimationCount() >= 44) {
				action = Action.BLOCK;
				filter = DARK;
			}
			if (state.getAnimationCount() >= 150) {
				action = Action.WALK;
				state.resetAnimationCount();
			}
			break;
		case DYING:
			action = Action.JUMP;
			break;
		case BLOCK:
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

		columnRowType[2] = Screen.WITCH;
		int framesPerSprite;
		
		switch (action) {
		case IDLE:
			columnRowType[1] = 3;
			framesPerSprite = 6;
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
		case WALK:
			columnRowType[0] = 1;
			columnRowType[1] = 1;
			break;
		case ATTACK:
			framesPerSprite = 2;
			columnRowType[1] = 1;
			if (animationCount < framesPerSprite * 10) {
				columnRowType[0] = 2;
			} else if (animationCount > framesPerSprite * 40) {
				columnRowType[0] = 2;
				action = Action.WALK;
			} else if (animationCount % 4 <= 1) {
				columnRowType[0] = (nextSpell == FIREBALL ? 3 : 5);
			} else {
				columnRowType[0] = (nextSpell == FIREBALL ? 4 : 6);
			}
			
			break;
			
		case ROLL:
			framesPerSprite = 10;
			columnRowType[1] = 2;
			if (animationCount < framesPerSprite * 6) {
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 7) {
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 8) {
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 9) {
				columnRowType[0] = 4;
			} else if (animationCount < framesPerSprite * 10) {
				columnRowType[0] = 5;
			} else {
				columnRowType[0] = 6;
			}
			break;
		case JUMP:
			framesPerSprite = 2;
			if (animationCount < framesPerSprite * 10) {
				columnRowType[1] = 4;
				columnRowType[0] = 1;
			} else if (animationCount < framesPerSprite * 12) {
				columnRowType[1] = 4;
				columnRowType[0] = 2;
			} else if (animationCount < framesPerSprite * 14) {
				columnRowType[1] = 4;
				columnRowType[0] = 3;
			} else if (animationCount < framesPerSprite * 18) {
				columnRowType[1] = 4;
				columnRowType[0] = 4;
			} else if (animationCount < framesPerSprite * 20) {
				columnRowType[1] = 4;
				columnRowType[0] = 5;
			} else if (animationCount < framesPerSprite * 22) {
				columnRowType[1] = 4;
				columnRowType[0] = 6;
			} else if (animationCount < framesPerSprite * 24) {
				columnRowType[1] = 1;
				columnRowType[0] = 2;
			} else if (animationCount % 8 <= 1) {
				columnRowType[1] = 1;
				columnRowType[0] = 3;
			} else if (animationCount % 8 <= 3) {
				columnRowType[1] = 1;
				columnRowType[0] = 4;
			} else if (animationCount % 8 <= 5) {
				columnRowType[1] = 1;
				columnRowType[0] = 5;
			} else {
				columnRowType[1] = 1;
				columnRowType[0] = 6;
			}
			break;
		case DYING:
			columnRowType[1] = 4;
			columnRowType[0] = 1;
			break;
			
		default:
			columnRowType[0] = 1;
			columnRowType[1] = 2;
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
		if (action != Action.IDLE) {
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
	
	/**
	 * Returns whether or not the enemy is threatening the player.
	 * That if, is by touching the enemy the player will be damaged.
	 * Generally always true. Should be overwritten by exceptional
	 * circumstances.
	 * @return Whether the player is damaged on contact.
	 */
	@Override
	public boolean isThreatening() {
		return false;
	}
	
	/**
	 * Sets whether or not the witch is able to cast spells.
	 * When set to false, her spellDelay timer does not tick,
	 * and if she's currently in an attack animation it will be
	 * interrupted.
	 * @param inCanCast
	 */
	public void canCast(boolean inCanCast) {
		canCast = inCanCast;
	}
	
	/**
	 * Called when entering LEVEL_5 phases where the Witch should be
	 * made vulnerable. It sets her state to the ROLL state, which
	 * simulates her falling off her broom and entering her vulnerable
	 * IDLE state.
	 */
	public void knockDown() {
		action = Action.ROLL;
		health--;
		state.resetAnimationCount();
	}
	
	/**
	 * Returns whether the Witch has finished her death animation
	 * and can be removed from the enemy list.
	 */
	@Override
	public boolean isDead() {
		if (action == Action.BLOCK && filter == 0x01ff00ff) {
			return true;
		}
		
		return false;
	}

}
