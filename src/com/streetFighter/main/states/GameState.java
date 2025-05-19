package com.streetFighter.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import com.streetFighter.entities.Ken;
import com.streetFighter.entities.Ryu;
import com.streetFighter.entities.DeeJay;
import com.streetFighter.gfx.*;
import com.streetFighter.main.Game;
import com.streetFighter.managers.PlayerManager;

public class GameState extends State {	
	// init player characters
	private Ryu ryu;
	private Ken ken;
	private DeeJay deeJay;
	// Add second instances for when players choose the same character
	private Ryu player2Ryu;
	private Ken player2Ken;
	private DeeJay player2DeeJay;
	
	private boolean isBothRyu = false;
	private boolean isBothKen = false;
	private boolean isBothDeeJay = false;
	
	private boolean gameOver = false;
	private int gameOverOption = 0; // 0 = restart, 1 = main menu
	private long lastSelectionTime = 0;
	private static final long SELECTION_COOLDOWN = 200;

	@Override
	public void music() {
		System.out.println("Playing game state music...");
	}
	
	// constructor
	public GameState(Game game) {
		super(game);
		initPlayers();
	}
	
	private void initPlayers() {
		// Get selections from PlayerManager
		int player1Character = game.getPlayerManager().getPlayer1Selection();
		int player2Character = game.getPlayerManager().getPlayer2Selection();
		
		// Reset variables
		ryu = null;
		ken = null;
		deeJay = null;
		player2Ryu = null;
		player2Ken = null;
		player2DeeJay = null;
		isBothRyu = false;
		isBothKen = false;
		isBothDeeJay = false;
		
		// Create player 1's character (left side)
		if (player1Character == PlayerManager.RYU) { // Player 1 chose Ryu
			ryu = new Ryu(game, 60, 280, 1); // Player 1 controls
		} else if (player1Character == PlayerManager.KEN) { // Player 1 chose Ken
			ken = new Ken(game, 60, 280, 1); // Player 1 controls
		} else if (player1Character == PlayerManager.DEEJAY) { // Player 1 chose DeeJay
			deeJay = new DeeJay(game, 60, 280, 1); // Player 1 controls
		}
		
		// Create player 2's character (right side)
		if (player2Character == PlayerManager.RYU) { // Player 2 chose Ryu
			if (player1Character == PlayerManager.RYU) {
				// Both chose Ryu
				player2Ryu = new Ryu(game, 224 * 2, 280, 2); // Player 2 controls
				isBothRyu = true;
			} else {
				// Only player 2 chose Ryu
				ryu = new Ryu(game, 224 * 2, 280, 2); // Player 2 controls
			}
		} else if (player2Character == PlayerManager.KEN) { // Player 2 chose Ken
			if (player1Character == PlayerManager.KEN) {
				// Both chose Ken
				player2Ken = new Ken(game, 224 * 2, 280, 2); // Player 2 controls
				isBothKen = true;
			} else {
				// Only player 2 chose Ken
				ken = new Ken(game, 224 * 2, 280, 2); // Player 2 controls
			}
		} else if (player2Character == PlayerManager.DEEJAY) { // Player 2 chose DeeJay
			if (player1Character == PlayerManager.DEEJAY) {
				// Both chose DeeJay
				player2DeeJay = new DeeJay(game, 224 * 2, 280, 2); // Player 2 controls
				isBothDeeJay = true;
			} else {
				// Only player 2 chose DeeJay
				deeJay = new DeeJay(game, 224 * 2, 280, 2); // Player 2 controls
			}
		}
		
		gameOver = false;
		gameOverOption = 0;
	}
	
	@Override
	public void tick() {
		if (!gameOver) {
			// Update hitboxes and attack boxes for main characters
			if (ryu != null) {
		ryu.getAttackBounds();
		ryu.getHitBounds();
				ryu.tick();
			}
		
			if (ken != null) {
		ken.getAttackBounds();
		ken.getHitBounds();
				ken.tick();
			}
			
			if (deeJay != null) {
				deeJay.getAttackBounds();
				deeJay.getHitBounds();
				deeJay.tick();
			}
			
			// Update second instances if both players chose the same character
			if (isBothRyu && player2Ryu != null) {
				player2Ryu.getAttackBounds();
				player2Ryu.getHitBounds();
				player2Ryu.tick();
			}
			
			if (isBothKen && player2Ken != null) {
				player2Ken.getAttackBounds();
				player2Ken.getHitBounds();
				player2Ken.tick();
			}
			
			if (isBothDeeJay && player2DeeJay != null) {
				player2DeeJay.getAttackBounds();
				player2DeeJay.getHitBounds();
				player2DeeJay.tick();
			}
			
			// Process collisions for the appropriate character combinations
			handleCollisions();
			
			// Check for game over
			checkGameOver();
		} else {
			// Handle game over menu navigation
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastSelectionTime > SELECTION_COOLDOWN) {
				if (game.getKeyManager().up || game.getKeyManager().down) {
					gameOverOption = gameOverOption == 0 ? 1 : 0;
					lastSelectionTime = currentTime;
				}
				
				if (game.getKeyManager().G) {
					if (gameOverOption == 0) {
						// Restart the game
						initPlayers();
					} else {
						// Return to menu
						State.setState(new MenuState(game));
					}
				}
			}
		}
	}
	
	private void handleCollisions() {
		// Handle different collision scenarios based on character combinations
		
		// DeeJay vs DeeJay - when both players chose DeeJay
		if (isBothDeeJay) {
			// DeeJay vs DeeJay - P1's DeeJay (left) attacks P2's DeeJay (right)
			if (deeJay.getAttackBounds().intersects(player2DeeJay.getHitBounds())) {
				player2DeeJay.takeDamage();
			}
			// P2's DeeJay (right) attacks P1's DeeJay (left)
			if (player2DeeJay.getAttackBounds().intersects(deeJay.getHitBounds())) {
				deeJay.takeDamage();
			}
		}
		// Ryu vs Ryu - when both players chose Ryu
		else if (isBothRyu) {
			// Ryu vs Ryu - P1's Ryu (left) attacks P2's Ryu (right)
			if (ryu.getAttackBounds().intersects(player2Ryu.getHitBounds())) {
				player2Ryu.takeDamage();
			}
			// P2's Ryu (right) attacks P1's Ryu (left)
			if (player2Ryu.getAttackBounds().intersects(ryu.getHitBounds())) {
				ryu.takeDamage();
			}
		}
		// Ken vs Ken - when both players chose Ken
		else if (isBothKen) {
			// Ken vs Ken - P1's Ken (left) attacks P2's Ken (right)
			if (ken.getAttackBounds().intersects(player2Ken.getHitBounds())) {
				player2Ken.takeDamage();
			}
			// P2's Ken (right) attacks P1's Ken (left)
			if (player2Ken.getAttackBounds().intersects(ken.getHitBounds())) {
				ken.takeDamage();
			}
		}
		// Different characters fighting each other
		else {
			// Check if Ryu is attacking anyone
			if (ryu != null) {
				if (ken != null && ryu.getAttackBounds().intersects(ken.getHitBounds())) {
					ken.takeDamage();
				}
				if (deeJay != null && ryu.getAttackBounds().intersects(deeJay.getHitBounds())) {
					deeJay.takeDamage();
				}
			}
			
			// Check if Ken is attacking anyone
			if (ken != null) {
				if (ryu != null && ken.getAttackBounds().intersects(ryu.getHitBounds())) {
					ryu.takeDamage();
				}
				if (deeJay != null && ken.getAttackBounds().intersects(deeJay.getHitBounds())) {
					deeJay.takeDamage();
				}
			}
			
			// Check if DeeJay is attacking anyone
			if (deeJay != null) {
				if (ryu != null && deeJay.getAttackBounds().intersects(ryu.getHitBounds())) {
					ryu.takeDamage();
				}
				if (ken != null && deeJay.getAttackBounds().intersects(ken.getHitBounds())) {
					ken.takeDamage();
				}
			}
		}
	}
	
	private void checkGameOver() {
		boolean isGameOver = false;
		
		if (isBothRyu && (ryu.getHealth() <= 0 || player2Ryu.getHealth() <= 0)) {
			isGameOver = true;
		} else if (isBothKen && (ken.getHealth() <= 0 || player2Ken.getHealth() <= 0)) {
			isGameOver = true;
		} else if (isBothDeeJay && (deeJay.getHealth() <= 0 || player2DeeJay.getHealth() <= 0)) {
			isGameOver = true;
		} else if (ryu != null && ken != null && (ryu.getHealth() <= 0 || ken.getHealth() <= 0)) {
			isGameOver = true;
		} else if (ryu != null && deeJay != null && (ryu.getHealth() <= 0 || deeJay.getHealth() <= 0)) {
			isGameOver = true;
		} else if (ken != null && deeJay != null && (ken.getHealth() <= 0 || deeJay.getHealth() <= 0)) {
			isGameOver = true;
		}
		
		if (isGameOver) {
			gameOver = true;
		}
	}

	@Override
	public void render(Graphics g) {				
		// get images for ui
		ImageIcon healthBar = new ImageIcon("healthBar.png");
		ImageIcon ryuFont = new ImageIcon("ryuFont.png");
		ImageIcon kenFont = new ImageIcon("kenFont.png");
		ImageIcon deeJayFont = new ImageIcon("deeJayFont.png");  // Add this image file

		// Render health bars and characters based on selection combination
		renderHealthBars(g, healthBar, ryuFont, kenFont, deeJayFont);
		renderCharacters(g);
					
		// Draw game over screen
		if (gameOver) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
			g.setColor(Color.WHITE);
			
			// Determine which player won based on character health and selections
			String winner = determineWinner();
			
			g.setFont(new Font("Arial", Font.BOLD, 40));
			int winnerWidth = g.getFontMetrics().stringWidth(winner);
			g.drawString(winner, (Game.WIDTH * Game.SCALE - winnerWidth) / 2, Game.HEIGHT * Game.SCALE / 3);
			
			// Draw game over menu options
			g.setFont(new Font("Arial", Font.BOLD, 24));
			String restart = "RESTART";
			String mainMenu = "MAIN MENU";
			
			g.setColor(gameOverOption == 0 ? Color.RED : Color.WHITE);
			int restartWidth = g.getFontMetrics().stringWidth(restart);
			g.drawString(restart, (Game.WIDTH * Game.SCALE - restartWidth) / 2, Game.HEIGHT * Game.SCALE / 2);
			
			g.setColor(gameOverOption == 1 ? Color.RED : Color.WHITE);
			int menuWidth = g.getFontMetrics().stringWidth(mainMenu);
			g.drawString(mainMenu, (Game.WIDTH * Game.SCALE - menuWidth) / 2, Game.HEIGHT * Game.SCALE / 2 + 40);
			
			// Draw instructions
			g.setFont(new Font("Arial", Font.PLAIN, 18));
			g.setColor(Color.GRAY);
			String instructions = "Use W/S to select, T to confirm";
			int instrWidth = g.getFontMetrics().stringWidth(instructions);
			g.drawString(instructions, (Game.WIDTH * Game.SCALE - instrWidth) / 2, Game.HEIGHT * Game.SCALE - 50);
		}
	}
	
	private void renderHealthBars(Graphics g, ImageIcon healthBar, ImageIcon ryuFont, ImageIcon kenFont, ImageIcon deeJayFont) {
		// Render left side health bar (Player 1)
		g.setColor(Color.yellow);
		
		if (isBothRyu) {
			// Player 1 is Ryu
			double percentRyu = ryu.getHealth() / 100.0;
			g.fillRect(61, 19, (int) (173 * percentRyu), 11);
			g.drawImage(ryuFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		} else if (isBothKen) {
			// Player 1 is Ken
			double percentKen = ken.getHealth() / 100.0;
			g.fillRect(61, 19, (int) (173 * percentKen), 11);
			g.drawImage(kenFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		} else if (isBothDeeJay) {
			// Player 1 is DeeJay
			double percentDeeJay = deeJay.getHealth() / 100.0;
			g.fillRect(61, 19, (int) (173 * percentDeeJay), 11);
			g.drawImage(deeJayFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		} else if (ryu != null) {
			// Player 1 is Ryu
		double percentRyu = ryu.getHealth() / 100.0;	
		g.fillRect(61, 19, (int) (173 * percentRyu), 11);
		g.drawImage(ryuFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		} else if (ken != null) {
			// Player 1 is Ken
			double percentKen = ken.getHealth() / 100.0;
			g.fillRect(61, 19, (int) (173 * percentKen), 11);
			g.drawImage(kenFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		} else if (deeJay != null) {
			// Player 1 is DeeJay
			double percentDeeJay = deeJay.getHealth() / 100.0;
			g.fillRect(61, 19, (int) (173 * percentDeeJay), 11);
			g.drawImage(deeJayFont.getImage(), kenFont.getIconWidth() - 35, 40, 48, 16, null);
		}
		
		// Render right side health bar (Player 2)
		g.setColor(Color.yellow);
		
		if (isBothRyu) {
			// Player 2 is also Ryu
			double percentRyu = player2Ryu.getHealth() / 100.0;
			g.fillRect(99 + 29 + 144, 19, (int) (173 * percentRyu), 11);
			g.drawImage(ryuFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * ryuFont.getIconWidth() + ryuFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
		} else if (isBothKen) {
			// Player 2 is also Ken
			double percentKen = player2Ken.getHealth() / 100.0;
			g.fillRect(99 + 29 + 144, 19, (int) (173 * percentKen), 11);
			g.drawImage(kenFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * kenFont.getIconWidth() + kenFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
		} else if (isBothDeeJay) {
			// Player 2 is also DeeJay
			double percentDeeJay = player2DeeJay.getHealth() / 100.0;
			g.fillRect(99 + 29 + 144, 19, (int) (173 * percentDeeJay), 11);
			g.drawImage(deeJayFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * deeJayFont.getIconWidth() + deeJayFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
		} else {
			// Get player selection info
			int player1Selection = game.getPlayerManager().getPlayer1Selection();
			int player2Selection = game.getPlayerManager().getPlayer2Selection();
			
			// Determine which character is player 2
			if (player2Selection == PlayerManager.RYU) {
				// Player 2 is Ryu
				double percentRyu = ryu.getHealth() / 100.0;
				g.fillRect(99 + 29 + 144, 19, (int) (173 * percentRyu), 11);
				g.drawImage(ryuFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * ryuFont.getIconWidth() + ryuFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
			} else if (player2Selection == PlayerManager.KEN) {
				// Player 2 is Ken
				double percentKen = ken.getHealth() / 100.0;
				g.fillRect(99 + 29 + 144, 19, (int) (173 * percentKen), 11);
				g.drawImage(kenFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * kenFont.getIconWidth() + kenFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
			} else if (player2Selection == PlayerManager.DEEJAY) {
				// Player 2 is DeeJay
				double percentDeeJay = deeJay.getHealth() / 100.0;
				g.fillRect(99 + 29 + 144, 19, (int) (173 * percentDeeJay), 11);
				g.drawImage(deeJayFont.getImage(), (Game.WIDTH * Game.SCALE) - 2 * deeJayFont.getIconWidth() + deeJayFont.getIconWidth() / 2 + 32, 40, 48, 16, null);
			}
		}
		
		// Draw health bar frame
		g.drawImage(healthBar.getImage(), 60, 16, (int) (healthBar.getIconWidth() * 1.2), (int) (healthBar.getIconHeight() * 1.2), null);
	}
	
	private void renderCharacters(Graphics g) {
		// Render characters based on selection
		if (isBothRyu) {
			// Both players chose Ryu
			ryu.render(g);
			player2Ryu.render(g);
		} else if (isBothKen) {
			// Both players chose Ken
			ken.render(g);
			player2Ken.render(g);
		} else if (isBothDeeJay) {
			// Both players chose DeeJay
			deeJay.render(g);
			player2DeeJay.render(g);
		} else {
			// Different characters
			if (ryu != null) {
		ryu.render(g);
			}
			if (ken != null) {
		ken.render(g);	
			}
			if (deeJay != null) {
				deeJay.render(g);
			}
		}
	}
	
	private String determineWinner() {
		if (isBothRyu) {
			return (ryu.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
		} else if (isBothKen) {
			return (ken.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
		} else if (isBothDeeJay) {
			return (deeJay.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
		} else {
			// Different characters
			int player1Character = game.getPlayerManager().getPlayer1Selection();
			
			if (player1Character == PlayerManager.RYU) {
				return (ryu.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
			} else if (player1Character == PlayerManager.KEN) {
				return (ken.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
			} else { // player1Character == PlayerManager.DEEJAY
				return (deeJay.getHealth() <= 0) ? "PLAYER 2 WINS!" : "PLAYER 1 WINS!";
			}
		}
	}
	
	// GETTERS AND SETTERS for hitboxes and positions
	
	// Ryu
	public Rectangle getRyuHitBounds() {
		if (isBothRyu) {
			// Need to specify which Ryu - this is for the main one (Player 1)
			return ryu.getHitBounds();
		} else if (ryu != null) {
		return ryu.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getRyuAttackBounds() {
		if (isBothRyu) {
			// Need to specify which Ryu - this is for the main one (Player 1)
			return ryu.getAttackBounds();
		} else if (ryu != null) {
		return ryu.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	// Ken
	public Rectangle getKenHitBounds() {
		if (isBothKen) {
			// Need to specify which Ken - this is for the main one (Player 1)
			return ken.getHitBounds();
		} else if (ken != null) {
		return ken.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getKenAttackBounds() {
		if (isBothKen) {
			// Need to specify which Ken - this is for the main one (Player 1)
			return ken.getAttackBounds();
		} else if (ken != null) {
		return ken.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	// DeeJay
	public Rectangle getDeeJayHitBounds() {
		if (isBothDeeJay) {
			// Need to specify which DeeJay - this is for the main one (Player 1)
			return deeJay.getHitBounds();
		} else if (deeJay != null) {
			return deeJay.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getDeeJayAttackBounds() {
		if (isBothDeeJay) {
			// Need to specify which DeeJay - this is for the main one (Player 1)
			return deeJay.getAttackBounds();
		} else if (deeJay != null) {
			return deeJay.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	// Position getters
	@Override
	public int getRyuX() {
		if (ryu != null) {
		return ryu.getRyuX();
		}
		return 0;
	}

	@Override
	public int getKenX() {
		if (ken != null) {
		return ken.getKenX();
		}
		return 0;
	}
	
	public int getDeeJayX() {
		if (deeJay != null) {
			return (int) deeJay.getDeeJayX();
		}
		return 0;
	}
	
	// Add methods for the second instances
	public Rectangle getPlayer2RyuHitBounds() {
		if (player2Ryu != null) {
			return player2Ryu.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getPlayer2RyuAttackBounds() {
		if (player2Ryu != null) {
			return player2Ryu.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getPlayer2KenHitBounds() {
		if (player2Ken != null) {
			return player2Ken.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getPlayer2KenAttackBounds() {
		if (player2Ken != null) {
			return player2Ken.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getPlayer2DeeJayHitBounds() {
		if (player2DeeJay != null) {
			return player2DeeJay.getHitBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
	
	public Rectangle getPlayer2DeeJayAttackBounds() {
		if (player2DeeJay != null) {
			return player2DeeJay.getAttackBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}
}
