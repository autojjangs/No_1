package entity.item;

import engine.GameState;
import entity.Item;
import engine.DrawManager.SpriteType;


public class Coin extends Item {
    public Coin(final int positionX, final int positionY) {
        super(positionX, positionY, 2, SpriteType.ItemDefault, ItemType.COIN, 10, 0, 0.5);
    }

    /**
     * Apply the item effect that increases coins.
     *
     * @param playerId
     *            the ID of the player receiving the effect.
     */
    @Override
    public void applyEffect(final GameState gameState, final int playerId) {
        if (gameState == null) return;
        gameState.addCoins(playerId, this.effectValue);
    }
}
