package game;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import state.LoadingState;
import state.MenuState;
import state.State;

/**
 * Main class that sets up and controls the window and game loop.
 * 
 * @author Dan Bryce
 */
public class Pong implements KeyListener {

	public static final int FONT_CHAR_SIZE = 4;
	public static final int FONT_GAP_SIZE = 1;
	
	private static final String TITLE = "Pong";
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 720;
	private static final int FPS = 60;
	private static final int MS = 1000 / FPS;

	private JFrame frame;
	private GameThread thread;
	private Screen screen;
	private State state;
	private BufferedImage font;
	private HashMap<Character, Integer> fontMap;

	/**
	 * Sets up the game.
	 */
	public Pong() {
		initialiseFont();
		changeState(new LoadingState(this));
		screen = new Screen();
		thread = new GameThread();
		thread.start();
		createFrame();
		changeState(new MenuState(this));
	}
	
	/**
	 * Creates and displays the game window.
	 */
	private void createFrame() {
		
		// Create a blank cursor
		BufferedImage cursorImg = 
				new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		
		frame = new JFrame(TITLE);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setCursor(blankCursor);
		frame.setContentPane(screen);
		frame.addKeyListener(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Initialises the game's font.
	 */
	private void initialiseFont() {
		font = getImage("font");
		fontMap = new HashMap<Character, Integer>();
		int index = 0;
		
		// Alphabet
		for (char c = 'a'; c <= 'z'; c++){			
			fontMap.put(c, index);
			index++;
		}
		
		// Numbers
		for (char c = '0'; c <= '9'; c++){
			fontMap.put(c, index);
			index++;
		}
	}
	
	/**
	 * Getter for font.
	 * @return
	 */
	public BufferedImage getFont() {
		return font;
	}
	
	/**
	 * Getter for the font map.
	 * 
	 * The font map maps characters to their position within the image.
	 * @return
	 */
	public HashMap<Character, Integer> getFontMap() {
		return fontMap;
	}
	
	/**
	 * Loads the image with the given name.
	 * 
	 * Note that this assumes the image has a PNG extension.
	 * @param filename
	 * @return
	 */
	private BufferedImage getImage(String filename) {
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource("gfx/" + filename + ".png");
		BufferedImage image = null; 
		try {
			image = ImageIO.read(url);
		} catch (IOException e){
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * KeyListener method that passes KeyEvents to the current State.
	 * 
	 * This will intercept "Escape" key presses, which are used to quit the 
	 * game.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			quit();
		} else {
			state.keyPressed(e);
		}
	}

	/**
	 * KeyListener method that passes KeyEvents to the current State.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		state.keyReleased(e);
	}

	/**
	 * (Unused) KeyListener method.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Changes the current State.
	 * @param state
	 */
	public void changeState(State state) {
		this.state = state;
		
		// Inform new State of its initial size
		if (screen != null){
			state.sizeChanged(screen.getWidth(), screen.getHeight());
		}
	}

	/**
	 * Quits the game, stopping all Threads and disposing of the window.
	 */
	private void quit() {
		thread.destroy();
		frame.setVisible(false);
		frame.dispose();
		System.exit(0);
	}
	
	/**
	 * Entry point for the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new Pong();
	}

	/**
	 * Thread that controls the game.
	 * 
	 * @author Dan Bryce
	 */
	public class GameThread extends Thread {
		
		private boolean paused, stopped;
		
		/**
		 * Runs the game at the correct FPS.
		 */
		@Override
		public void run() {
			
			long beforeTime, dt, sleepTime;
			
			while (!stopped){
				beforeTime = System.currentTimeMillis();
				
				if (paused){
					try {
						synchronized (this){
							wait();
						}
					} catch (InterruptedException e) {
						if (stopped) break;
					}
				}

				state.tick();
				screen.repaint();
				
				// Sleep time is dependent on how long this tick took
				dt = System.currentTimeMillis() - beforeTime;
	            sleepTime = MS - dt;
	            if (sleepTime < 0) sleepTime = 0;
		        
		        // Sleep until the next tick
		        try {
					sleep(sleepTime);
				} catch (InterruptedException e) {
					if (stopped) break;
				}
			}
		}
		
		/**
		 * Pauses this GameThread.
		 * 
		 * Call unpause() to resume it.
		 */
		public void pause() {
	    	if (isAlive()){
	    		paused = true;
	    	}
		}
		
		/**
		 * Unpauses this GameThread after a call to pause().
		 */
		public void unpause() {
	    	if (isAlive()){
				synchronized (this){
					paused = false;
					notifyAll();
				}
	    	}
		}
		
		/**
		 * Stops this GameThread running entirely.
		 */
		public void destroy() {
			if (isAlive()){
				stopped = true;
				interrupt();
			}
		}
		
	}
	
	/**
	 * Custom JPanel implementation to draw to the game window.
	 * 
	 * @author Dan Bryce
	 */
	public class Screen extends JPanel implements ComponentListener {
		
		private static final long serialVersionUID = 1L;
		
		/**
		 * Constructs a Screen.
		 */
		public Screen() {
			setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
			setBackground(Color.BLACK);
			addComponentListener(this);
		}
		
		/**
		 * Passes the Graphics2D object to the current State.
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			
			state.draw(g2d);
		}

		/**
		 * (Unused) ComponentListener method.
		 */
		@Override
		public void componentHidden(ComponentEvent e) {
		}

		/**
		 * (Unused) ComponentListener method.
		 */
		@Override
		public void componentMoved(ComponentEvent e) {
		}

		/**
		 * ComponentListener method that responds to size changes.
		 * 
		 * When the Screen is resized, it will inform the current State.
		 */
		@Override
		public void componentResized(ComponentEvent e) {
			int width = e.getComponent().getWidth();
			int height = e.getComponent().getHeight();
			state.sizeChanged(width, height);
		}

		/**
		 * (Unused) ComponentListener method.
		 */
		@Override
		public void componentShown(ComponentEvent e) {
		}
		
	}
	
}
