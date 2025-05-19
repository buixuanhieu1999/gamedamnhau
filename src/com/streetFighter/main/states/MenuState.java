package com.streetFighter.main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.streetFighter.main.Game;
import com.streetFighter.managers.KeyManager;

public class MenuState extends State {
    private String[] options = {"START", "EXIT"};
    private int currentSelection = 0;
    private Font menuFont;
    private Color selectedColor = Color.RED;
    private Color unselectedColor = Color.WHITE;

    public MenuState(Game game) {
        super(game);
        menuFont = new Font("Arial", Font.BOLD, 30);
    }

    @Override
    public void tick() {
        KeyManager keyManager = game.getKeyManager();
        
        if (keyManager.up && !keyManager.down) {
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection = options.length - 1;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (keyManager.down && !keyManager.up) {
            currentSelection++;
            if (currentSelection >= options.length) {
                currentSelection = 0;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (keyManager.G) {
            switch(currentSelection) {
                case 0: // START
                    State.setState(new CharacterSelectState(game));
                    break;
                case 1: // EXIT
                    System.exit(0);
                    break;
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
        String title = "STREET FIGHTER II";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.WIDTH * Game.SCALE - titleWidth) / 2, 100);
        
        // Draw menu options
        g.setFont(menuFont);
        for (int i = 0; i < options.length; i++) {
            g.setColor(i == currentSelection ? selectedColor : unselectedColor);
            int textWidth = g.getFontMetrics().stringWidth(options[i]);
            g.drawString(options[i], (Game.WIDTH * Game.SCALE - textWidth) / 2, 200 + i * 50);
        }
        
        // Draw instructions
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        String instructions = "Use W/S to select, T to confirm";
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