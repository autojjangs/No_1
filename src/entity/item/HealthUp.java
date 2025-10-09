package entity.item;


import engine.GameState;
import entity.Item;
import engine.DrawManager.SpriteType;

public class HealthUp extends Item{
    public HealthUp(final int positionX, final int positionY) {
        super(positionX, positionY, 2, SpriteType.ItemDefault, ItemType.HealthUp, 10, 0, 0.5);
    }

    /**

     Apply the item effect that increases Health.*
     @param playerId
     the ID of the player receiving the effect.*/
    @Override
    public void applyEffect(final GameState gameState, final int playerId) {
        if (gameState == null) return;
        gameState.addLife(playerId, this.effectValue);}
}