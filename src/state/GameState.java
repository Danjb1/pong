package state;

import game.AIPlayer;
import game.Ball;
import game.Entity;
import game.Player;
import game.Pong;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Class representing the "in-game" state.
 * 
 * @author Dan Bryce
 */
public class GameState extends State {

	public static final int GAME_WIDTH = 1080;
	public static final int GAME_HEIGHT = 520;
	public static final int DIR_UP = -1;
	public static final int DIR_NONE = 0;
	public static final int DIR_DOWN = 1;

	private static final int SCORE_Y = 50;
	private static final int SCORE_SIZE = 12;
	private static final int BORDER_TOP = 150;
	private static final int BORDER_BOTTOM = 50;
	private static final int BORDER_LEFT = 100;
	private static final int BORDER_RIGHT = 100;
	private static final int NUM_PLAYERS = 2;

	private int drawX, drawY, drawWidth, drawHeight;
	private double unitsPerPixelX, unitsPerPixelY;
	private ArrayList<Entity> entities;
	private Player[] players;
	private boolean upPressed, downPressed;

	/**
	 * Constructs the GameState with the given number of Balls.
	 * @param pong
	 * @param balls
	 */
	public GameState(Pong pong, int balls) {
		super(pong);

		entities = new ArrayList<Entity>();
		players = new Player[NUM_PLAYERS];
		
		// Create Players
		players[0] = new Player(0);
		players[1] = new AIPlayer(1, entities);
		entities.add(players[0]);
		entities.add(players[1]);
		
		// Create Balls
		for (int i = 0; i < balls; i++){
			Ball ball = new Ball(this, GAME_WIDTH/2, GAME_HEIGHT/2);
			entities.add(ball);
		}
	}
	
	/**
	 * Re-calculates necessary dimensions when the Screen size changes.
	 */
	@Override
	public void sizeChanged(int width, int height) {
		super.sizeChanged(width, height);
		
		drawX = BORDER_LEFT;
		drawY = BORDER_TOP;
		drawWidth = screenWidth - (BORDER_LEFT + BORDER_RIGHT);
		drawHeight= screenHeight - (BORDER_TOP + BORDER_BOTTOM);

		unitsPerPixelX = (double) GAME_WIDTH / drawWidth;
		unitsPerPixelY = (double) GAME_HEIGHT / drawHeight;
	}

	/**
	 * Draws the game.
	 */
	@Override
	public void draw(Graphics2D g) {

		// Draw scores
		int scoreX = screenWidth - BORDER_RIGHT
				- getStringWidth(players[1].getScoreAsString(), SCORE_SIZE);
		drawString(g, players[0].getScoreAsString(), 
				BORDER_LEFT, SCORE_Y, SCORE_SIZE);
		drawString(g, players[1].getScoreAsString(), 
				scoreX, SCORE_Y, SCORE_SIZE);
		
		// Transform to fit the game area
		g.translate(drawX, drawY);
		g.scale(1/unitsPerPixelX, 1/unitsPerPixelY);
		
		// Draw border
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		
		// Draw Entities
		for (Entity entity : entities){
			entity.draw(g);
		}		
	}
	
	/**
	 * Processes the Entities within the game.
	 */
	@Override
	public void tick() {
		for (Entity entity : entities){
			entity.tick();
		}
	}
	
	/**
	 * KeyListener method to handle player input.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_UP:
			upPressed = true;
			players[0].setDir(DIR_UP);
			break;
		case KeyEvent.VK_DOWN:
			downPressed = true;
			players[0].setDir(DIR_DOWN);
			break;
		}
	}

	/**
	 * KeyListener method to handle player input.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_UP:
			upPressed = false;
			if (downPressed){
				players[0].setDir(DIR_DOWN);
			} else {
				players[0].setDir(DIR_NONE);
			}
			break;
		case KeyEvent.VK_DOWN:
			downPressed = false;
			if (upPressed){
				players[0].setDir(DIR_UP);
			} else {
				players[0].setDir(DIR_NONE);
			}
			break;
		}
	}

	/**
	 * Updates the given Player's score, and resets the scoring Ball.
	 * @param id Player number, 0 or 1.
	 * @param ball
	 */
	public void pointScored(int id, Ball ball) {
		players[id].modScore(1);
		
		// Reset Ball (TODO: re-use Ball to save creating a new Object)
		int index = entities.indexOf(ball);
		entities.set(index, new Ball(this, GAME_WIDTH/2, GAME_HEIGHT/2));
	}

	/**
	 * Getter for the given Player.
	 * @param id Player number, 0 or 1.
	 * @return
	 */
	public Player getPlayer(int id) {
		return players[id];
	}
	
}
