package game;

import java.awt.Graphics2D;

/**
 * Class representing an Entity, i.e. an object in the game.
 * 
 * @author Dan Bryce
 */
public abstract class Entity {

	protected double x1, y1, x2, y2;

	/**
	 * Draws this Entity in place.
	 * @param g
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Updates the Entity.
	 */
	public abstract void tick();
	
}
