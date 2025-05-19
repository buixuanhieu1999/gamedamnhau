package com.streetFighter.main;


import java.io.File;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.streetFighter.gfx.Assets;
import com.streetFighter.gfx.ImageLoader;
import com.streetFighter.gfx.SpriteSheet;
import com.streetFighter.main.states.GameState;
import com.streetFighter.main.states.MenuState;
import com.streetFighter.main.states.CharacterSelectState;
import com.streetFighter.main.states.State;
import com.streetFighter.main.states.MapSelectState;
import com.streetFighter.managers.KeyManager;
import com.streetFighter.managers.PlayerManager;


public class Game extends Canvas implements Runnable {
	// declare constants
	public static final String TITLE  = "Street Fighter II";
	public static final int    WIDTH  = 256;
	public static final int    HEIGHT = 224;
	public static final int    SCALE  = 2;
	
	// tick variables
	public boolean running = false;
	public int tickCount = 0;
	
	// graphics
	private Graphics g;
	
	// states
	private State menuState;
	private State gameState;
	private State characterSelectState;
	private State mapSelectState;
	private int selectedCharacter = -1; // -1 = none, 0 = Ryu, 1 = Ken

	// input
	private KeyManager keyManager;
	
	// init jFrame
	private JFrame frame;
	
	// init scanner, and random
	private Scanner sc;
	private Random rand;
	
	// int map
	private int map = 0;
	
	// Add PlayerManager
	private PlayerManager playerManager;
	
	public Game() {
		// init frame properties
		frame = new JFrame(TITLE);
		frame.setSize(WIDTH * SCALE, HEIGHT * SCALE);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		
		frame.setUndecorated(false);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		setFocusable(false);
				
		// user input
		keyManager = new KeyManager();
		frame.addKeyListener(keyManager);
		
		// Initialize player manager
		playerManager = new PlayerManager(this);
		
		// pack everything
		frame.pack();
	}
		
	/**
	 * @see ex_1.png in project dir for explanation of game updating
	 * @see ex_2.png [...] 							of game state managing
	 */

	public synchronized void start() throws IOException {
		// the program is running...
		running = true;		
		
		// pre-load assets
		Assets.init();
			
		// Initialize all states
		menuState = new MenuState(this);
		characterSelectState = new CharacterSelectState(this);
		mapSelectState = new MapSelectState(this);
		
		// Set initial state to menu
		State.setState(menuState);
		
		// thread this class
		new Thread(this).start();
	}
	
	public synchronized void stop() {
		// if program is stopped, running is false
		running = false;
	}
	
	/**
	 * Minecraft: Notch's game loop
	 * @link https://stackoverflow.com/questions/18283199/java-main-game-loop
	 */
	
	public void run() {		
		// init vars
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / 60.0;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		// while the program is running....
		while (running) {
			
			// get the current system time
			long now = System.nanoTime();
			// find delta by taking difference between now and last
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			// can render each frame...
			boolean canRender = true;
			
			// if ratio is greater than one, meaning...
			while (delta >= 1) {
				/* if the current time - last / n, where n
				   can be any real number is greater than 1
			    update the game...*/
				ticks++;
				tick();
				delta--;
				canRender = true;
			}
			
			// sleep program so that not to many frames are produced (reduce lag)
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// if can render...
			if (canRender) {
				// increment frames and render
				frames++;
				render();

			}
			
			// if one second has passed...
			if (System.currentTimeMillis() - lastTimer > 1000) {
				// increment last timer, output frames to user
				lastTimer += 1000;
				System.out.println(ticks + " ticks, " + frames + " frames");
				frames = 0;
				ticks = 0;
			}						
		}		
	}
	
	public void tick() {
		// update keyboard input
		keyManager.tick();
		
		// if current state exist, then update the game
		if (State.getState() != null) {
			
			// increment tick count and get state of program
			tickCount++;
			State.getState().tick();

		}
	}
	
	
	public void render() {		
		BufferStrategy bs = getBufferStrategy();
		
		// create a double buffering strategy
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		// create temp black rect that fills screen
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		/* ALL DRAWING HERE */
		
		// init maps
		ImageIcon ryuStage    = new ImageIcon("ryu_stage.png");
		ImageIcon mainStage   = new ImageIcon("main_stage.gif");
		ImageIcon forestStage = new ImageIcon("forest_stage.gif");
		
		// Draw background based on selected map
		if (map == 1) {
			g.drawImage(forestStage.getImage(), -900, -220, forestStage.getIconWidth() * 2, forestStage.getIconHeight() * 2, null);
		} else if (map == 2) {
			g.drawImage(mainStage.getImage(), -67, -67, null);
		} else if (map == 3) {
			g.drawImage(ryuStage.getImage(), -212, 30, ryuStage.getIconWidth() * Game.SCALE, ryuStage.getIconHeight() * Game.SCALE, null);
		}
		
		// if current state exist, then render		
		if (State.getState() != null) {		
			tickCount++;
			State.getState().render(g);	
		}
					
		/* ALL DRAWING HERE */
		
		g.dispose();
		bs.show();
	}	
	
	public static void main(String[] args) throws IOException {
		Game game = new Game();
		game.start();
	}
	
	// GETTERS AND SETTERS
	
	/**
	 * @description 
	 * 	   gets key presses of user
	*/
	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	/**
	 * @description 
	 * 	   gets current game state
	*/
	public State getGameState() {
		if (gameState == null) {
			gameState = new GameState(this);
		}
		return gameState;
	}

	// Add method to set map
	public void setMap(int mapNumber) {
		this.map = mapNumber;
	}

	// Add method to set selected character
	public void setSelectedCharacter(int character) {
		this.selectedCharacter = character;
	}

	// Add method to get selected character
	public int getSelectedCharacter() {
		return selectedCharacter;
	}

	// Add method to get character select state
	public State getCharacterSelectState() {
		return characterSelectState;
	}

	// Add method to get map select state
	public State getMapSelectState() {
		return mapSelectState;
	}

	// Add getter for PlayerManager
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
}
