package roundWorld.stage;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import roundWorld.input.InputHandler;
import roundWorld.entity.Entity.Action;
import roundWorld.entity.Entity.Direction;
import roundWorld.entity.enemy.Enemy;
import roundWorld.entity.enemy.Fireball;
import roundWorld.entity.enemy.Golem;
import roundWorld.entity.enemy.Hornet;
import roundWorld.entity.enemy.Lightning;
import roundWorld.entity.enemy.Porcupine;
import roundWorld.entity.enemy.Scorpion;
import roundWorld.entity.enemy.Witch;
import roundWorld.entity.player.Player;
import roundWorld.graphics.Screen;

/**
 * The Level class creates and manages all Entities, tips, titles, stages and
 * holds the code for multi-stage progression. It is instantiated directly by
 * the Game instance and gets passed the InputHandler class
 * 
 * @author Andrew Black, Andrew Aitken
 * 
 */
public class Level {
	/**
	 * Enumeration to help set and determine what phase the game is in
	 */
	public static enum Phase {
		TITLE_SCREEN, HOW_TO_PLAY,
		LEVEL_1_TIP, LEVEL_1, LEVEL_1_WIN, 
		LEVEL_2_TIP, LEVEL_2, LEVEL_2_WIN,
		LEVEL_3_TIP, LEVEL_3, LEVEL_3_WIN,
		LEVEL_4_TIP, LEVEL_4, LEVEL_4_WIN,
		LEVEL_5_TIP, LEVEL_5_INTRO,
		LEVEL_5A1, LEVEL_5A2, LEVEL_5A3,
		LEVEL_5B1, LEVEL_5B2, LEVEL_5B3,
		LEVEL_5C1, LEVEL_5C2, LEVEL_5C3,
		LEVEL_5_WIN, VICTORY,
		FAILING, GAME_OVER
	};

	/**
	 * All enemies on screen at a time are contained in this list. Once it is
	 * empty the level is over
	 */
	private static List<Enemy> allEnemies;

	/**
	 * Spells the Witch can cast. They are used by entityToAdd to determine
	 * what spell, if any, to add to the allEnemies list.
	 */
	private static final int NONE = 0, FIREBALL = 1, LIGHTNING = 2;
	/**
	 * An entity to be added to the field in the next update.
	 * Used by the Witch enemy for casting spells.
	 */
	private static int entityToAdd;
	
	/**
	 * References the InputHandler object that is passed from the Game Instance.
	 */
	private InputHandler input;
	/**
	 * Used to keep track of the rotation as set by the Player class. A delta is
	 * calculated based on previous values to ensure that lag does not
	 * desynchronize the game. The delta is used by enemies and the stage to
	 * control their movement
	 */
	private double lastPlayerLocation, playerLocationDelta;
	/**
	 * Holds the current instance of the game stage to be rendered in background
	 */
	private Stage stage;
	
	/**
	 * Holds the colourspace class, which controls the vulnerability of most enemies.
	 */
	private Colourspace colourspace;
	
	/**
	 * Holds the StartEffect class, which displays an intro at the beginning of each level.
	 */
	private StartEffect startEffect;
	
	/**
	 * Holds the Player class, which drives most of the events in the game
	 */
	private Player player;
	/**
	 * Variable that keeps track of the current phase using the enumeration
	 */
	private Phase phase;
	
	/**
	 * Specifies whether the current phase is a Level 5 stage, which uses
	 * a different stage image. This value is passed to the renderer.
	 */
	private boolean isStageFive;
	
	/**
	 * A timer that, when has a number, will tick down every update then
	 * run nextPhase. The timer is disabled when it has a value of -1.
	 */
	private int timer;
	
	
	/**
	 * The unique Witch enemy.
	 */
	private Witch theWitch;

	/**
	 * When true, the player does not take damage for any reason.
	 * It is set by pressing the /i/ key on the title screen.
	 */
	private boolean invincibilityCheat;
	
	/**
	 * Instantiates the Stage and Player Objects, and begins the phase with the
	 * title screen.
	 * 
	 * @param inInput
	 *            Object from the Game instance which is held in class variable
	 */
	public Level(InputHandler inInput) {
		allEnemies = new ArrayList<>();
		input = inInput;
		timer = -1;
		resetLevel();

		input.setLevel(this);

		phase = Phase.TITLE_SCREEN;
		colourspace = new Colourspace();
		isStageFive = false;
		
	}

	/**
	 * Sets or resets the location variables, Stage and Player Objects and
	 * passes the input variable to Player, so that it can handle key events
	 */
	private void resetLevel() {
		lastPlayerLocation = 0;
		playerLocationDelta = 0;
		stage = new Stage();
		player = new Player(Direction.RIGHT, Action.IDLE, 0, invincibilityCheat);
		input.setPlayer(player);
		allEnemies = new ArrayList<>();
		
	}

	/**
	 * Allows the game logic to be updated if the phase is currently on a
	 * playable level. Update is held in a separate method
	 */
	public void update() {
		if (timer == 0) {
			nextPhase();
			timer--;
		}
		
		switch (phase) {
		case LEVEL_1:
		case LEVEL_2:
		case LEVEL_3:
		case LEVEL_4:
		case LEVEL_5_INTRO:
		case LEVEL_5A1:
		case LEVEL_5A2:
		case LEVEL_5A3:
		case LEVEL_5B1:
		case LEVEL_5B2:
		case LEVEL_5B3:
		case LEVEL_5C1:
		case LEVEL_5C2:
		case LEVEL_5C3:
		case LEVEL_1_WIN:
		case LEVEL_2_WIN:
		case LEVEL_3_WIN:
		case LEVEL_4_WIN:
		case LEVEL_5_WIN:
			updateLevel();
			break;
		case FAILING:
			player.update();
		default:
			break;
		}
	}

	/**
	 * Renders the screen based on the current phase. This switch logic allows
	 * different items to be drawn for the different phases
	 * 
	 * @param screen
	 *            The Screen object, to be passed to items that require
	 *            rendering
	 */
	public void render(Screen screen) {
		if (timer != -1) {
			timer--;
		}
		switch (phase) {
		case TITLE_SCREEN:
			screen.drawTitleScreen();
			if (invincibilityCheat) {
				screen.drawCheatNotice();
			}
			break;
		case HOW_TO_PLAY:
			screen.drawHowToPlay();
			break;
		case LEVEL_1_TIP:
			renderLevel(screen);
			renderTip(screen, 1);
			break;
		case LEVEL_2_TIP:
			renderLevel(screen);
			renderTip(screen, 2);
			break;
		case LEVEL_3_TIP:
			renderLevel(screen);
			renderTip(screen, 3);
			break;
		case LEVEL_4_TIP:
			renderLevel(screen);
			renderTip(screen, 4);
			break;
		case LEVEL_5_TIP:
			renderLevel(screen);
			renderTip(screen, 5);
			break;
			
		case LEVEL_1:
		case LEVEL_2:
		case LEVEL_3:
		case LEVEL_4:
		case LEVEL_5_INTRO:
		case LEVEL_5A1:
		case LEVEL_5A2:
		case LEVEL_5A3:
		case LEVEL_5B1:
		case LEVEL_5B2:
		case LEVEL_5B3:
		case LEVEL_5C1:
		case LEVEL_5C2:
		case LEVEL_5C3:
		case LEVEL_1_WIN:
		case LEVEL_2_WIN:
		case LEVEL_3_WIN:
		case LEVEL_4_WIN:
		case LEVEL_5_WIN:
			renderLevel(screen);
			break;
		case VICTORY:
			renderLevel(screen);
			renderTip(screen, 6);
			break;
		case FAILING:
			screen.clearScreen();
			player.render(screen, timer);
			break;
		case GAME_OVER:
			screen.clearScreen();
			renderTip(screen, 7);
			break;
		default:
			break;
		}
	}

	/**
	 * Called by various methods when a phase has ended. This uses a switch to
	 * decide how to correctly set up the next phase
	 */
	private void nextPhase() {
		switch (phase) {
		case TITLE_SCREEN:
			phase = Phase.LEVEL_1_TIP;
			isStageFive = false;
			resetLevel();
			generateLevelOne();
			break;
		case HOW_TO_PLAY:
			phase = Phase.TITLE_SCREEN;
			break;
		case LEVEL_1_TIP:
			phase = Phase.LEVEL_1;
			player.enableInput();
			colourspace.form();
			startEffect = new StartEffect(1);
			break;
		case LEVEL_1:
			phase = Phase.LEVEL_1_WIN;
			timer = 270;
			player.victoryState();
			colourspace.clear();
			break;
		case LEVEL_1_WIN:
			phase = Phase.LEVEL_2_TIP;
			resetLevel();
			generateLevelTwo();
			break;
		case LEVEL_2_TIP:
			phase = Phase.LEVEL_2;
			player.enableInput();
			colourspace.form();
			startEffect = new StartEffect(2);
			break;
		case LEVEL_2:
			phase = Phase.LEVEL_2_WIN;
			timer = 270;
			player.victoryState();
			colourspace.clear();
			break;
		case LEVEL_2_WIN:
			phase = Phase.LEVEL_3_TIP;
			resetLevel();
			generateLevelThree();
			break;
		case LEVEL_3_TIP:
			phase = Phase.LEVEL_3;
			player.enableInput();
			colourspace.form();
			startEffect = new StartEffect(3);
			break;
		case LEVEL_3:
			phase = Phase.LEVEL_3_WIN;
			timer = 270;
			player.victoryState();
			colourspace.clear();
			break;
		case LEVEL_3_WIN:
			phase = Phase.LEVEL_4_TIP;
			resetLevel();
			generateLevelFour();
			break;
		case LEVEL_4_TIP:
			phase = Phase.LEVEL_4;
			player.enableInput();
			colourspace.form();
			startEffect = new StartEffect(4);
			break;
		case LEVEL_4:
			phase = Phase.LEVEL_4_WIN;
			timer = 270;
			player.victoryState();
			colourspace.clear();
			break;
		case LEVEL_4_WIN:
			phase = Phase.LEVEL_5_TIP;
			isStageFive = true;
			resetLevel();
			break;
		case LEVEL_5_TIP:
			phase = Phase.LEVEL_5_INTRO;
			theWitch = new Witch(Direction.LEFT, Action.WALK, 800, player.getX(), Enemy.NOCOLOUR);
			allEnemies.add(theWitch);
			theWitch.canCast(true);
			timer = 180; 
			break;
		case LEVEL_5_INTRO:
			phase = Phase.LEVEL_5A1;
			player.enableInput();
			generateLevelFiveA();
			colourspace.form();
			startEffect = new StartEffect(5);
			break;
		case LEVEL_5A1:
			phase = Phase.LEVEL_5A2;
			player.victoryState();
			timer = 270;
			colourspace.clear();
			theWitch.canCast(false);
			break;
		case LEVEL_5A2:
			phase = Phase.LEVEL_5A3;
			theWitch.knockDown();
			player.enableInput();
			player.increaseHealth(2);
			break;
		case LEVEL_5A3:
			phase = Phase.LEVEL_5B1;
			theWitch.canCast(true);
			generateLevelFiveB();
			break;
		case LEVEL_5B1:
			phase = Phase.LEVEL_5B2;
			player.victoryState();
			timer = 270;
			colourspace.clear();
			theWitch.canCast(false);
			break;
		case LEVEL_5B2:
			phase = Phase.LEVEL_5B3;
			theWitch.knockDown();
			player.enableInput();
			player.increaseHealth(2);
			break;
		case LEVEL_5B3:
			phase = Phase.LEVEL_5C1;
			theWitch.canCast(true);
			generateLevelFiveC();
			break;
		case LEVEL_5C1:
			phase = Phase.LEVEL_5C2;
			player.victoryState();
			timer = 270;
			colourspace.clear();
			theWitch.canCast(false);
			break;
		case LEVEL_5C2:
			phase = Phase.LEVEL_5C3;
			theWitch.knockDown();
			player.enableInput();
			break;
		case LEVEL_5C3:
			phase = Phase.LEVEL_5_WIN;
			timer = 300;
			player.disableInput();
			break;
		case LEVEL_5_WIN:
			phase = Phase.VICTORY;
			break;
		case VICTORY:
			phase = Phase.TITLE_SCREEN;
			break;
		case FAILING:
			phase = Phase.GAME_OVER;
			break;
		case GAME_OVER:
			phase = Phase.TITLE_SCREEN;
			break; 
		default:
			break;
		}
	}

	/**
	 * This method is called whenever a key is pressed. If the Level is
	 * currently set to a phase that requires key input to continue, the next
	 * phase is called
	 */
	public void inputRecieved(int keyCode) {
		switch (phase) {
		case TITLE_SCREEN:
			if (keyCode == KeyEvent.VK_Z) {
				nextPhase();
				break;
			}
			if (keyCode == KeyEvent.VK_X) {
				phase = Phase.HOW_TO_PLAY;
				break;
			}
			if (keyCode == KeyEvent.VK_I) {
				invincibilityCheat = ! invincibilityCheat;
			}
			break;
		case HOW_TO_PLAY:
		case LEVEL_1_TIP:
		case LEVEL_2_TIP:
		case LEVEL_3_TIP:
		case LEVEL_4_TIP:
		case LEVEL_5_TIP:
		case VICTORY:
		case GAME_OVER:
			nextPhase();
		default:
			break;
		}
	}

	/**
	 * Calls the Screen class with the number of a notification to draw
	 * 
	 * @param screen
	 *            The Screen object, so that it can be called
	 * @param tipNumber
	 *            A number that associates with a tip from the sprite sheet
	 */
	private void renderTip(Screen screen, int tipNumber) {
		screen.drawTip(tipNumber);
	}

	/**
	 * Sets up the first level with horrifying porcupines
	 */
	private void generateLevelOne() {
		colourspace = new Colourspace();
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 500, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 4700, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 4000, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 2000, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 1000, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 1600, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 2800, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 3200, player.getX(), Enemy.BLUE));
	}

	/**
	 * Sets up the second level with super-strong hornets
	 */
	private void generateLevelTwo() {
		colourspace = new Colourspace();
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 3900, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 1900, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 400, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 4700, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 2500, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 900, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 3100, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 3300, player.getX(), Enemy.BLUE));
		
	}
	
	/**
	 * Sets up the third level with mind-blowing Scorpions
	 */
	private void generateLevelThree() {
		colourspace = new Colourspace();
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 600, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 4500, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 3200, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 2800, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 4000, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 1100, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 1300, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 3900, player.getX(), Enemy.BLUE));
	}
	
	/**
	 * Sets up the fourth level with a pantaloon-polluting golem.
	 */
	private void generateLevelFour() {
		colourspace = new Colourspace();
		allEnemies.add(new Golem(Direction.RIGHT, Action.WALK, 4800, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 700, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 3200, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 1500, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 2900, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 1100, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 2000, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 2600, player.getX(), Enemy.BLUE));
	}
	
	/**
	 * Sets up the first part of the fifth level with the big scary witch. 
	 */
	private void generateLevelFiveA() {
		colourspace = new Colourspace();
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 500, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 2000, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 4700, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 1300, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 2500, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 3700, player.getX(), Enemy.BLUE));
	}
	
	/**
	 * Sets up the second part of the fifth level, because the Witch just won't stay down.
	 */
	private void generateLevelFiveB() {
		colourspace.form();
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 4000, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 2000, player.getX(), Enemy.BLUE));
		allEnemies.add(new Porcupine(Direction.LEFT, Action.WALK, 1100, player.getX(), Enemy.RED));
		allEnemies.add(new Porcupine(Direction.RIGHT, Action.WALK, 4600, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 800, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 1600, player.getX(), Enemy.BLUE));
		allEnemies.add(new Hornet(Direction.LEFT, Action.WALK, 3100, player.getX(), Enemy.RED));
		allEnemies.add(new Hornet(Direction.RIGHT, Action.WALK, 4200, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 500, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 2600, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 3500, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 4800, player.getX(), Enemy.BLUE));
	}
	
	/**
	 * Sets up the final part of the fifth level. Believe in yourself, and finish her!
	 */
	private void generateLevelFiveC() {
		colourspace.form();
		allEnemies.add(new Golem(Direction.RIGHT, Action.WALK, 4600, player.getX(), Enemy.RED));
		allEnemies.add(new Golem(Direction.LEFT, Action.WALK, 400, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 2000, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 2400, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 2800, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 3200, player.getX(), Enemy.BLUE));
		allEnemies.add(new Scorpion(Direction.LEFT, Action.WALK, 3600, player.getX(), Enemy.RED));
		allEnemies.add(new Scorpion(Direction.RIGHT, Action.WALK, 4000, player.getX(), Enemy.BLUE));
	}

	/**
	 * Calls the update method of all Entities using loops. Various checks are
	 * done for collision amongst all entities. A secondary outOfBound hit box
	 * is created for a special case that occurs at position 0 or 5026. Level
	 * complete, or game over is checked for at the end
	 */
	private void updateLevel() {
		player.update();
		
		if (startEffect != null) {
			startEffect.update();
			if (startEffect.getSpeed() < -30) {
				startEffect = null;
			}
		}

		double playerLocation = player.getRotation();
		playerLocationDelta = playerLocation - lastPlayerLocation;
		lastPlayerLocation = playerLocation;

		stage.update(playerLocationDelta);
		colourspace.update();
		
		switch (entityToAdd) {
		case NONE:
			break;
		case FIREBALL:
			allEnemies.add(new Fireball(Direction.LEFT, Action.WALK, 400, player.getX(), Enemy.NOCOLOUR));
			entityToAdd = NONE;
			break;
		case LIGHTNING:
			allEnemies.add(new Lightning(Direction.LEFT, Action.IDLE, 0, player.getX(), Enemy.NOCOLOUR));
			entityToAdd = NONE;
			break;
		default:
			break;
				
		}

		ArrayList<Enemy> enemiesToBeUpdated = new ArrayList<>(allEnemies);
		ArrayList<Enemy> enemiesToRemove = new ArrayList<>();

		for (Enemy anEnemy : allEnemies) {
			if (anEnemy.isDead()) {
				enemiesToRemove.add(anEnemy);
				continue;
			}
			anEnemy.update(playerLocationDelta);
			enemiesToBeUpdated.remove(anEnemy);

			Rectangle enemyHitBox = anEnemy.getHitBox();
			checkCollisions(enemyHitBox, anEnemy, enemiesToRemove, enemiesToBeUpdated);
			Rectangle outOfBoundsBox = getOutOfBoundsBox(enemyHitBox);
			if (outOfBoundsBox != null) {
				checkCollisions(outOfBoundsBox, anEnemy, enemiesToRemove, enemiesToBeUpdated);
			}

		}

		if (player.isDead()) {
			phase = Phase.FAILING;
			timer = 127;
		}

		for (Enemy enemyToRemove : enemiesToRemove) {
			allEnemies.remove(enemyToRemove);
		}

		if (allEnemies.isEmpty()) {
			switch (phase) {
			case LEVEL_1:
			case LEVEL_2:
			case LEVEL_3:
			case LEVEL_4:
			case LEVEL_5C3:
				nextPhase();
				break;
			default:
				break;
			}
		}
		
		if (allEnemies.size() == 1 && allEnemies.get(0).getName() == "Witch") {
			switch (phase) {
			case LEVEL_5A1:
			case LEVEL_5B1:
			case LEVEL_5C1:
				nextPhase();
				break;
			case LEVEL_5A3:
			case LEVEL_5B3:
				if (timer == -1 && (theWitch.getHealth() == 14 || theWitch.getHealth() == 7)) {
					timer = 90;
				}
			default:
				break;
			}
		}

	}

	/**
	 * This method tests a hit box against all other entities on the level to
	 * see if an intersection has occurred. This is a separate method because it
	 * may need to be called twice in an update due to the outOfBounds logic
	 * 
	 * @param hitBox
	 *            The hit box currently being checked
	 * @param anEnemy
	 *            The enemy that the hit box belongs to
	 * @param enemiesToRemove
	 *            A list for storing enemies if they have been defeated
	 * @param enemiesToBeUpdated
	 *            All enemies that have not yet checked for intersection
	 */
	private void checkCollisions(Rectangle hitBox, Enemy anEnemy, ArrayList<Enemy> enemiesToRemove, ArrayList<Enemy> enemiesToBeUpdated) {
		Rectangle playerHitBox = player.getHitBox();
		if (hitBox.intersects(playerHitBox)) {
			if (!player.playerHit(anEnemy, anEnemy.getX())) {
				anEnemy.reverse();
			}
		}
		Rectangle playerOutOfBoundsBox = getOutOfBoundsBox(playerHitBox);
		if (playerOutOfBoundsBox != null) {
			if (hitBox.intersects(playerOutOfBoundsBox)) {
				if (!player.playerHit(anEnemy, anEnemy.getX())) {
					anEnemy.reverse();
				}
			}
		}

		if (player.getActionState() == Action.ATTACK) {
			Rectangle playerAttackBox = player.getAttackBox();
			if (hitBox.intersects(playerAttackBox)) {
				anEnemy.attackedByPlayer(player.getDirection());
			}
			Rectangle playerAttackOutOfBoundsBox = getOutOfBoundsBox(playerAttackBox);
			if (playerAttackOutOfBoundsBox != null) {
				if (hitBox.intersects(playerAttackOutOfBoundsBox)) {
					anEnemy.attackedByPlayer(player.getDirection());
				}
			}
		}

		for (Enemy otherEnemy : enemiesToBeUpdated) {
			Rectangle otherEnemyHitBox = otherEnemy.getHitBox();
			if (hitBox.intersects(otherEnemyHitBox)) {
				anEnemy.reverse();
				otherEnemy.reverse();
			}
			Rectangle otherEnemyOutOfBoundsBox = getOutOfBoundsBox(otherEnemyHitBox);
			if (otherEnemyOutOfBoundsBox != null) {
				if (hitBox.intersects(otherEnemyOutOfBoundsBox)) {
					anEnemy.reverse();
					otherEnemy.reverse();
				}
			}
		}
	}

	/**
	 * Generates the outOfBounds box necessary for determining if a collision
	 * has occurred near the 0 and 5026 positions. A new rectangle is generated
	 * based on the intersection with these out of bounds areas. The rectangle
	 * is relocated and returned. If no intersection occurs then it returns null
	 * 
	 * @param hitBox
	 *            The hit box that will check for out of bounds intersection
	 * @return The intersection rectangle, or null if no intersection
	 */
	private Rectangle getOutOfBoundsBox(Rectangle hitBox) {
		Rectangle overlap = null;
		
		if (hitBox.intersects(Stage.POSITIVE_BOUNDS)) {
			overlap = hitBox.intersection(Stage.POSITIVE_BOUNDS);
			overlap.setLocation(0, (int) overlap.getY());
		}

		if (hitBox.intersects(Stage.NEGATIVE_BOUNDS)) {
			overlap = hitBox.intersection(Stage.NEGATIVE_BOUNDS);
			overlap.setLocation((int) (Stage.CIRCUMFERENCE - overlap.getWidth()), (int) overlap.getY());
		}

		return overlap;
	}

	/**
	 * Renders the screen, health bar, player and runs a for each to render all
	 * of the enemies by passing each Entity the Screen object
	 * 
	 * @param screen
	 *            The Screen Object for passing to the Entities render methods
	 */
	private void renderLevel(Screen screen) {
		stage.render(screen, isStageFive);
		screen.drawColourspace(colourspace);
		screen.drawHealthBar(player.getHealth());
		
		for (Enemy anEnemy : allEnemies) {
			anEnemy.render(screen);
		}

		player.render(screen);
		screen.drawMinimap(allEnemies);
		if (startEffect != null) {
			startEffect.render(screen);
		}
		
	}
	
	/**
	 * Called by the Witch enemy when she casts a spell. It identifies
	 * the spell being cast an queues it for creation in the next update.
	 * 
	 * @param spell the spell to be cast.
	 */
	public static void witchCastSpell(int spell) {
		if (spell == Witch.FIREBALL) {
			entityToAdd = FIREBALL;
		}
		
		if (spell == Witch.LIGHTNING) {
			entityToAdd = LIGHTNING;
		}
		
	}


}