package roundWorld.entity.enemy;


import roundWorld.graphics.Screen;

/**
 * The Fireball is summoned by the Witch. It careens across the screen in the
 * direction it is created in, and disappears after five seconds. The fireball
 * cannot be blocked and must be rolled under.
 * 
 * @author Andrew Aitken
 *
 */
public class Fireball extends Enemy {
	
	public static final int SPRITE_WIDTH = 50, SPRITE_HEIGHT = 100, BASE_SPEED = 10;

	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Fireball(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		name = "Fireball";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 300;

		hitBoxWidth = 25;
		hitBoxHeight = 19;
		hitBoxOffsetX = 21;
		hitBoxOffsetY = 13;
		
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
		case WALK:
			health--;
			if (health == 0) {
				action = Action.DYING;
				filter = HURT;
			}
			move(state.getDirectionState());
			break;
		default:
			move(state.getDirectionState());
		}
	}
	
	/**
	 * Overridden by child classes to return sprite information
	 */
	public int[] getColumnRowType(int animationCount) {
		int framesPerSprite = 6;
		int columnRowType[] = new int[3];

		columnRowType[2] = Screen.FIREBALL;
		columnRowType[1] = 1;
		
		if (animationCount < framesPerSprite) {
			columnRowType[0] = 1;
		} else if (animationCount < framesPerSprite * 2) {
			columnRowType[0] = 2;
		} else if (animationCount < framesPerSprite * 3) {
			columnRowType[0] = 3;
		} else if (animationCount < framesPerSprite * 4) {
			columnRowType[0] = 4;
		} else if (animationCount < framesPerSprite * 5) {
			columnRowType[0] = 5;
		} else {
			state.resetAnimationCount();
			for (int i = 0; i < framesPerSprite * 3; i++) {
				state.incrementAnimationCount();
			}
			columnRowType[0] = 4;
		}
		
		return columnRowType;

	}
}
