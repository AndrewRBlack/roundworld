package roundWorld.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import roundWorld.Game;
import roundWorld.entity.enemy.Enemy;
import roundWorld.entity.enemy.Fireball;
import roundWorld.entity.enemy.Hornet;
import roundWorld.entity.enemy.Lightning;
import roundWorld.entity.enemy.Porcupine;
import roundWorld.entity.enemy.Scorpion;
import roundWorld.entity.enemy.Golem;
import roundWorld.entity.enemy.Witch;
import roundWorld.entity.player.Player;
import roundWorld.stage.Colourspace;
import roundWorld.stage.Stage;

/**
 * Screen is the graphics engine. It generates a matrix for all sprite sheets
 * and draws all images as called by the various game entities
 * 
 * @author Andrew Black, Andrew Aitken
 * 
 */
public class Screen {
	/**
	 * Constants for keeping track of which sprite matrix to access, based on
	 * entity type
	 */
	public static final int COLOURSPACE = 0, TIP = 1, HEALTH = 2, START_EFFECT = 3,
							PLAYER = 4, PORCUPINE = 5, HORNET = 6, 
							SCORPION = 7, GOLEM = 8, WITCH = 9,
							FIREBALL = 10, LIGHTNING = 11;
	/**
	 * Constants to track the left and right versions of each sprite matrix
	 */
	public static final int LEFT = 1, RIGHT = 0;

	/**
	 * Primary buffered image, and non-sprite images
	 */
	private BufferedImage image, titleImage, howToPlayImage, stageImage, stageFiveImage;
	/**
	 * The base matrix for holding all other sprite matrices. Assigned by entity
	 * type
	 */
	private BufferedImage[][][][] allSprites;
	/**
	 * Graphics 2D object extracted from the image variable. All images are
	 * drawn to this variable
	 */
	private Graphics2D g2d;
	
	/**
	 * The coordinates of the center of the minimap. Used to place
	 * each enemy icon around the edge of the minimap.
	 */
	private final int minimapCenterX = (Game.WINDOW_WIDTH / 2), minimapCenterY = 25;

	/**
	 * Constructor instantiates the main image, and loads all images and
	 * matrices that will be used
	 */
	public Screen() {
		image = new BufferedImage(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		titleImage = loadImage("/res/title.png");
		howToPlayImage = loadImage("/res/howto.png");
		stageImage = loadImage("/res/Stage.png");
		stageFiveImage = loadImage("/res/StageFive.png");

		allSprites = new BufferedImage[12][][][];
		
		allSprites[0] = createSpriteMatrix(1, 6, 2, 800, 141, "/res/colourspace.png");	
		allSprites[1] = createSpriteMatrix(1, 1, 7, 800, 480, "/res/tips.png");
		allSprites[2] = createSpriteMatrix(1, 1, 6, 200, 50, "/res/health.png");
		allSprites[3] = createSpriteMatrix(1, 1, 6, 183, 31, "/res/startEffect.png");
		
		allSprites[4] = createSpriteMatrix(2, 6, 6, Player.SPRITE_WIDTH, Player.SPRITE_HEIGHT, "/res/PCSprites.png");
		allSprites[5] = createSpriteMatrix(2, 3, 2, Porcupine.SPRITE_WIDTH, Porcupine.SPRITE_HEIGHT, "/res/Porcupine.png");
		allSprites[6] = createSpriteMatrix(2, 3, 2, Hornet.SPRITE_WIDTH, Hornet.SPRITE_HEIGHT, "/res/Hornet.png");
		allSprites[7] = createSpriteMatrix(2, 4, 4, Scorpion.SPRITE_WIDTH, Scorpion.SPRITE_HEIGHT, "/res/Scorpion.png");
		allSprites[8] = createSpriteMatrix(2, 4, 10, Golem.SPRITE_WIDTH, Golem.SPRITE_HEIGHT, "/res/Golem.png");
		allSprites[9] = createSpriteMatrix(2, 6, 4, Witch.SPRITE_WIDTH, Witch.SPRITE_HEIGHT, "/res/Witch.png");
		allSprites[10] = createSpriteMatrix(2, 5, 1, Fireball.SPRITE_WIDTH, Fireball.SPRITE_HEIGHT, "/res/Fireball.png");
		allSprites[11]= createSpriteMatrix(2, 4, 4, Lightning.SPRITE_WIDTH, Lightning.SPRITE_HEIGHT, "/res/Lightning.png");

		g2d = image.createGraphics();
		g2d.setColor(Color.YELLOW);
		g2d.setStroke(new BasicStroke(2));
	}

	/**
	 * Returns the image to be rendered as the current frame
	 * 
	 * @return a BufferedImage containing all drawn graphics
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Called at the beginning of the game to display game information
	 */
	public void drawTitleScreen() {
		g2d.drawImage(titleImage, 0, 0, null);
	}
	
	/**
	 * Displays a simple notification at the top-left corner of the Title Screen
	 * to indicate that invincibility mode has been activated.
	 */
	public void drawCheatNotice() {
		g2d.drawString("Invincibility Cheat Enabled, you bad boy you.", 0, 12);
	}
	
	/**
	 * Displays the how-to-play image.
	 */
	public void drawHowToPlay() {
		g2d.drawImage(howToPlayImage, 0, 0, null);
	}

	/**
	 * Draws a rotated stage graphic to the screen. Repositions the stage, so
	 * its bottom is in the frame and sets a centered anchor point and rotates
	 * the stage graphic
	 * 
	 * @param stageRotation
	 *            the current angle that the stage will be rotated
	 */
	public void drawStage(double stageRotation, boolean isStageFive) {
		AffineTransform transformer = new AffineTransform();

		transformer.translate(Stage.SHIFT_INTO_FRAME_X, Stage.SHIFT_INTO_FRAME_Y);
		transformer.rotate(stageRotation, Stage.CENTER_OF_IMAGE, Stage.CENTER_OF_IMAGE);

		g2d.drawImage((isStageFive ? stageFiveImage : stageImage), transformer, null);
	}
	
	/**
	 * Gets the current state of the colourspace, and draws the
	 * corresponding sprite.
	 */
	public void drawColourspace(Colourspace colourspace) {
		int state = colourspace.getState();
		int column = 0;
		int row = 0;
		switch (state) {
			case Colourspace.INACTIVE:
				return;
			case Colourspace.ACTIVE:
				break;
			case Colourspace.CLEARING:
				row = 0;
				column = colourspace.getFrame();
				break;
			case Colourspace.FORMING:
				row = 1;
				column = colourspace.getFrame();
		}
		
		BufferedImage sprite = allSprites[COLOURSPACE][0][column][row];
		int x = 0;
		int y = 260;
		
		g2d.drawImage(sprite, x, y, null);
	}
	
	/**
	 * Draw two images, one that displays the current stage, and one that
	 * says "Start!".
	 * 
	 * @param stage The stage to display.
	 * @param stageX The x-coordinate of the stage image.
	 * @param stageY The y-coordinate of the stage image.
	 * @param startX The x-coordinate of the start image.
	 * @param startY The y-coordinate of the start image.
	 */
	public void drawStartEffect(int stage, int stageX, int stageY, int startX, int startY) {
		BufferedImage sprite = allSprites[START_EFFECT][0][0][stage];
		g2d.drawImage(sprite, stageX, stageY, null);
		
		sprite = allSprites[START_EFFECT][0][0][0];
		g2d.drawImage(sprite, startX, startY, null);
	}
	

	/**
	 * Draws a small graphic over the game to give the player some instructions
	 * 
	 * @param tipNumber
	 *            The tip graphic in relation to its sprite sheet position
	 */
	public void drawTip(int tipNumber) {
		BufferedImage tipImage = allSprites[Screen.TIP][0][0][tipNumber - 1];

		int x = 0;
		int y = 0;

		g2d.drawImage(tipImage, x, y, null);
	}

	/**
	 * Draws a health bar to visually represent the players current health
	 * 
	 * @param health
	 *            The current health value of Player. Relates to sprite sheet
	 */
	public void drawHealthBar(int health) {
		BufferedImage healthBar = allSprites[Screen.HEALTH][0][0][health];

		int x = (Game.WINDOW_WIDTH / 2) - (healthBar.getWidth() / 2);
		int y = (Game.WINDOW_HEIGHT) - healthBar.getHeight();

		g2d.drawImage(healthBar, x, y, null);
	}
	
	/**
	 * Draws the minimap in the top-center of the screen, including
	 * dots to represent the player and enemy positions.
	 */
	public void drawMinimap(List<Enemy> allEnemies) {
		int x = minimapCenterX - 25;
		int y = minimapCenterY - 25;
		
		g2d.setColor(Color.getHSBColor(0f, 1f, 0.25f));
		g2d.fillArc(x, y, 50, 50, 90, 180);
		g2d.setColor(Color.getHSBColor(0.6f, 1f, 0.25f));
		g2d.fillArc(x, y, 50, 50, -90, 180);
		g2d.setColor(Color.YELLOW);
		
		
		//Player and enemy icons
		g2d.fillOval(minimapCenterX - 5, minimapCenterY + 15, 10, 10);
		
		for (Enemy icon : allEnemies) {
			drawMinimapEnemy(icon);
		}
		
		//Border
		g2d.setColor(Color.BLACK);
		g2d.drawOval(minimapCenterX - 25, 0, 50, 50);
		g2d.setColor(Color.YELLOW);
	}
	
	/**
	 * Called by the parent drawMinimap method.
	 * Draws a small circle on the minimap for the given enemy,
	 * to match their x position on the circle (their y position
	 * is not considered).
	 * @param anEnemy The enemy to draw.
	 */
	private void drawMinimapEnemy(Enemy anEnemy) {
		int colour = anEnemy.getColour();
		switch (colour) {
		case Enemy.RED:
			g2d.setColor(Color.RED);
			break;
		case Enemy.BLUE:
			g2d.setColor(Color.BLUE);
			break;
		default:
			if (anEnemy.getName() != "Witch") {
				return;
			}
			g2d.setColor(Color.DARK_GRAY);
			break;
		}
		
		double position = anEnemy.getDistanceFromPlayer() * Stage.RADIAN_PER_PIXEL + (Math.PI / 2);
		
		int x = (int) (minimapCenterX - 5 + (20 * Math.cos(position)));
		int y = (int) (minimapCenterY - 5 + (20 * Math.sin(position)));
		g2d.fillOval(x, y, 10, 10);
		g2d.setColor(Color.YELLOW);
	}

	/**
	 * Draws the sprite for player and enemy. Applies the appropriate
	 * transformations and rotations by utilizing constants set by the Stage and
	 * Game classes. BEWARE OF MATH!
	 * 
	 * @param type
	 *            The type of entity to be drawn
	 * @param direction
	 *            The direction that the sprite is facing
	 * @param column
	 *            The column containing the desired sprite
	 * @param row
	 *            The row containing the desired sprite
	 * @param rotation
	 *            The rotation for enemies, but this is also used to pass the
	 *            current Y coord for the Player class
	 * @param filter
	 * 			  The ARGB filter to use, if any, for the sprite.
	 */
	public void drawSprite(int type, int direction, int column, int row, double rotation, int filter) {
		BufferedImage sprite = allSprites[type][direction][column - 1][row - 1];

		if (type == PLAYER) {
			int[] oldrgb = null;
			if (filter != 0) {
				oldrgb = sprite.getRGB(0, 0, Player.SPRITE_WIDTH, Player.SPRITE_HEIGHT, null, 0, Player.SPRITE_WIDTH);
				int[] rgb = oldrgb.clone();
				
				for (int i = 0; i < rgb.length; i++) {
					if (rgb[i] > 0x01000000 || rgb[i] < 0) {
						rgb[i] = rgb[i] & 0x00ffffff;
						rgb[i] += filter * 0x02000000;
					}
				}
			
			sprite.setRGB(0, 0, sprite.getWidth(), sprite.getHeight(), rgb, 0, sprite.getWidth());
			}
			
			int x = (Game.WINDOW_WIDTH / 2) - (Player.SPRITE_WIDTH / 2);
			int y = (Game.WINDOW_HEIGHT + Stage.HEALTH_BAR_OFFSET) - Player.SPRITE_HEIGHT;
			double playerJumpAdjustment = rotation;
			int adjustedY = y + (int) playerJumpAdjustment;
			g2d.drawImage(sprite, x, adjustedY, null);
			if (filter != 0) {
				sprite.setRGB(0, 0, sprite.getWidth(), sprite.getHeight(), oldrgb, 0, sprite.getWidth());
			}
			return;
		}
		


		int width = sprite.getWidth();
		int height = sprite.getHeight();
		
		int[] oldrgb = null;
		if (filter != 0) {
			oldrgb = sprite.getRGB(0, 0, width, height, null, 0, width);
			int[] rgb = oldrgb.clone();
			int filterStartPoint = 0;
			if (type == WITCH) {
				if ((filter & 0x00ffffff) == 0x00ffff00) {
					filterStartPoint = rgb.length / 4 * 3;
				}
			}
			
			for (int i = filterStartPoint; i < rgb.length; i++) {
				if (rgb[i] > 0x01000000 || rgb[i] < 0) {
					rgb[i] = filter;
				}
			}
		
		sprite.setRGB(0, 0, sprite.getWidth(), sprite.getHeight(), rgb, 0, sprite.getWidth());
		}
		
		AffineTransform rotateSprite = new AffineTransform();
		rotateSprite.translate(Stage.RELATIVE_CENTER_X - (width / 2), Stage.RELATIVE_CENTER_Y - height);
		rotateSprite.rotate(rotation, (width / 2), height);
		AffineTransform repositionSprite = new AffineTransform();

		repositionSprite.translate(0, Stage.RADIUS);
		rotateSprite.concatenate(repositionSprite);
		

		
		
		g2d.drawImage(sprite, rotateSprite, null);
		
		if (filter != 0) {
			sprite.setRGB(0, 0, sprite.getWidth(), sprite.getHeight(), oldrgb, 0, sprite.getWidth());
		}

		
	}
	
	/**
	 * Draw two simple lines that stem from the player's sword during
	 * their victory pose, and runs along the ground up to the top.
	 * The Level class must time when to stop drawing.
	 * @param angle The angle of the line being drawn, with 90 being straight down.
	 */
	public void drawBeams(int angle) {
		int x = (int) (399 + (800 * Math.cos(Math.toRadians(angle))));
		int y = (int) (-420 + (800 * Math.sin(Math.toRadians(angle))));
		g2d.drawLine(399, 260, x, y);
		g2d.drawLine(399, 260, 800 - x, y);
		if (angle >= 267) {
			g2d.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		}
	}
	
	public void clearScreen() {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		g2d.setColor(Color.YELLOW);
	}

	/**
	 * Passed instructions for a sprite sheet to be loaded, and instructions for
	 * how to break it apart. Individual sprites are then held in a matrix which
	 * is returned
	 * 
	 * @param directions
	 *            The number of directional orientations required
	 * @param columns
	 *            The number of columns to be retrieved
	 * @param rows
	 *            The number of rows to be retrieved
	 * @param width
	 *            The width of each individual sprite
	 * @param height
	 *            The height of each individual sprite
	 * @param spriteSheetPath
	 *            The path of the sprite sheet to be loaded
	 * @return The completed matrix containing all of the individual sprites
	 */
	private BufferedImage[][][] createSpriteMatrix(int directions, int columns, int rows, int width, int height, String spriteSheetPath) {
		BufferedImage spriteSheet = loadImage(spriteSheetPath);

		BufferedImage[][][] spriteMatrix = new BufferedImage[directions][columns][rows];

		for (int i = 0; i < directions; i++) {
			for (int j = 0; j < columns; j++) {
				for (int h = 0; h < rows; h++) {
					spriteMatrix[i][j][h] = createSprite(i, j, h, width, height, spriteSheet);
				}
			}
		}

		return spriteMatrix;
	}

	/**
	 * Retrieves and returns a single sprite from a given sprite sheet.
	 * Transformation is applied if a right facing version is required
	 * 
	 * @param direction
	 *            The direction that the sprite will face
	 * @param column
	 *            The column to retrieve the sprite from
	 * @param row
	 *            The row to retrieve the sprite from
	 * @param width
	 *            The width of the desired sprite
	 * @param height
	 *            The height of the desired sprite
	 * @param spriteSheet
	 *            The sprite sheet to retrieve from
	 * @return The buffered image for the requested sprite
	 */
	private BufferedImage createSprite(int direction, int column, int row, int width, int height, BufferedImage spriteSheet) {
		BufferedImage requestedSprite = spriteSheet.getSubimage((column * width), (row * height), width, height);

		if (direction == LEFT) {
			AffineTransform changeSpriteAnchor = AffineTransform.getScaleInstance(-1, 1);
			changeSpriteAnchor.translate(-requestedSprite.getWidth(), 0);
			AffineTransformOp flipSprite = new AffineTransformOp(changeSpriteAnchor, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			requestedSprite = flipSprite.filter(requestedSprite, null);
		}

		return requestedSprite;
	}

	/**
	 * Loads the sprite sheet from a path and returns it as a buffered image
	 * 
	 * @param path
	 *            A string of the sprite sheet's file name
	 * @return The sprite sheet as a buffered image
	 */
	private BufferedImage loadImage(String path) {
		BufferedImage loadedImage = null;

		try {
			loadedImage = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return loadedImage;
	}

}