package com.streetFighter.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.streetFighter.gfx.Assets;
import com.streetFighter.main.Game;
import com.streetFighter.managers.KeyManager;
import com.streetFighter.managers.PlayerManager;

public class CharacterSelectState extends State {
    private String[] characters = {"RYU", "KEN", "DEEJAY"};
    private int currentSelection = 0;
    private Font charFont;
    private Color selectedColor = Color.RED;
    private Color unselectedColor = Color.WHITE;
    
    // Player colors
    private Color player1Color = new Color(0, 128, 255); // Blue
    private Color player2Color = new Color(255, 50, 50); // Red

    public CharacterSelectState(Game game) {
        super(game);
        charFont = new Font("Arial", Font.BOLD, 30);
    }

    @Override
    public void tick() {
        KeyManager keyManager = game.getKeyManager();
        PlayerManager playerManager = game.getPlayerManager();
        
        // Determine which player is selecting and use appropriate controls
        if (playerManager.getCurrentSelector() == 1) {
            // Player 1 controls with WASD
            // Handle selection movement
            if (keyManager.left && !keyManager.right) {
                currentSelection--;
                if (currentSelection < 0) {
                    currentSelection = characters.length - 1;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (keyManager.right && !keyManager.left) {
                currentSelection++;
                if (currentSelection >= characters.length) {
                    currentSelection = 0;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Handle selection confirmation with cooldown
            if (keyManager.G) {
                playerManager.confirmSelection(currentSelection);
                currentSelection = 0; // Reset selection for player 2
            }
        } else {
            // Player 2 controls with arrow keys
            // Handle selection movement
            if (keyManager.left1 && !keyManager.right1) {
                currentSelection--;
                if (currentSelection < 0) {
                    currentSelection = characters.length - 1;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if (keyManager.right1 && !keyManager.left1) {
                currentSelection++;
                if (currentSelection >= characters.length) {
                    currentSelection = 0;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Handle selection confirmation with cooldown (Player 2 uses N4 key - V)
            if (keyManager.N4) {
                playerManager.confirmSelection(currentSelection);
                // Both players have selected, move to map selection
                if (playerManager.isSelectionComplete()) {
                    game.setSelectedCharacter(playerManager.getGameCharacterSelection());
                    State.setState(new MapSelectState(game));
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
        
        // Draw title
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Color.WHITE);
        
        // Show which player is currently selecting
        String title = (game.getPlayerManager().getCurrentSelector() == 1) ? 
                      "PLAYER 1 SELECT" : "PLAYER 2 SELECT";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.WIDTH * Game.SCALE - titleWidth) / 2, 80);
        
        // Draw character options
        g.setFont(charFont);
        for (int i = 0; i < characters.length; i++) {
            // Determine color based on selection status
            if (i == currentSelection) {
                // Current selection is highlighted in the player's color
                g.setColor(game.getPlayerManager().getCurrentSelector() == 1 ? player1Color : player2Color);
            } else {
                g.setColor(unselectedColor);
            }
            
            int textWidth = g.getFontMetrics().stringWidth(characters[i]);
            // Adjust spacing to accommodate three characters
            int xPos = (Game.WIDTH * Game.SCALE) / 4 + (i * 160) - textWidth/2;
            g.drawString(characters[i], xPos, 180);
        }
        
        // Display character portraits
        drawCharacterPortrait(g, currentSelection);
        
        // Display current player selections
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Player 1 selection
        g.setColor(player1Color);
        int player1Selection = game.getPlayerManager().getPlayer1Selection();
        String p1Text = "Player 1: " + (player1Selection >= 0 ? characters[player1Selection] : "Selecting...");
        g.drawString(p1Text, 50, 250);
        
        // Player 2 selection
        g.setColor(player2Color);
        int player2Selection = game.getPlayerManager().getPlayer2Selection();
        String p2Text = "Player 2: " + (player2Selection >= 0 ? characters[player2Selection] : "Waiting...");
        g.drawString(p2Text, Game.WIDTH * Game.SCALE - 250, 250);
        
        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        String instructions = (game.getPlayerManager().getCurrentSelector() == 1) ? 
                            "Player 1: Use A/D to select, T to confirm" : 
                            "Player 2: Use ←/→ to select, V to confirm";
        int instrWidth = g.getFontMetrics().stringWidth(instructions);
        g.drawString(instructions, (Game.WIDTH * Game.SCALE - instrWidth) / 2, Game.HEIGHT * Game.SCALE - 50);
    }
    
    private void drawCharacterPortrait(Graphics g, int characterIndex) {
        BufferedImage portrait = null;
        
        // Get a frame from each character's idle animation
        switch (characterIndex) {
            case 0: // Ryu
                portrait = Assets.idle[0];
                break;
            case 1: // Ken
                portrait = Assets.idle1[0];
                break;
            case 2: // DeeJay
                portrait = Assets.deejay_idle[0];
                break;
        }
        
        if (portrait != null) {
            // Draw the character portrait in the center bottom area
            int portraitWidth = 120;
            int portraitHeight = 120;
            g.drawImage(portrait, 
                       (Game.WIDTH * Game.SCALE - portraitWidth) / 2,
                       300, 
                       portraitWidth, 
                       portraitHeight, 
                       null);
        }
    }

    // Required abstract method implementations
    @Override
    public Rectangle getRyuHitBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public Rectangle getRyuAttackBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public Rectangle getKenHitBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public Rectangle getKenAttackBounds() { return new Rectangle(0, 0, 0, 0); }

    @Override
    public Rectangle getDeejayHitBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public Rectangle getDeejayAttackBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public int getRyuX() { return 0; }
    
    @Override
    public int getKenX() { return 0; }

    @Override
    public int getDeejayX() { return 0; }
} 