package state;

import game.Pong;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * Class representing the ball input screen.
 * 
 * @author Dan Bryce
 */
public class MenuState extends State {

	private static final int FONT_SIZE = 8;
	
	private String input = "";

	/**
	 * Constructs a MenuState.
	 * @param pong
	 */
	public MenuState(Pong pong) {
		super(pong);
	}

	/**
	 * Draws the ball prompt and current user input.
	 */
	@Override
	public void draw(Graphics2D g) {

		String string = "Enter the number of balls";
		int stringWidth = getStringWidth(string, FONT_SIZE);
		int stringHeight = getStringHeight(string, FONT_SIZE);
		int drawX = (screenWidth/2) - (stringWidth/2);
		int drawY = (screenHeight/2) - 50 - stringHeight;
		drawString(g, string, drawX, drawY, FONT_SIZE);

		stringWidth = getStringWidth(input, FONT_SIZE);
		drawX = (screenWidth/2) - (stringWidth/2);
		drawY = (screenHeight/2) + 50;
		drawString(g, input, drawX, drawY, FONT_SIZE);
	}
	
	/**
	 * KeyListener method that handles user input.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		char c = e.getKeyChar();
		
		// User typed a number
		if (Character.isDigit(c)){
			if (c == '0') return;
			input = Character.toString(c);
		
		// User pressed backspace
		} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			input = input.substring(0, input.length() - 1);
		
		// User pressed enter
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER){
			if (input.equals("")) return;
			int balls = Integer.parseInt(input);
			pong.changeState(new GameState(pong, balls));
		}
	}

}