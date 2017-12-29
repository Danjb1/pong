package state;

import game.Pong;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Base class used to represent a state of the game.
 * 
 * Includes various helper methods, i.e. for drawing.
 * 
 * @author Dan Bryce
 */
public abstract class State {

	protected Pong pong;
	protected BufferedImage font;
	protected HashMap<Character, Integer> fontMap;
	protected int screenWidth, screenHeight;

	/**
	 * Constructs a State.
	 * @param pong Handle to the Pong instance.
	 */
	public State(Pong pong) {
		this.pong = pong;
		this.font = pong.getFont();
		this.fontMap = pong.getFontMap();
	}

	/**
	 * Updates the State.
	 */
	public void tick(){
	}
	
	/**
	 * Draws the State.
	 * @param g
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Handles key presses.
	 * @param e
	 */
	public void keyPressed(KeyEvent e){
	}
	
	/**
	 * Handles keys being released.
	 * @param e
	 */
	public void keyReleased(KeyEvent e){
	}

	/**
	 * Gets the width of the given String if drawn at the given size.
	 * @param string String to measure.
	 * @param fontSize Font size, where 1 = smallest, 2 = double size, etc.
	 * @return
	 */
	protected static int getStringWidth(String string, int fontSize) {
		int charWidth = string.length() * Pong.FONT_CHAR_SIZE;
		int gapWidth = string.length() * Pong.FONT_GAP_SIZE;
		return (charWidth + gapWidth) * fontSize;
	}

	/**
	 * Gets the height of the given String if drawn at the given size.
	 * @param string String to measure.
	 * @param fontSize Font size, where 1 = smallest, 2 = double size, etc.
	 * @return
	 */
	protected static int getStringHeight(String string, int fontSize) {
		return Pong.FONT_CHAR_SIZE * fontSize;
	}

	/**
	 * Draws the given String at the given position and size.
	 * @param g
	 * @param string String to draw
	 * @param drawX
	 * @param drawY
	 * @param fontSize Font size, where 1 = smallest, 2 = double size, etc.
	 */
	protected void drawString(Graphics2D g, String string, 
			int drawX, int drawY, int fontSize) {

		int charSize = Pong.FONT_CHAR_SIZE * fontSize;
		int gapWidth = Pong.FONT_GAP_SIZE * fontSize;
		
		char[] chars = string.toLowerCase().toCharArray();
		for (int i = 0; i < chars.length; i++){
			
			// Skip spaces
			if (chars[i] == ' ') continue;
			
			int index = fontMap.get(chars[i]);
			
			int dx1 = drawX + (i * (charSize + gapWidth));
			int dy1 = drawY;
			int dx2 = dx1 + charSize;
			int dy2 = dy1 + charSize;
			int sx1 = index * Pong.FONT_CHAR_SIZE;
			int sy1 = 0;
			int sx2 = (index + 1) * Pong.FONT_CHAR_SIZE;
			int sy2 = Pong.FONT_CHAR_SIZE;

			g.drawImage(font, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		}
	}

	/**
	 * Changes all visible pixels in an image to the given Color.
	 * @param img
	 * @param color
	 * @return
	 */
	protected BufferedImage colourImage(BufferedImage img, Color color) {
		
		BufferedImage colouredImg = img;
		int width = img.getWidth();
		int height = img.getHeight();
		
		// Strip alpha value from desired colour
		int newRGB = color.getRGB() & 0x00ffffff;
		
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				
				// Keep only alpha value of each pixel
				int rgb = img.getRGB(x, y);
				int alpha = rgb & 0xff000000;
				
				// Apply new colour to existing alpha values
				rgb = alpha | newRGB;
				colouredImg.setRGB(x, y, rgb);				
			}
		}
		
		return colouredImg;
	}

	/**
	 * Responds to the Screen changing in size.
	 * @param width
	 * @param height
	 */
	public void sizeChanged(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

}
