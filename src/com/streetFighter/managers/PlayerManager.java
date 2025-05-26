package com.streetFighter.managers;

import com.streetFighter.main.Game;

public class PlayerManager {
    // Character selection constants
    public static final int RYU = 0;
    public static final int KEN = 1;
    public static final int DEEJAY = 2;
    
    // Current selection state
    private int currentSelector = 1;  // 1 = Player 1 selecting, 2 = Player 2 selecting
    
    // Character selections
    private int player1Selection = -1; // -1 = not selected yet
    private int player2Selection = -1; // -1 = not selected yet
    
    private Game game;
    
    public PlayerManager(Game game) {
        this.game = game;
    }
    
    /**
     * Check if both players have made their selection
     * @return true if both players selected a character
     */
    public boolean isSelectionComplete() {
        return player1Selection >= 0 && player2Selection >= 0;
    }
    
    /**
     * Get current player that is selecting
     * @return 1 for Player 1, 2 for Player 2
     */
    public int getCurrentSelector() {
        return currentSelector;
    }
    
    /**
     * Confirm character selection for current player
     * @param characterIndex The index of the character (0=RYU, 1=KEN, 2=DEEJAY)
     */
    public void confirmSelection(int characterIndex) {
        if (currentSelector == 1) {
            player1Selection = characterIndex;
            currentSelector = 2;  // Now player 2's turn to select
        } else {
            player2Selection = characterIndex;
            // Both players have selected, no need to change selector
        }
    }
    
    /**
     * Get Player 1's character selection
     * @return The index of player 1's character or -1 if not selected
     */
    public int getPlayer1Selection() {
        return player1Selection;
    }
    
    /**
     * Get Player 2's character selection
     * @return The index of player 2's character or -1 if not selected
     */
    public int getPlayer2Selection() {
        return player2Selection;
    }
    
    /**
     * Reset all selections to start over
     */
    public void resetSelections() {
        player1Selection = -1;
        player2Selection = -1;
        currentSelector = 1;
    }
    
    /**
     * Get the character selection for the game
     * This determines which character faces right in the game
     */
    public int getGameCharacterSelection() {
        // Character facing right is the one player 1 selected
        return player1Selection;
    }
} 