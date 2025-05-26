package com.streetFighter.managers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
	private boolean[] keys;
	public boolean up, down, left, right;
	public boolean up1, down1, left1, right1;
	
	public boolean G, H, B, N;
	public boolean N1, N2, N4, N5;

	private long lastTransitionTime;
	private static final long TRANSITION_COOLDOWN = 300; // 300ms cooldown
	
	public KeyManager() {
		keys = new boolean[256];
		lastTransitionTime = 0;
	}
	
	public void tick() {		
		// movement P1
		up    = keys[KeyEvent.VK_W];
		down  = keys[KeyEvent.VK_S];
		left  = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		
		// attack P1 with cooldown for T key
		long currentTime = System.currentTimeMillis();
		if (keys[KeyEvent.VK_T] && currentTime - lastTransitionTime >= TRANSITION_COOLDOWN) {
			G = true;
			lastTransitionTime = currentTime;
		} else {
			G = false;
		}
		
		H = keys[KeyEvent.VK_Y];
		B = keys[KeyEvent.VK_U];
		N = keys[KeyEvent.VK_I];
		
		// movement P2
		up1    = keys[KeyEvent.VK_UP];
		down1  = keys[KeyEvent.VK_DOWN];
		left1  = keys[KeyEvent.VK_LEFT];
		right1 = keys[KeyEvent.VK_RIGHT];
		
		// attack P2
		N4 = keys[KeyEvent.VK_V];
		N5 = keys[KeyEvent.VK_B];
		N1 = keys[KeyEvent.VK_N];
		N2 = keys[KeyEvent.VK_M];
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
