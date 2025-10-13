package entity;

import java.util.logging.Logger;
import engine.Core;
import engine.GameState;

public class ItemEffect {
    public static void applyCoinItem(final GameState gameState, final int playerId, int coinAmount) {
        if (gameState == null) return;
        gameState.addCoins(playerId, coinAmount);
    }

    public static void applyHealItem(final GameState gameState, final int playerId, int lifeAmount) {
        if (gameState == null) return;
        gameState.addLife(playerId, lifeAmount);
    }

    public static void applyScoreItem(final GameState gameState, final int playerId, int scoreAmount) {
        if (gameState == null) return;
        gameState.addScore(playerId, scoreAmount);
    }
}
