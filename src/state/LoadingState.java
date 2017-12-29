package state;

import game.Pong;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * State representing the loading screen.
 * 
 * @author Dan Bryce
 */
public class LoadingState extends State {

	private static final int FONT_SIZE = 8;
	
	/**
	 * Constructs the LoadingState.
	 * @param pong
	 */
	public LoadingState(Pong pong) {
		super(pong);
		
		font = colourImage(font, Color.WHITE);
	}

	/**
	 * Draws the loading screen.
	 */
	@Override
	public void draw(Graphics2D g) {
		
		String string = "Loading";
		int stringWidth = getStringWidth(string, FONT_SIZE);
		int stringHeight = getStringHeight(string, FONT_SIZE);
		int drawX = (screenWidth/2) - (stringWidth/2);
		int drawY = (screenHeight/2) - (stringHeight/2);
		
		drawString(g, string, drawX, drawY, FONT_SIZE);
	}
	

}
