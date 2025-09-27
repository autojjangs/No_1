//package entity;
//
//import java.awt.Color;
//
//import engine.DrawManager.SpriteType;
//
///**
// * Implements a Item that moves vertically up or down.
// *
// * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
// *
// */
//public class Item extends Entity {
//
//    /**
//     * Speed of the Item, positive or negative depending on direction -
//     * positive is down.
//     */
//    private int speed;
//
//    /**
//     * Constructor, establishes the Item's properties.
//     *
//     * @param positionX
//     *            Initial position of the Item in the X axis.
//     * @param positionY
//     *            Initial position of the Item in the Y axis.
//     * @param speed
//     *            Speed of the Item, positive or negative depending on
//     *            direction - positive is down.
//     */
//    public Item(final int positionX, final int positionY, final int speed) {
//        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);
//
//        this.speed = speed;
//        setSprite();
//    }
//
//    /**
//     * Sets correct sprite for the Item, based on speed.
//     */
//    public final void setSprite() {
//        if (speed < 0)
//            this.spriteType = SpriteType.Item;
//        else
//            this.spriteType = SpriteType.Item;
//    }
//
//    /**
//     * Updates the Item's position.
//     */
//    public final void update() {
//        this.positionY += this.speed;
//    }
//
//    /**
//     * Setter of the speed of the Item.
//     *
//     * @param speed
//     *            New speed of the Item.
//     */
//    public final void setSpeed(final int speed) {
//        this.speed = speed;
//    }
//
//    /**
//     * Getter for the speed of the Item.
//     *
//     * @return Speed of the Item.
//     */
//    public final int getSpeed() {
//        return this.speed;
//    }
//}
