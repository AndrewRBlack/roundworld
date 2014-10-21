package roundWorld.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import roundWorld.entity.player.Player;
import roundWorld.stage.Level;
import roundWorld.Game;

/**
 * Listens for keyboard input and passes it to the appropriate classes
 * 
 * @author Andrew Black
 * 
 */
public class InputHandler implements KeyListener {
	/**
	 * An array that tracks keys which have been pressed, and disables them
	 * until it has been released to prevent key repeating
	 */
	private boolean[] keyDisabled;
	/**
	 * Reference to the Player object so player actions can be handled
	 */
	Player player;
	/**
	 * Reference to the Level object so it can receive key events for UI
	 * notifications
	 */
	Level level;

	/**
	 * Constructor attaches the InputHandler class to the game instance and
	 * grants it focus so that the JFrame can listen for key events
	 * 
	 * @param gameInstance
	 *            the game instance containing the JFrame
	 */
	public InputHandler(Game gameInstance) {
		keyDisabled = new boolean[1000];
		player = null;
		level = null;
		gameInstance.addKeyListener(this);
		gameInstance.setFocusable(true);
	}

	/**
	 * Allows the Level class to pass the current Player object to InputHandler
	 * 
	 * @param inPlayer
	 *            the Player object
	 */
	public void setPlayer(Player inPlayer) {
		player = inPlayer;
	}

	/**
	 * Allows the Level object to pass itself to InputHandler
	 * 
	 * @param inLevel
	 *            the Level object
	 */
	public void setLevel(Level inLevel) {
		level = inLevel;
	}

	/**
	 * Handles all key presses and alerts the Level class. Event code is passed
	 * the Player class only if that key has not been disabled. Keys are then
	 * set to disabled to prevent repeating
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		

		int keyCode = e.getKeyCode();
		
		

		if (keyDisabled[keyCode]) {
			return;
		}
		
		level.inputRecieved(keyCode);

		player.keyPressed(keyCode);

		keyDisabled[keyCode] = true;
	}

	/**
	 * Handles all key release events. Key code is retrieved, the key is
	 * re-enabled, and then passed to the Player class
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		keyDisabled[keyCode] = false;

		player.keyReleased(keyCode);
	}

	/**
	 * Method not used
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

}