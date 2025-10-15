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
     * @param width
     *            Requested size of the bullet width.
     * @param height
     *            Requested size of the bullet height.
     * @param team
     *            Requested team type.
     * @return Requested bullet.
     */
    public static Bullet getBullet(final int positionX,
                                   final int positionY, final int speed, final int width, final int height, final Team team) {
        Bullet bullet;
        if (!pool.isEmpty()) {
            bullet = pool.iterator().next();
            pool.remove(bullet);
            bullet.setPositionX(positionX - width / 2);
            bullet.setPositionY(positionY);
            bullet.setSpeed(speed);
            bullet.setSize(width, height);  // bullet size
            bullet.setTeam(team);	// team setting
        } else {
            bullet = new Bullet(positionX, positionY, width, height, speed);
            bullet.setPositionX(positionX - width / 2);
            bullet.setSize(width, height); // bullet size
            bullet.setTeam(team); // team setting
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