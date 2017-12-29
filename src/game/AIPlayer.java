package game;

import java.util.ArrayList;

import state.GameState;

/**
 * A simple AI Player that moves towards the nearest Ball.
 * 
 * @author Dan Bryce
 */
public class AIPlayer extends Player {

	private ArrayList<Entity> entities;

	/**
	 * Constructs an AIPlayer.
	 * @param id Player number (0 or 1), determines x-position.
	 * @param entities The list of Entities in the game.
	 */
	public AIPlayer(int id, ArrayList<Entity> entities) {
		super(id);
		this.entities = entities;
	}
	
	/**
	 * Processes the AI.
	 */
	@Override
	public void tick() {
		super.tick();
		
		Entity nearestBall = null;
		int shortestDist = Integer.MAX_VALUE;
		int px = (int) (x1 + WIDTH/2);
		int py = (int) (y1 + HEIGHT/2);
		
		// Find nearest Ball
		for (Entity entity : entities){
			if (!(entity instanceof Ball)) continue;
			
			// Calculate Entity centre
			int bx = (int) (entity.x1 + Ball.WIDTH/2);
			int by = (int) (entity.y1 + Ball.HEIGHT/2);
			
			// Calculate distance
			int dx = px - bx;
			int dy = py - by;
			int dist = (dx*dx) + (dy*dy);
			
			// Remember nearest
			if (dist < shortestDist){
				shortestDist = dist;
				nearestBall = entity;
			}
		}
		
		// Move towards nearest Ball
		if (nearestBall.y1 < y1){
			dir = GameState.DIR_UP;
		} else if (nearestBall.y2 > y2) {
			dir = GameState.DIR_DOWN;
		} else {
			dir = GameState.DIR_NONE;
		}
	}
	
}
