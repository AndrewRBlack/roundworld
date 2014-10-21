package roundWorld.stage;

import java.awt.Rectangle;

import roundWorld.graphics.Screen;

/**
 * Holds constants used throughout the Game and keeps track of the current
 * rotation of the stage
 * 
 * @author Andrew Black
 * 
 */
public class Stage {
	/**
	 * The dimensions of the circular portion of the stage and the length of the
	 * outside border area in the stage image
	 */
	public static final int RADIUS = 800, DIAMETER = 1600, BORDER_AREA = 200;
	/**
	 * The distance that the stage is shifted up to make room for the health
	 * bars
	 */
	public static final int HEALTH_BAR_OFFSET = -100;
	/**
	 * The distances that the stage is being shifted from the top left, so that
	 * it appears correctly in the window
	 */
	public static final int FRAME_OFFSET_X = -400, FRAME_OFFSET_Y = -1120 + HEALTH_BAR_OFFSET;
	/**
	 * The actual distances required to position the stage (including the
	 * outside area of the stage image)
	 */
	public static final int SHIFT_INTO_FRAME_X = FRAME_OFFSET_X - BORDER_AREA, SHIFT_INTO_FRAME_Y = FRAME_OFFSET_Y - BORDER_AREA;
	/**
	 * The distance from the far side of the stage image to the center of the
	 * circle
	 */
	public static final int CENTER_OF_IMAGE = RADIUS + BORDER_AREA;
	/**
	 * The relative center of the stage after it has been shifted into position
	 */
	public static final int RELATIVE_CENTER_X = RADIUS + FRAME_OFFSET_X, RELATIVE_CENTER_Y = RADIUS + FRAME_OFFSET_Y;
	/**
	 * The number of actual points around the circle
	 */
	public static final double CIRCUMFERENCE = (DIAMETER * Math.PI);
	/**
	 * The value for the units when moving around the circle, and the radian
	 * adjustment required for each movement
	 */
	public static final double UNIT_PER_PIXEL = (CIRCUMFERENCE / 5026), RADIAN_PER_PIXEL = (2 * Math.PI) / CIRCUMFERENCE;
	/**
	 * Rectangles for checking if an entity is overlapping at the point where
	 * the circle switches from its circumference value to zero and vice versa
	 */
	public static final Rectangle POSITIVE_BOUNDS = new Rectangle(5026, -1000, 1000, 1000),
			NEGATIVE_BOUNDS = new Rectangle(-1000, -1000, 1000, 1000);

	/**
	 * Tracks the current value (in radians) that the stage is rotated
	 */
	private double stageRotation;

	/**
	 * Constructor initializes the rotation variable
	 */
	public Stage() {
		stageRotation = 0;
	}

	/**
	 * Calls the Screen object to render the stage based on its current rotation
	 * 
	 * @param screen
	 *            The Screen class
	 */
	public void render(Screen screen, boolean isStageFive) {
		//Frame rate drops at close-to-0 rotations, so band-aid
		if (stageRotation < 0.001 && stageRotation > -0.001) {
			screen.drawStage(0.001, isStageFive);
			return;
		}
		screen.drawStage(stageRotation, isStageFive);
	}

	/**
	 * Updates the rotation of the stage based on current location of the Player
	 * 
	 * @param playerLocation
	 *            Amount that the stage should be rotated based on where the
	 *            player is standing
	 */
	public void update(double playerLocation) {
		stageRotation -= playerLocation;

	}

}