package roundWorld.entity.enemy;

import roundWorld.entity.Entity;
import roundWorld.graphics.Screen;
import roundWorld.stage.Colourspace;
import roundWorld.stage.Stage;

/**
 * Super class for all Enemy objects. Contains common methods such as update and
 * render because they all draw and move the same way
 * 
 * @author Andrew Black, Andrew Aitken
 * 
 */
public class Enemy extends Entity {
	/**
	 * Common Enemy state object for keeping track of actions and timers
	 */
	protected EnemyStates state;
	
	/**
	 * Common enemy action object for keeping track of current action.
	 */
	protected Action action;
	
	/**
	 * The colours an enemy can be, for use in determining whether or not
	 * they are damaged when attacked. NOCOLOUR enemies are always damaged.
	 */
	public static final int RED = 1, BLUE = 2, NOCOLOUR = 0;
	
	/**
	 * The colour of this particular enemy.
	 */
	protected int colour;
	
	/**
	 * The offset of rows for use by blue enemies.
	 */
	protected int BLUE_ROW_OFFSET;
	
	/**
	 * The current health of the enemy. It is decremented when hit.
	 * When its health reaches 0, the Level class will remove it.
	 */
	protected int health;
	
	/**
	 * The time in frames that the enemy is invulnerable. This is set
	 * when the enemy is hit, and lasts for the duration of a single
	 * attack animation.
	 */
	protected int invincibility_time;
	
	/**
	 * The different possible flash effects for when the enemy is attacked.
	 * When damaged, it flashes yellow; when invulnerable to its attack,
	 * if flashes its colour. No flash allows Screen's drawSprite to know not to
	 * flash at all.
	 */
	protected final int START = 0x00ff00ff, HURT = 0xffffff00, DARK = 0xffff00ff, NOTHURTRED = 0xffff0000, NOTHURTBLUE = 0xff0080ff, NOFLASH = 0;
	
	/**
	 * The current flash effect to use. At each update cycle, it will set this
	 * to NOFLASH. When hit by the player, it will change. Each flash is 
	 * rendered only once.
	 */
	protected int filter;
	
	/**
	 * For basic AI use. It will tick down every update. When it reaches zero,
	 * the default AI will turn the enemy around. In complex AI it is up to the
	 * individual aiUpdate whether or not to use this and what to set it to.
	 */
	protected int turnAroundTime;
	
	/**
	 * Contains the enemy type (i.e. "Porcupine"). Used so the player can
	 * identify which enemy he has made contact with, for use of determining
	 * whether or not the player is being damaged by touching the Golem.
	 */
	protected String name;
	
	/**
	 * Represents whether or not the player can use the Block action to avoid
	 * damage. This is usually set to true, but some enemies have unblockable
	 * attacks.
	 */
	protected boolean blockable;
	

	/**
	 * Constructor for creating enemies during a level.
	 * Enemies are placed relative to the position of the player, as if
	 * the player's x location on the stage was still x = 0.
	 * 
	 * @param direction 
	 * 			Orientation for the enemy to be facing initially.
	 * @param inAction
	 * 			Action that the enemy will be taking upon its creation.
	 * @param position
	 * 			Location on the stage that Enemy will start at. Used to
	 *          calculate the starting value of X.
	 * @param playerPosition
	 * 			Location on the stage of the player. Used to calculate the
	 * 			starting value of X.
	 * @param inColour
	 * 			The colour, if any, of the enemy.
	 */
	public Enemy(Direction direction, Action inAction, int position, double playerPosition, int inColour) {
		super(direction, inAction, position);
		name = "Generic";
		
		rotation = -Stage.RADIAN_PER_PIXEL * position;
		x = (position + playerPosition) * Stage.UNIT_PER_PIXEL;
		y = 0;
		
		action = inAction;
		gravityAccel = 0;
		
		
		state = new EnemyStates(direction);
		turnAroundTime =  300 + (int) (Math.random() * 10);
		
		colour = inColour;
		action = inAction;
		blockable = true;
		BLUE_ROW_OFFSET = 1;
		filter = START;
	}

	/**
	 * Gets information for Enemy direction and animation counter, and calls the
	 * child class for an Array of which sprite to render. Screen class is then
	 * called to perform the draw
	 * 
	 * @param screen
	 *            Screen object so it can be called with the arguments required
	 *            to draw the Enemy
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

		int[] columnRowType = getColumnRowType(animationCount);
		int column = columnRowType[0];
		int row = columnRowType[1];
		int type = columnRowType[2];

		screen.drawSprite(type, direction, column, (colour == BLUE) ? row + BLUE_ROW_OFFSET : row, rotation, filter);
	}

	/**
	 * Called by Level to check where Enemy is in relation to other Entities
	 * 
	 * @return The X coordinate as a double
	 */
	public double getX() {
		return x;
	}

	/**
	 * Generic update that applies to all enemies at all times. After
	 * completing these tasks, it calls the enemy-specific aiUpdate.
	 * 
	 * @param playerLocation
	 *            Rotation value of the Player
	 */
	public void update(double playerLocation) {

		state.decrementReverseLock();
		rotation -= playerLocation;
		
		if (action == Action.DYING){
			filter -= 0x10000000;
			return;
		}
		
		
		
		if (invincibility_time > 0) {
			invincibility_time--;
			if (invincibility_time < 10) {
				filter = NOFLASH;
			}
		}        
		
		if ((filter & 0x00ffffff) == (DARK & 0x00ffffff)) {
			filter += 0x05000000;
			if (filter == (DARK & 0xf0ffffff)) {
				filter = NOFLASH;
			}
		}

		aiUpdate(playerLocation);
		
	}
	
	/**
	 * Contains artificial intelligence information to determine the enemy's
	 * next action. It should be overridden by child classes. Not doing so
	 * will leave the enemy with a simple walk action.
	 * @param playerLocation
	 */
	public void aiUpdate(double playerLocation) {
		turnAroundTime--;
		double distance = rotation / Stage.RADIAN_PER_PIXEL;

		while (distance < 0) {
			distance += 5024;
		}
		while (distance > 5024) {
			distance -= 5024;
		}
		
		if (turnAroundTime <= 0) {
			turnAroundTime = 300 + (int) (Math.random() * 180);
			state.switchDirection();
		}
		move(state.getDirectionState());
	}
	
	/**
	 * Determines the distance between the enemy and the player,
	 * starting from the direction the enemy is facing. 
	 * @param playerLocation The player's current location.
	 * @return the distance away from the enemy to the player.
	 */
	public double getDistanceFromPlayer() {
		double distance = rotation / Stage.RADIAN_PER_PIXEL;
		
		while (distance < 0) {
			distance += 5024;
		}
		while (distance > 5024) {
			distance -= 5024;
		}
				
		return distance;
	}

	/**
	 * Causes the Enemy to change its direction. Reverse is locked out for a
	 * short time after being called to prevent infinitely looping reversals
	 * between two enemies (hopefully)
	 */
	public void reverse() {
		if (state.isReverseLocked()) {
			return;
		}

		state.lockReverse();
		turnAroundTime += 300;
		Direction direction = state.getDirectionState();
		if (direction == Direction.LEFT) {
			state.moveRight();
			return;
		}

		if (direction == Direction.RIGHT) {
			state.moveLeft();
			return;
		}
	}

	/**
	 * Returns the direction which the Enemy is currently facing
	 */
	@Override
	public Direction getDirection() {
		return state.getDirectionState();
	}

	/**
	 * Overridden by child classes to return sprite information
	 */
	public int[] getColumnRowType(int animationCount) {
		return null;
	}
	
	/**
	 * Called when the enemy is within the player's hitbox. It calls
	 * the Colourspace's abstract method to determine if is damaged.
	 * If it is, it decrements its health by one.
	 * 
	 * @param playerDirection The side the player is attacking
	 */
	public void attackedByPlayer(Direction playerDirection) {
		if (invincibility_time > 0) {
			return;
		}
		boolean isHurt = Colourspace.checkIfVulnerable(colour, playerDirection);
		if (isHurt) {
			health--;
			invincibility_time = 18;
			filter = HURT;
			if (health == 0) {
				action = Action.DYING;
			}
		} else {
			invincibility_time = 18;
			filter = (colour == RED) ? NOTHURTRED : NOTHURTBLUE;
		}
	}
	
	/**
	 * Returns the current health of this instance of the enemy.
	 * @return the enemy's health.
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Sets the current health of the enemy.
	 * 
	 * @param inHealth the amount of health to set to.
	 */
	public void setHealth(int inHealth) {
		health = inHealth;
	}
	
	/**
	 * Checks whether or not the enemy is dead. An enemy is considered dead
	 * when in the DYING state and they have faded the last frame of their
	 * animation.
	 * @return whether or not the enemy can be queued for removal.
	 */
	public boolean isDead() {
		if (action == Action.DYING && filter == 0x0fffff00) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the type of the enemy as a string (i.e. "Porcupine")
	 * @return The name of the enemy.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns whether or not the enemy is threatening the player.
	 * That if, is by touching the enemy the player will be damaged.
	 * Generally always true. Should be overwritten by exceptional
	 * circumstances.
	 * @return Whether the player is damaged on contact.
	 */
	public boolean isThreatening() {
		return true;
	}
	
	/**
	 * Returns whether or not the player nullifies damage if blocking.
	 * @return true is blockable, false is damage regardless
	 */
	public boolean isBlockable() {
		return blockable;
	}
	
	/**
	 * Returns the colour of the enemy according to the final int values.
	 * 
	 * @return an int that represents the enemy's colour.
	 */
	public int getColour() {
		return colour;
	}


}