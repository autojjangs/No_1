package entity;

import java.util.HashSet;
import java.util.Set;
import entity.Entity.Team;

/**
 * Implements a pool of recyclable bullets.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class BulletPool {

	/** Set of already created bullets. */
	private static Set<Bullet> pool = new HashSet<Bullet>();
	//기본 총알 크기
	private static final int BASE_WIDTH = 3*2;	//Todo PlayerC의 총알 크기 변경값 반영
	private static final int BASE_HEIGHT = 5*2;

	/**
	 * Constructor, not called.
	 */
	private BulletPool() {

	}

	/**
	 * Returns a bullet from the pool if one is available, a new one if there
	 * isn't.
	 *
	 * @param positionX
	 *            Requested position of the bullet in the X axis.
	 * @param positionY
	 *            Requested position of the bullet in the Y axis.
	 * @param speed
	 *            Requested speed of the bullet, positive or negative depending
	 *            on direction - positive is down.
	 * @return Requested bullet.
	 */
	//기본 크기 bullet
	public static Bullet getBullet(final int positionX,
								   final int positionY, final int speed, final Team team) {
		return getBullet(positionX, positionY, speed, BASE_WIDTH, BASE_HEIGHT, team);
	}

	public static Bullet getBullet(final int positionX,
								   final int positionY, final int speed, final int width, final int height, final Team team) {
		Bullet bullet;
		if (!pool.isEmpty()) {
			bullet = pool.iterator().next();
			pool.remove(bullet);
			bullet.setPositionX(positionX - width / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeed(speed);
			bullet.setSize(width, height);
			bullet.setTeam(team);	//Team setting
		} else {
			bullet = new Bullet(positionX, positionY, BASE_WIDTH, BASE_HEIGHT, speed);
			bullet.setPositionX(positionX - width / 2);
			bullet.setSize(width, height);
			bullet.setTeam(team); //Team setting
		}
		bullet.setSprite();
		return bullet;
	}

	/**
	 * Adds one or more bullets to the list of available ones.
	 *
	 * @param bullet
	 *            Bullets to recycle.
	 */
	public static void recycle(final Set<Bullet> bullet) {
		pool.addAll(bullet);
	}
}