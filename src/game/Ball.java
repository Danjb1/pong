package game;

import java.awt.Color;
import java.awt.Graphics2D;

import state.GameState;

/**
 * Class representing the ball!
 * 
 * @author Dan Bryce
 */
public class Ball extends Entity {

	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	
	private static final double INITIAL_SPEED = 5.5;
	private static final double MAX_SPEED = 20.0;
	private static final double BOUNCE_SPEED_MULTIPLIER = 1.1;
	
	private double angle, speed, speedX, speedY;
	private GameState state;
	private Player p1, p2;

	/**
	 * Constructs a Ball at the given point.
	 * @param state
	 * @param centreX
	 * @param centreY
	 */
	public Ball(GameState state, int centreX, int centreY) {
		
		this.state = state;
		this.p1 = state.getPlayer(0);
		this.p2 = state.getPlayer(1);
		
		x1 = centreX - WIDTH/2;
		y1 = centreY - HEIGHT/2;
		x2 = x1 + WIDTH;
		y2 = y1 + HEIGHT;
		speed = INITIAL_SPEED;
		
		/*
		 * Randomize initial angle but point towards a Player
		 * Angles between -45..45 go to the right
		 * Angles between 135..225 go to the left
		 */
		angle = (Math.random() * 180) - 45;
		if (angle > 45) angle += 90;
	}

	/**
	 * Draws the Ball in place.
	 */
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect((int) x1, (int) y1, WIDTH, HEIGHT);
	}

	/**
	 * Updates the Ball's position.
	 */
	@Override
	public void tick() {
		
		double a = Math.toRadians(angle);
		
		speedX = Math.cos(a) * speed;
		speedY = Math.sin(a) * speed;
		
		translate(speedX, speedY);
		
	}

	/**
	 * Moves the Ball according to its speed, and handles collision.
	 * @param dx
	 * @param dy
	 */
	private void translate(double dx, double dy) {
		
		// Update position
		x1 += dx;
		y1 += dy;
		x2 = x1 + WIDTH;
		y2 = y1 + HEIGHT;

		// Check bounds
		if (x1 < 0){
			x1 = 0;
			x2 = x1 + WIDTH;
			state.pointScored(1, this);
		} else if (x2 > GameState.GAME_WIDTH){
			x1 = GameState.GAME_WIDTH - WIDTH;
			x2 = x1 + WIDTH;
			state.pointScored(0, this);
		}
		
		if (y1 < 0){
			y1 = 0;
			y2 = y1 + HEIGHT;
			bounceOffWall();
		} else if (y2 > GameState.GAME_HEIGHT){
			y1 = GameState.GAME_HEIGHT - HEIGHT;
			y2 = y1 + HEIGHT;
			bounceOffWall();
		}

		// Check for player collision
		if (x1 < p1.x2){
			if (p1.y1 < y2 && p1.y2 > y1){
				setPos(p1.x2, y1);
				bounceOffPlayer(p1.getSpeed());
			}
		} else if (x2 > p2.x1){
			if (p2.y1 < y2 && p2.y2 > y1){
				setPos(p2.x1 - WIDTH - 1, y1);
				bounceOffPlayer(p2.getSpeed());
			}
		}
	}

	/**
	 * Sets the Ball's position according to its top-left co-ordinate.
	 * @param x1
	 * @param y1
	 */
	private void setPos(double x1, double y1) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1 + WIDTH;
		this.y2 = y1 + HEIGHT;
	}

	/**
	 * Processes a bounce after hitting a Player.
	 * @param playerSpeed
	 */
	private void bounceOffPlayer(double playerSpeed) {
		angle = 180 - angle;
		
		// Change the angle by slicing the ball
		angle += Player.BALL_ANGLE_MULTIPLIER * playerSpeed;
		
		// Accelerate
		if (speed < MAX_SPEED){
			speed *= BOUNCE_SPEED_MULTIPLIER;
			if (speed > MAX_SPEED) speed = MAX_SPEED;
		}
	}

	/**
	 * Processes a bounce after hitting a wall.
	 */
	private void bounceOffWall() {
		angle = -angle;

		// Accelerate
		if (speed < MAX_SPEED){
			speed *= BOUNCE_SPEED_MULTIPLIER;
			if (speed > MAX_SPEED) speed = MAX_SPEED;
		}
	}

}
