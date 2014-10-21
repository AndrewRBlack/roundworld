package roundWorld.stage;

import roundWorld.entity.Entity.Direction;
import roundWorld.entity.enemy.Enemy;

/**
 * Splits the screen into two sides, red and blue (drawn cyan).
 * Controls whether or not enemies are damaged by the player's attacks.
 * 
 * @author Andrew Aitken
 *
 */
public class Colourspace {
	/**
	 * State variables to represent the Colourspace's current appearance.
	 * INACTIVE: The colourspace is not visible.
	 * ACTIVE: The colourspace is visible in its standard form.
	 * CLEARING: The colourspace is transitioning from active to inactive.
	 * FORMING: The colourspace is transition from inactive to active.
	 */
	public static final int INACTIVE = 0, ACTIVE = 1, CLEARING = 2, FORMING = 3;
	
	/**
	 * The current state of the colourspace (see above)
	 */
	private int state;
	
	/**
	 * The current sprite of the CLEARING or FORMING animations.
	 * Note that CLEARING counts from sprites 1 to 5, while
	 * FORMING counts from sprites 5 to 0 (backwards).
	 */
	private int currentSprite;
	
	/**
	 * Tracks how many frames remain for the current animation's current sprite.
	 * During an animation, it ticks downward once per update, updating
	 * currentSprite when it reaches 0 and resets itself.
	 */
	private int framesToNextSprite;
	
	/**
	 * Constructor. Will automatically form on the first update.
	 */
	public Colourspace() {
		state = INACTIVE;
		currentSprite = 5;
		framesToNextSprite = 0;
	}
	
	/**
	 * Returns the current state of the colourspace.
	 * @return ACTIVE, INACTIVE, CLEARING, or FORMING
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Returns the current sprite of a CLEARING or FORMING animation.
	 * @return the column on the spritesheet.
	 */
	public int getFrame() {
		return currentSprite;
	}
	
	/**
	 * Changes the colourspace's state to FORMING, thus forming itself
	 * on the screen and then setting itself to the ACTIVE state.
	 */
	public void form() {
		state = FORMING;
	}
	
	/**
	 * Changes the colourspace's state to CLEARING, thus clearing itself
	 * off the screen and then setting itself to the INACTIVE state.
	 */
	public void clear() {
		state = CLEARING;
		framesToNextSprite = 90;
	}
	
	/**
	 * Determines whether or not a player's attack damages an enemy based on the
	 * enemy's colour, and its location on the screen (derived by the direction the
	 * player is attackin). Returns true if the enemy is vulnerable (due to its colour),
	 * and false it is not. Non-coloured enemies (i.e., the Witch) automatically returns true.
	 * 
	 * @param colour The colour of the enemy being checked.
	 * @param playerDirection The direction the player is attakcing.
	 * @return whether or not the enemy's is vulnerable
	 */
	public static boolean checkIfVulnerable(int colour, Direction playerDirection) {
		
		if (colour == Enemy.NOCOLOUR)
			return true;
		
		if (colour == Enemy.RED && playerDirection == Direction.RIGHT)
			return true;
					
		if (colour == Enemy.BLUE && playerDirection == Direction.LEFT)
			return true;
		
		return false;
	}
	
	/**
	 * The main update method that controls its animation, when necessary.
	 */
	public void update() {
		switch (state) {
			case ACTIVE:
			case INACTIVE:
				break;
			case CLEARING:
				clearingUpdate();
				break;
			case FORMING:
				formingUpdate();
				break;
		}
	}
	
	/**
	 * Updates the colourspace's current progress through the CLEARING animation.
	 */
	private void clearingUpdate() {
		framesToNextSprite--;
		if (framesToNextSprite > 0) {
			return;
		}
		
		currentSprite++;
		if (currentSprite > 5) {
			currentSprite = 5;
			state = INACTIVE;
			return;
		}
		framesToNextSprite = 6;
	}
	
	/**
	 * Updates the colourspace's current progress through the FORMING animation.
	 */
	private void formingUpdate() {
		framesToNextSprite--;
		if (framesToNextSprite > 0) {
			return;
		}
		
		currentSprite--;
		if (currentSprite < 0) {
			currentSprite = 0;
			state = ACTIVE;
			return;
		}
		framesToNextSprite = 6;
	}
}
