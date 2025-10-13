package entity;

import java.util.logging.Logger;
import engine.Core;
import engine.GameState;

public class ItemEffect {
    public static void applyCoinItem(final GameState gameState, final int playerId, int coinAmount) {
        if (gameState == null) return;
        Logger logger = Core.getLogger();
        final int playerIndex = getPlayerIndex(playerId);
        final int beforeCoin = gameState.getCoins(playerIndex);

        gameState.addCoins(playerIndex, coinAmount);

        logger.info("Player " + playerId + " added " + coinAmount + " coins. before : " + beforeCoin + ", after : " + gameState.getCoins(playerIndex));
    }

    public static void applyHealItem(final GameState gameState, final int playerId, int lifeAmount) {
        if (gameState == null) return;
        Logger logger = Core.getLogger();
        final int beforeLife = gameState.getLivesRemaining();

        gameState.addLife(getPlayerIndex(playerId), lifeAmount);
        logger.info("Player added " + lifeAmount + " lives. before : " + beforeLife + ", after : " + gameState.getLivesRemaining());
    }

    public static void applyScoreItem(final GameState gameState, final int playerId, int scoreAmount) {
        if (gameState == null) return;
        Logger logger = Core.getLogger();
        final int playerIndex = getPlayerIndex(playerId);
        final int beforeScore = gameState.getScore(playerIndex);

        gameState.addScore(getPlayerIndex(playerId), scoreAmount);

        logger.info("Player " + playerId + " added " + scoreAmount + " score. before : " + beforeScore + ", after : " + gameState.getScore(playerIndex));

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