package roundWorld.entity.enemy;

import roundWorld.graphics.Screen;

/**
 * The most basic enemy. Porcupine simply walks around and needs to be jumped
 * over, but he does not attack
 * 
 * @author Andrew Black
 * 
 */
public class Porcupine extends Enemy {
	/**
	 * Constants that define the width/height of each sprite, and the base
	 * movement speed
	 */
	public static final int SPRITE_WIDTH = 60, SPRITE_HEIGHT = 50, BASE_SPEED = 1;

	
	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Porcupine(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		BLUE_ROW_OFFSET = 1;
		
		name = "Porcupine";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 4;

		hitBoxWidth = 43;
		hitBoxHeight = 43;
		hitBoxOffsetX = 7;
		hitBoxOffsetY = 7;
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
		columnRowType[1] = 1;
		columnRowType[2] = Screen.PORCUPINE;

		int framesPerSprite = 10;
		if (animationCount < framesPerSprite) {
			columnRowType[0] = 1;
		} else if (animationCount < framesPerSprite * 2) {
			columnRowType[0] = 2;
		} else if (animationCount < framesPerSprite * 3) {
			columnRowType[0] = 3;
		} else if (animationCount < framesPerSprite * 4) {
			columnRowType[0] = 2;
		} else {
			columnRowType[0] = 1;
			state.resetAnimationCount();
		}

		return columnRowType;
	}

}