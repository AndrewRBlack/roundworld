package roundWorld.stage;

import roundWorld.Game;
import roundWorld.graphics.Screen;

/**
 * The StartEffect appears at the beginning of each stage, where two images
 * ("Stage x" and "Start!") fly by from one side of the stage to the other.
 * 
 * @author Andrew Aitken
 */
public class StartEffect {

	/**
	 * The coordinates of the "Stage x" and "Start!" images respectively.
	 */
	private int stageX, stageY, startX, startY;
	
	/**
	 * The stage the effect is for.
	 */
	private int currentStage;
	
	/**
	 * The speed at which the effect flies by. Its absolute value is used.
	 */
	private int speed;
	
	/**
	 * Pauses the speed decrement when it reaches zero, so that the images are
	 * stopped in the center for a short time.
	 */
	private int speedDelay;
	
	/**
	 * Constructor to start the effect.
	 * @param stage the stage this affect applies to.
	 */
	public StartEffect(int stage) {
		stageX = -200;
		stageY = (Game.WINDOW_HEIGHT / 2) - 35;
		startX = (Game.WINDOW_WIDTH);
		startY = (Game.WINDOW_HEIGHT / 2);
		currentStage = stage;
		speed = 29;
		speedDelay = 60;
	}
	
	/**
	 * Calculates the new locations of the stage effects.
	 */
	public void update() {
		if (speed < -30) {
			return;
		}
		if (speed == 0 && speedDelay > 0) {
			speedDelay--;
			return;
		}
		stageX += Math.abs(speed);
		startX -= Math.abs(speed);
		speed--;
	}
	
	/**
	 * Tells the Screen where to draw the effects, if necessary.
	 * 
	 * @param screen the screen object to draw to.
	 */
	public void render(Screen screen) {
		if (speed < -30) {
			return;
		}
		screen.drawStartEffect(currentStage, stageX, stageY, startX, startY);
	}
	
	/**
	 * Returns the current speed of the flyby, for use in removing.
	 * @return The current speed.
	 */
	public int getSpeed() {
		return speed;
	}
}
