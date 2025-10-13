package entity;

import java.util.logging.Logger;
import engine.Core;
import engine.GameState;

public class ItemEffect {
    public static void applyCoinItem(final GameState gameState, final int playerId, int coinAmount) {
        if (gameState == null) return;

        gameState.addCoins(getPlayerIndex(playerId), coinAmount);
    }

    public static void applyHealItem(final GameState gameState, final int playerId, int lifeAmount) {
        if (gameState == null) return;

        gameState.addLife(getPlayerIndex(playerId), lifeAmount);
    }

    public static void applyScoreItem(final GameState gameState, final int playerId, int scoreAmount) {
        if (gameState == null) return;

        gameState.addScore(getPlayerIndex(playerId), scoreAmount);
    }

    /**
     * Converts a playerId (unknown : 0, player1 : 1, player2 : 2) to the corresponding array index.
     *
     * @param playerId
     *            ID of the player (0, 1, 2)
     * @return array index (player1 or unknown : 0, player2 : 1)
     */
    private static int getPlayerIndex(final int playerId) {
        return (playerId == 2) ? 1 : 0;
    }
}
