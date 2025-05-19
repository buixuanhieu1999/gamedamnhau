package com.streetFighter.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.streetFighter.gfx.Animation;
import com.streetFighter.gfx.Assets;
import com.streetFighter.main.Game;

public class Ryu extends Creature {

	// Ryu: vars...
	private int health;
	private int velX, velY;
	private Game game;
	private boolean facingRight = true; // By default Ryu faces right
	private int playerNumber = 1; // Default to player 1, can be 1 or 2

	// STATES

	// basic movement
	private final int IDLING         = 0;
	private final int PARRYING_R     = 1; 
	private final int PARRYING_L     = 2;
	private final int CROUCHING      = 3;
	private final int JUMPING        = 9;
	private final int FRONT_FLIPPING = 10;
	private final int BACK_FLIPPING  = 11;

	// ground attacks: lettter stands for respective key
	private final int ATTACKING_G    = 4;
	private final int ATTACKING_H    = 5;
	private final int ATTACKING_B    = 6;
	private final int ATTACKING_N    = 7;

	// crouch attacks: C for crouch
	private final int ATTACKING_C_G  = 8;

	// air attacks: A for air
	private final int ATTACKING_A_G  = 12;
	private final int ATTACKING_A_H  = 13;
	private final int ATTACKING_A_B  = 14;	

	// hurting anims
	private final int HURTING = 15;
	// dummy var for checks
	private final int DUMMY = 19;

	// platformer
	private final int GRAV = 1;
	private final int TERMINAL_VELOCITY = 2;
	private final int JUMP_SPEED = -10;

	private boolean[] anims = new boolean[20];

	// movement animations
	private Animation idle;
	private Animation parry_f, parry_b;
	private Animation crouch;
	private Animation jump, front_flip, back_flip;

	// ground attack animations
	private Animation attack_G, attack_H, attack_B, attack_N;

	// crouch attack animations
	private Animation attack_C_G;

	// air attacks animation
	private Animation attack_A_G, attack_A_H, attack_A_B;

	// ground hurt
	private Animation hurting_G;

	// cooldowns
	private boolean hurting;
	private long lastTimer;

	// random generator
	Random rand;

	public Ryu(Game game, float x, float y) {
		this(game, x, y, 1); // Default to player 1
	}
	
	public Ryu(Game game, float x, float y, int playerNumber) {
		super(x, y);
		// initialise game in constuctor to access vars
		this.game = game;
		this.playerNumber = playerNumber;
		
		// Determine facing direction based on position
		// If Ryu is on the right side, he should face left
		this.facingRight = (x < Game.WIDTH);

		// instantiate random gen
		rand = new Random();

		// health to 100
		health = 100;

		// movement
		idle 	   = new Animation(100, Assets.idle);
		parry_f    = new Animation(100, Assets.parry_f);
		parry_b    = new Animation(100, Assets.parry_b);
		crouch     = new Animation(100, Assets.crouch);
		jump 	   = new Animation(85, Assets.jump);
		front_flip = new Animation(120, Assets.front_flip);
		back_flip  = new Animation(120, Assets.back_flip);

		// ground attacks
		attack_G   = new Animation(100, Assets.punch);
		attack_H   = new Animation(100, Assets.quick_punch);
		attack_B   = new Animation(75, Assets.upper_kick);
		attack_N   = new Animation(100, Assets.kick_low);

		// crouch attack
		attack_C_G = new Animation(50, Assets.crouch_punch);

		// air attacks
		attack_A_G = new Animation(100, Assets.air_punch);
		attack_A_H = new Animation(100, Assets.punch_down);
		attack_A_B = new Animation(100, Assets.air_kick);		

		// hurting anim
		hurting_G   = new Animation(100, Assets.hit_stand_back);
	}

	@Override
	public void tick() {

		// tick movement
		idle.tick();
		crouch.tick();

		// update anims
		parry_b.tick();		
		parry_f.tick();	

		// update attacks animations
		if (anims[ATTACKING_G])
			attack_G.tick();

		if (anims[ATTACKING_H])
			attack_H.tick();

		if (anims[ATTACKING_B])
			attack_B.tick();

		if (anims[ATTACKING_N])
			attack_N.tick();

		if (anims[ATTACKING_C_G])
			attack_C_G.tick();

		if (anims[ATTACKING_A_G])
			attack_A_G.tick();

		if (anims[ATTACKING_A_H])
			attack_A_H.tick();

		if (anims[ATTACKING_A_B])
			attack_A_B.tick();

		// If hurting, tick the animation
		if (hurting) {	
			hurting_G.tick();
		}

		// Reset hurting state after 400ms
		if (hurting && System.currentTimeMillis() - lastTimer > 400) {
			anims[HURTING] = false;
			hurting = false;
		}

		// if on the ground		
		if (y == 280) {
			// Check appropriate controls based on which player is controlling this Ryu
			boolean goLeft = (playerNumber == 1) ? game.getKeyManager().left : game.getKeyManager().left1;
			boolean goRight = (playerNumber == 1) ? game.getKeyManager().right : game.getKeyManager().right1;
			boolean goUp = (playerNumber == 1) ? game.getKeyManager().up : game.getKeyManager().up1;
			boolean goDown = (playerNumber == 1) ? game.getKeyManager().down : game.getKeyManager().down1;
			boolean attackG = (playerNumber == 1) ? game.getKeyManager().G : game.getKeyManager().N4;
			boolean attackH = (playerNumber == 1) ? game.getKeyManager().H : game.getKeyManager().N5;
			boolean attackB = (playerNumber == 1) ? game.getKeyManager().B : game.getKeyManager().N1;
			boolean attackN = (playerNumber == 1) ? game.getKeyManager().N : game.getKeyManager().N2;
			
			// if press left, move left
			if (goLeft && !goUp) {
				velX = -2;

				// reset and init true to state
				handleAnims(PARRYING_L);
			}
			// if press right, move forward
			else if (goRight && !goUp) {
				velX = 2;

				// reset and init true to state
				handleAnims(PARRYING_R);
			} 
			// if pressing only up
			else if (goUp && !goRight && !goLeft && !anims[JUMPING]) {
				// jump
				velY = JUMP_SPEED - 2;
				y-=1;
				jump.index = 0;
			}
			// if pressing up, right
			else if (goUp && goRight && !goLeft && !anims[FRONT_FLIPPING]) {
				// jump diagonally to the right
				velY = JUMP_SPEED;
				velX = 2;
				y-=1;
				front_flip.index = 0;				
			}
			// if pressing up, left
			else if (goUp && !goRight && goLeft && !anims[BACK_FLIPPING]) {
				// jump diagonally to the left
				velY = JUMP_SPEED;
				velX = -2;
				y-=1;
				back_flip.index = 0;	
			}
			// if press down, crouch
			else if (!goRight && !goLeft && goDown){

				// stop moving
				velX = 0;
				velY = 0;

				// only activate crouch
				handleAnims(CROUCHING);

				// if pressing g while crouching
				if (attackG) {
					velX = 0;
					velY = 0;

					// for punching frames, deactivate crouch
					anims[CROUCHING] = false;

					// reset and init true to state, punch
					if (checkIfRunning())
						handleAnims(ATTACKING_C_G);

				}

				// play full animation once then reset
				resetAnim(attack_C_G, ATTACKING_C_G);

			}
			// if g and not crouching
			else if (attackG && !anims[CROUCHING]){
				velX = 0;
				velY = 0;

				// reset and init true to state, punch
				if (checkIfRunning())
					handleAnims(ATTACKING_G);
			} else if (attackH && !anims[CROUCHING]){
				velX = 0;
				velY = 0;

				// reset and quick punch
				if (checkIfRunning())
					handleAnims(ATTACKING_H);
			} else if (attackB && !anims[CROUCHING]){
				velX = 0;
				velY = 0;

				// reset and kick
				if (checkIfRunning())
					handleAnims(ATTACKING_B);
			} else if (attackN && !anims[CROUCHING]){
				velX = 0;
				velY = 0;

				// reset and quick kick
				if (checkIfRunning())
					handleAnims(ATTACKING_N);

				// otherwise, idle
			} else {

				velX = 0;
				velY = 0;

				// reset anims that are instantaneous (only activate when pressed)
				anims[CROUCHING] = false;
				anims[PARRYING_L] = false;
				anims[PARRYING_R] = false;
				anims[JUMPING] = false;
				anims[FRONT_FLIPPING] = false;
				anims[BACK_FLIPPING] = false;

				// if only dummy boolean is active, then idle
				if (anims[DUMMY]) {
					handleAnims(IDLING);
				}	

			}
			// otherwise, player is in air
		} else {
			// Check appropriate controls based on which player is controlling this Ryu
			boolean attackG = (playerNumber == 1) ? game.getKeyManager().G : game.getKeyManager().N4;
			boolean attackH = (playerNumber == 1) ? game.getKeyManager().H : game.getKeyManager().N5;
			boolean attackB = (playerNumber == 1) ? game.getKeyManager().B : game.getKeyManager().N1;

			anims[PARRYING_R] = false;
			anims[PARRYING_L] = false;

			// update frames
			jump.tick();
			front_flip.tick();
			back_flip.tick();

			// if moving left
			if (velX < 0) {
				// back flip
				handleAirAttacks(back_flip, BACK_FLIPPING, attackG, attackH, attackB);
				// if moving right
			} else if (velX > 0) {
				// front flip
				handleAirAttacks(front_flip, FRONT_FLIPPING, attackG, attackH, attackB);
				// otherwise
			} else {
				// jump vertically
				handleAirAttacks(jump, JUMPING, attackG, attackH, attackB);
			}
		}

		// if attacking, stop moving
		if (anims[ATTACKING_G] || anims[ATTACKING_H] || anims[ATTACKING_B] || anims[ATTACKING_N]) {
			velX = 0;
			velY = 0;
		}

		// reset ground attacks
		resetAnim(attack_G, ATTACKING_G);
		resetAnim(attack_H, ATTACKING_H);
		resetAnim(attack_B, ATTACKING_B);
		resetAnim(attack_N, ATTACKING_N);

		// reset air attacks
		resetAnim(attack_A_G, ATTACKING_A_G);
		resetAnim(attack_A_H, ATTACKING_A_H);
		resetAnim(attack_A_B, ATTACKING_A_B);

		// if on the floor...
		if (y == 280) {
			// if attacking...
			if (anims[ATTACKING_A_G] || anims[ATTACKING_A_H] || anims[ATTACKING_A_B]) {
				anims[ATTACKING_A_G] = false;
				anims[ATTACKING_A_H] = false;
				anims[ATTACKING_A_B] = false;
			}
		}

		collisions();

		// update horizontal pos.
		x += velX;

		// if in air, fall
		if (y < 280)
			fall();

		// if player clips through floor, set y to floor
		if (y > 280)
			y = 280;

		// check edges of screen
		checkWalls();
		
	}

	public void checkWalls(){
		// if x < 0...
		if (x < 0){
			// do not go past left edge
			x = 0;
		}

		// if x > 0
		if (x > Game.WIDTH * 2){
			// do not go past right edge
			x = Game.WIDTH * 2;
		}
	}

	public void fall(){

		// update y speed
		velY += GRAV;

		// if greater than max speed, just equal max speed
		if (velY > TERMINAL_VELOCITY){
			velY = TERMINAL_VELOCITY;
		} 

		// if moving left...		
		if (velX < 0) {
			// backflip
			if (back_flip.getFrame() >= 2 && back_flip.getFrame() <= 3) {
				velY = 0;
			}
		}

		// if moving right...
		if (velX > 0) {
			// front flip
			if (front_flip.getFrame() >= 2 && front_flip.getFrame() <= 3) {
				velY = 0;
			}
		}

		// if horizontally still...
		if (velX == 0) {
			// jump anims
			if (jump.getFrame() >= 3 && jump.getFrame() <= 4) {
				velY = 0;
			}
		}

		// update y by y-speed
		y += velY;


	}

	public void collisions() {
		// Collision handling now managed by GameState
		// This method is kept for compatibility but damage is handled externally
	}

	@Override
	public void render(Graphics g) {

		// 2d graphics for transformations
		Graphics2D g2d = (Graphics2D) g;	

		// when hit, vibrate randomly
		if (hurting) {
			int k = rand.nextInt(3);	
			g2d.translate(-k, k);
		}

		// draw shadow
		g.setColor(new Color(0,0,0, 125));
		g.fillOval((int) x - 4, 188 * Game.SCALE, 64, 16);

		// If Ryu is facing left, flip the sprite horizontally
		if (!facingRight) {
			drawRyuFacingLeft(g);
		} else {
			drawRyuFacingRight(g);
		}

		// draw hitboxes

		/*		g.setColor(Color.WHITE);
		g.drawRect(getHitBounds().x, getHitBounds().y, getHitBounds().width, getHitBounds().height);

		g.setColor(Color.RED);
		g.drawRect(getAttackBounds().x, getAttackBounds().y, getAttackBounds().width, getAttackBounds().height);*/

	}

	private void drawRyuFacingRight(Graphics g) {
		// Original rendering code for Ryu facing right
		if (anims[PARRYING_R])
			g.drawImage(getCurrentAnimFrame(), (int) (x - 9), (int) (y - 3), null);	

		else if (anims[PARRYING_L])
			g.drawImage(getCurrentAnimFrame(), (int) (x - 4), (int) y, null);	

		else if (anims[CROUCHING])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y + 36, null);	

		else if (anims[JUMPING])
			g.drawImage(getCurrentAnimFrame(), (int) x - 3, (int) y - 20, null);

		else if (anims[BACK_FLIPPING])
			g.drawImage(getCurrentAnimFrame(), (int) x - 15, (int) y - 15, null);

		else if (anims[FRONT_FLIPPING])
			g.drawImage(getCurrentAnimFrame(), (int) x - 15, (int) y - 15, null);

		else if (anims[ATTACKING_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y + 3), null);

		else if (anims[ATTACKING_H])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y + 3), null);

		else if (anims[ATTACKING_B])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y - 3), null);

		else if (anims[ATTACKING_N])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y - 4), null);

		else if (anims[ATTACKING_C_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y + 37, null);	

		else if (anims[ATTACKING_A_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y - 10, null);

		else if (anims[ATTACKING_A_H])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y - 10, null);

		else if (anims[ATTACKING_A_B])
			g.drawImage(getCurrentAnimFrame(), (int) x - 4, (int) y - 5, null);

		else if (anims[HURTING])
			g.drawImage(getCurrentAnimFrame(), (int) (x - 15), (int) (y + 1), null);	

		else 
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y, null);
	}
	
	private void drawRyuFacingLeft(Graphics g) {
		// Flipped rendering code for Ryu facing left
		if (anims[PARRYING_R])
			g.drawImage(getCurrentAnimFrame(), (int) (x + 9), (int) (y - 3), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);	

		else if (anims[PARRYING_L])
			g.drawImage(getCurrentAnimFrame(), (int) (x + 4), (int) y, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);	

		else if (anims[CROUCHING])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y + 36, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);	

		else if (anims[JUMPING])
			g.drawImage(getCurrentAnimFrame(), (int) x + 3, (int) y - 20, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[BACK_FLIPPING])
			g.drawImage(getCurrentAnimFrame(), (int) x + 15, (int) y - 15, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[FRONT_FLIPPING])
			g.drawImage(getCurrentAnimFrame(), (int) x + 15, (int) y - 15, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y + 3), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_H])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y + 3), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_B])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y - 3), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_N])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) (y - 4), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_C_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y + 37, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);	

		else if (anims[ATTACKING_A_G])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y - 10, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_A_H])
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y - 10, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[ATTACKING_A_B])
			g.drawImage(getCurrentAnimFrame(), (int) x + 4, (int) y - 5, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);

		else if (anims[HURTING])
			g.drawImage(getCurrentAnimFrame(), (int) (x + 15), (int) (y + 1), -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);	

		else 
			g.drawImage(getCurrentAnimFrame(), (int) x, (int) y, -getCurrentAnimFrame().getWidth(), getCurrentAnimFrame().getHeight(), null);
	}

	private BufferedImage getCurrentAnimFrame() {

		if (anims[PARRYING_L]) 
			return parry_b.getCurrentFrame();

		else if (anims[PARRYING_R])
			return parry_f.getCurrentFrame();

		else if (anims[CROUCHING]) 
			return crouch.getCurrentFrame();

		else if (anims[JUMPING]) 
			return jump.getCurrentFrame();

		else if (anims[FRONT_FLIPPING]) 
			return front_flip.getCurrentFrame();

		else if (anims[BACK_FLIPPING]) 
			return back_flip.getCurrentFrame();

		else if (anims[ATTACKING_G]) 
			return attack_G.getCurrentFrame();

		else if (anims[ATTACKING_H]) 
			return attack_H.getCurrentFrame();

		else if (anims[ATTACKING_B]) 
			return attack_B.getCurrentFrame();

		else if (anims[ATTACKING_N]) 
			return attack_N.getCurrentFrame();

		else if (anims[ATTACKING_C_G]) 
			return attack_C_G.getCurrentFrame();

		else if (anims[ATTACKING_A_G]) 
			return attack_A_G.getCurrentFrame();

		else if (anims[ATTACKING_A_H]) 
			return attack_A_H.getCurrentFrame();

		else if (anims[ATTACKING_A_B]) 
			return attack_A_B.getCurrentFrame();

		else if (anims[HURTING]) 
			return hurting_G.getCurrentFrame();

		else return idle.getCurrentFrame();

	}

	public Rectangle getHitBounds() {

		if (facingRight) {
			// Right-facing hitboxes
			if (anims[CROUCHING])
				return new Rectangle((int) x, (int) y + 30, 60, 80);	
			else if (anims[ATTACKING_C_G])
				return new Rectangle((int) x, (int) y + 30, 60, 80);	
			else
				return new Rectangle((int) x, (int) y, 60, 110);
		} else {
			// Left-facing hitboxes
			if (anims[CROUCHING])
				return new Rectangle((int) x - 60, (int) y + 30, 60, 80);	
			else if (anims[ATTACKING_C_G])
				return new Rectangle((int) x - 60, (int) y + 30, 60, 80);	
			else
				return new Rectangle((int) x - 60, (int) y, 60, 110);
		}
	}

	public Rectangle getAttackBounds() {

		// add specialized hitbox for each individual attack
		if (facingRight) {
			// Right-facing hitboxes
			if (anims[ATTACKING_G] && attack_G.index == 2)
				return new Rectangle((int) x + 40, (int) y + 10, 60, 30);

			if (anims[ATTACKING_H] && attack_H.index == 2)
				return new Rectangle((int) x + 40, (int) y + 10, 60, 30);

			if (anims[ATTACKING_B] && attack_B.index >= 4 && attack_B.index <= 6)
				return new Rectangle((int) x + 60, (int) y, 60, 50);

			if (anims[ATTACKING_N] && attack_N.index >= 3 && attack_N.index <= 4)
				return new Rectangle((int) x + 60, (int) y + 50, 60, 50);

			if (anims[ATTACKING_C_G] && attack_C_G.index >= 0 && attack_C_G.index <= 1)
				return new Rectangle((int) x + 30, (int) y + 40, 60, 30);

			if (anims[ATTACKING_A_G] && attack_A_G.index >= 2 && attack_A_G.index <= 3)
				return new Rectangle((int) x + 30, (int) y + 20, 60, 50);

			if (anims[ATTACKING_A_H] && attack_A_H.index >= 0 && attack_A_H.index <= 1)
				return new Rectangle((int) x + 30, (int) y + 20, 60, 50);

			if (anims[ATTACKING_A_B] && attack_A_B.index >= 2 && attack_A_B.index <= 3)
				return new Rectangle((int) x + 40, (int) y + 40, 60, 30);
		} else {
			// Left-facing hitboxes (mirrored)
			if (anims[ATTACKING_G] && attack_G.index == 2)
				return new Rectangle((int) x - 100, (int) y + 10, 60, 30);

			if (anims[ATTACKING_H] && attack_H.index == 2)
				return new Rectangle((int) x - 100, (int) y + 10, 60, 30);

			if (anims[ATTACKING_B] && attack_B.index >= 4 && attack_B.index <= 6)
				return new Rectangle((int) x - 120, (int) y, 60, 50);

			if (anims[ATTACKING_N] && attack_N.index >= 3 && attack_N.index <= 4)
				return new Rectangle((int) x - 120, (int) y + 50, 60, 50);

			if (anims[ATTACKING_C_G] && attack_C_G.index >= 0 && attack_C_G.index <= 1)
				return new Rectangle((int) x - 90, (int) y + 40, 60, 30);

			if (anims[ATTACKING_A_G] && attack_A_G.index >= 2 && attack_A_G.index <= 3)
				return new Rectangle((int) x - 90, (int) y + 20, 60, 50);

			if (anims[ATTACKING_A_H] && attack_A_H.index >= 0 && attack_A_H.index <= 1)
				return new Rectangle((int) x - 90, (int) y + 20, 60, 50);

			if (anims[ATTACKING_A_B] && attack_A_B.index >= 2 && attack_A_B.index <= 3)
				return new Rectangle((int) x - 100, (int) y + 40, 60, 30);
		}

		return new Rectangle((int) x, (int) y, 0, 0);
	}

	public void handleAnims(int unchanged){

		// make all false...
		for (int i = 0; i < 19; i++) {
			anims[i] = false;
		}

		// except active anim
		anims[unchanged] = true;
	}

	public void handleAirAttacks(Animation anim, int index, boolean attackG, boolean attackH, boolean attackB) {
		// if g, h, b while in air... set all anims to false except called anim
		if (attackG) {
			handleAnims(ATTACKING_A_G);
		} else if (attackH) {
			handleAnims(ATTACKING_A_H);
		} else if (attackB) {
			handleAnims(ATTACKING_A_B);
		} else if (checkIfRunning()){
			// update anim and set all to false
			anim.tick();
			handleAnims(index);
		}
	}

	public boolean checkIfRunning() {

		// counter
		int i = 0;

		// traverse through anims
		for (boolean b : anims) {
			// if false, increment counter
			if (b == false)
				i++;
		}

		// if all anims are false, return false
		if (i == 19) {
			return false;
		}

		// otherwise, true
		return true;
	}

	public void resetAnim(Animation anim, int frame) {
		// if called anim is played once...
		if (anim.hasPlayedOnce()) {			
			// set anim to false
			anim.setPlayed();
			anims[frame] = false;
		}

	}

	// GETTERS: 

	// get hp
	public int getHealth() {
		return health;
	}

	// get x	
	public int getRyuX() {
		return (int) x;
	}

	// Add damage method to handle attacks
	public void takeDamage() {
		// If not already hurting...
		if (!hurting) {
			handleAnims(HURTING);
			lastTimer = System.currentTimeMillis();
			hurting = true;
			health -= 10;
			
			// Add knockback effect
			if (facingRight) {
				x -= 20; // Knocked back left
			} else {
				x += 20; // Knocked back right
			}
		}
	}

}
