package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a ship, to be controlled by the player.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class Ship extends Entity {

	/** Time between shots. */
	private static final int SHOOTING_INTERVAL = 750;
	/** Speed of the bullets shot by the ship. */
	private static final int BULLET_SPEED = -6;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;

	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	// 2P mode: id number to specifying which player this ship belongs to - 0 =
	// unknown, 1 = P1, 2 = P2
	private int playerId = 0; // 0 = unknown, 1 = P1, 2 = P2

	/**
	 * Constructor, establishes the ship's properties.
	 *
	 * @param positionX
	 *                  Initial position of the ship in the X axis.
	 * @param positionY
	 *                  Initial position of the ship in the Y axis.
	 */
	public Ship(final int positionX, final int positionY) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
	}

	// 2P mode: create and tag with a team in one shot
	public Ship(final int positionX, final int positionY, final Team team) {
		this(positionX, positionY);
		this.setTeam(team); // uses Entity.setTeam
		this.playerId = (team == Team.PLAYER1 ? 1 : team == Team.PLAYER2 ? 2 : 0);
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		this.positionX += SPEED;
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		this.positionX -= SPEED;
	}

	/**
	 * Shoots a bullet upwards.
	 *
	 * @param bullets
	 *                List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			Bullet b = (BulletPool.getBullet(positionX + this.width / 2,
					positionY, BULLET_SPEED)); // shoots bullet and tags with shooter's team

			b.setOwnerPlayerId(this.getPlayerId()); // 2P mode:owner tag for bullet
			b.setTeam(this.getTeam()); // bullet inherits shooter's team
			bullets.add(b);
			return true;
		}
		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else
			this.spriteType = SpriteType.Ship;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is destroyed.
	 *
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 *
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return SPEED;
	}

	// 2P mode: adding playerId getter and setter
	public final int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int id) {
		this.playerId = id;
	}

}