package roundWorld;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import roundWorld.graphics.Screen;
import roundWorld.stage.Level;
import roundWorld.input.InputHandler;

/**
 * The main game instance. Manages the canvas, JFrame, and game engine
 * 
 * @author Andrew Black
 * 
 */
public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 480;
	public static final Dimension DIMENSION = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
	public static final String NAME = "Knight of the Round World";

	/**
	 * The JFrame window. Static so that it can be modified with statistics info
	 */
	private static JFrame frame;
	/**
	 * Main game thread
	 */
	private Thread thread;
	/**
	 * Listener for keyboard input
	 */
	private InputHandler input;
	/**
	 * Level class drives the game logic
	 */
	private Level level;
	/**
	 * Screen class holds and draws all graphics
	 */
	private Screen screen;
	/**
	 * Running boolean controls the game loop
	 */
	private boolean running;

	/**
	 * Constructor adds the Game class to the Thread and InputHandler. It passes
	 * the InputHandler object to Level and instantiates the Screen
	 */
	public Game() {
		setSize(DIMENSION);
		thread = new Thread(this);
		input = new InputHandler(this);
		level = new Level(input);
		screen = new Screen();
		running = false;
	}

	/**
	 * Starts the game loop as well as the main thread
	 */
	public void start() {
		running = true;
		thread.start();
	}

	/**
	 * Contains the main game loop which runs until the application is closed.
	 * It keeps track of when the game should be updated, or rendered and calls
	 * those methods when necessary. Statistics for update and frame rate are
	 * tracked and displayed to the window frame
	 */
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = false;

			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			if (shouldRender) {
				frames++;
				render();
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				if (frame != null) {
					frame.setTitle(NAME + " | " + ticks + " ticks, " + frames + " frames");
				}
				frames = 0;
				ticks = 0;
			}
		}
	}

	/**
	 * Calls the update method of Level, so the game can be updated
	 */
	public void tick() {
		level.update();
	}

	/**
	 * Creates a buffer strategy for rendering graphics, and passes the Screen
	 * class to Level so that it can be updated with the current frame. Screens
	 * image variable is rendered to the buffered strategy, which is then
	 * disposed so the next frame can render
	 */
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics graphics = bs.getDrawGraphics();

		level.render(screen);
		
		graphics.drawImage(screen.getImage(), 0, 0, getWidth(), getHeight(), null);
		graphics.dispose();
		bs.show();
	}

	/**
	 * Main method for instantiating the game instance and setting up the JFrame
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();
		frame = new JFrame(NAME);

		frame.add(game);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.start();
	}

}