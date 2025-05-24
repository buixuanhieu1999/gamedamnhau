package com.streetFighter.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import com.streetFighter.main.Game;
import com.streetFighter.managers.KeyManager;

public class MapSelectState extends State {
    private String[] maps = {"FOREST", "MAIN", "RYU STAGE"};
    private int currentSelection = 0;
    private Font mapFont;
    private Color selectedColor = Color.RED;
    private Color unselectedColor = Color.WHITE;
    private ImageIcon[] mapPreviews;

    public MapSelectState(Game game) {
        super(game);
        mapFont = new Font("Arial", Font.BOLD, 30);
        
        // Load map previews
        mapPreviews = new ImageIcon[3];
        mapPreviews[0] = new ImageIcon("forest_stage.gif");
        mapPreviews[1] = new ImageIcon("main_stage.gif");
        mapPreviews[2] = new ImageIcon("ryu_stage.png");
    }

    @Override
    public void tick() {
        KeyManager keyManager = game.getKeyManager();
        
        // Handle selection movement
        if (keyManager.left && !keyManager.right) {
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection = maps.length - 1;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (keyManager.right && !keyManager.left) {
            currentSelection++;
            if (currentSelection >= maps.length) {
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
            game.setMap(currentSelection + 1); // Maps are 1-based in Game class
            State.setState(game.getGameState());
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
        String title = "SELECT YOUR STAGE";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.WIDTH * Game.SCALE - titleWidth) / 2, 100);
        
        // Draw map preview
        if (mapPreviews[currentSelection] != null) {
            g.drawImage(mapPreviews[currentSelection].getImage(), 
                       (Game.WIDTH * Game.SCALE - mapPreviews[currentSelection].getIconWidth()) / 2,
                       120,
                       mapPreviews[currentSelection].getIconWidth(),
                       mapPreviews[currentSelection].getIconHeight(),
                       null);
        }
        
        // Draw map options
        g.setFont(mapFont);
        for (int i = 0; i < maps.length; i++) {
            g.setColor(i == currentSelection ? selectedColor : unselectedColor);
            int textWidth = g.getFontMetrics().stringWidth(maps[i]);
            g.drawString(maps[i], (Game.WIDTH * Game.SCALE) / 3 + (i * 200) - textWidth/2, 300);
        }
        
        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        String instructions = "Use A/D to select, T to confirm";
        int instrWidth = g.getFontMetrics().stringWidth(instructions);
        g.drawString(instructions, (Game.WIDTH * Game.SCALE - instrWidth) / 2, 350);
    }

    @Override
    public void music() {
        // Add music implementation if needed
    }

    // Required abstract method implementations
    @Override
    public Rectangle getRyuHitBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public Rectangle getRyuAttackBounds() { return new Rectangle(0, 0, 0, 0); }

    @Override
    public Rectangle getDeejayHitBounds() { 
        return new Rectangle(0, 0, 0, 0); 
    }

    @Override
    public Rectangle getDeejayAttackBounds() { 
        return new Rectangle(0, 0, 0, 0); 
    }

    @Override
    public int getDeejayX() { 
        return 0; 
    }

    @Override
    public Rectangle getKenHitBounds() { 
        return new Rectangle(0, 0, 0, 0); 
    }
    
    @Override
    public Rectangle getKenAttackBounds() { return new Rectangle(0, 0, 0, 0); }
    
    @Override
    public int getRyuX() { 
        return 0; 
    }
    
    @Override
    public int getKenX() { return 0; }
} 