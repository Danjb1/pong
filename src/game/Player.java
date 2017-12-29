package game;

import java.awt.Color;
import java.awt.Graphics2D;

import state.GameState;

/**
 * Class representing a Player's paddle.
 * 
 * @author Dan Bryce
 */
public class Player extends Entity {

	public static final int WIDTH = 32;
	public static final int HEIGHT = 128;
	public static final double BALL_ANGLE_MULTIPLIER = 3.0;
	
	private static final double ACCELERATION = 0.75;
	private static final double DECELERATION = 0.95;
	private static final double BOUNCE_SPEED_MULTIPLIER = 0.9;
	private static final double MIN_SPEED = 0.001;
	private static final double MAX_SPEED = 7.5;
	
	protected int dir;
	protected int score;
	protected double speed;

	/**
	 * Constructs a Player with the given number.
	 * @param id Player number (0 or 1), determines x-position.
	 */
	public Player(int id) {
		this.x1 = id * (GameState.GAME_WIDTH - WIDTH);
		this.y1 = GameState.GAME_HEIGHT/2 - HEIGHT/2;
		this.x2 = x1 + WIDTH;
		this.y2 = y1 + HEIGHT;
	}

	/**
	 * Draws this Player.
	 */
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect((int) x1, (int) y1, WIDTH, HEIGHT);
	}

	/**
	 * Setter for direction.
	 * @param dir Constant from GameState.
	 */
	public void setDir(int dir) {
		this.dir = dir;
	}

	/**
	 * Updates the Player's speed and position.
	 */
	@Override
	public void tick() {
		
		// Accelerate according to key pressed
		speed += (ACCELERATION * dir);
		
		// Cap speed
		if (speed > MAX_SPEED){
			speed = MAX_SPEED;
		} else if (speed < -MAX_SPEED){
			speed = -MAX_SPEED;
		}
		
		// Decelerate if no direction pressed
		if (dir == GameState.DIR_NONE){
			speed *= DECELERATION;
			if (Math.abs(speed) < MIN_SPEED) speed = 0;
		}

		translate(speed);
	}

	/**
	 * Moves the Player according to his speed, and handles collision.
	 * @param dy
	 */
	private void translate(double dy) {
		
		// Update position
		y1 += dy;
		y2 = y1 + HEIGHT;

		// Check bounds
		if (y1 < 0){
			y1 = 0;
			y2 = y1 + HEIGHT;
			bounce();
		} else if (y2 > GameState.GAME_HEIGHT){
			y1 = GameState.GAME_HEIGHT - HEIGHT;
			y2 = y1 + HEIGHT;
			bounce();
		}
	}

	/**
	 * Processes a bounce after the paddle collides with a wall.
	 */
	private void bounce() {
		speed = -speed * BOUNCE_SPEED_MULTIPLIER;
	}

	/**
	 * Gets the Player's score as a String.
	 * @return
	 */
	public String getScoreAsString() {
		return String.valueOf(score);
	}

	/**
	 * Modifies the Player's score by the given amount.
	 * @param i
	 */
	public void modScore(int i) {
		score += i;
	}

	/**
	 * Getter for speed (in the y-direction).
	 * @return Speed, where negative means up and positive means down.
	 */
	public double getSpeed() {
		return speed;
	}

}
