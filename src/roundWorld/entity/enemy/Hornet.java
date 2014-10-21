package roundWorld.entity.enemy;

import roundWorld.graphics.Screen;

/**
 * A simple enemy that flies above the ground, making him unable to be jumped
 * over. The Hornet needs to be rolled under to get past him
 * 
 * @author Andrew Black
 * 
 */
public class Hornet extends Enemy {
	/**
	 * Constants that define the width/height of each sprite, and the base
	 * movement speed
	 */
	public static final int SPRITE_WIDTH = 70, SPRITE_HEIGHT = 100, BASE_SPEED = 1;

	/**
	 * Constructor initialized declared variables from super class with
	 * constants declared at this level. Hit box properties are also set
	 */
	public Hornet(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position, playerPosition, inColour);
		
		name = "Hornet";
		
		spriteWidth = SPRITE_WIDTH;
		spriteHeight = SPRITE_HEIGHT;
		speed = BASE_SPEED;
		health = 4;

		hitBoxWidth = 26;
		hitBoxHeight = 46;
		hitBoxOffsetX = 28;
		hitBoxOffsetY = 13;

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
		columnRowType[2] = Screen.HORNET;

		int framesPerSprite = 1;
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