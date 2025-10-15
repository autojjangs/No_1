package engine;

import java.util.logging.Logger;
import

public class ItemEffect {

    Logger logger = Core.getLogger();
    /**
     * Applies the coin item effect to the specified player.
     *
     * @param gameState
     *            current game state instance.
     * @param playerId
     *            ID of the player to apply the effect to.
     * @param coinAmount
     *            amount of coins to add.
     */
    public static void applyCoinItem(final GameState gameState, final int playerId, int coinAmount) {
        if (gameState == null) return;
        final int playerIndex = getPlayerIndex(playerId);
        final int beforeCoin = gameState.getCoins(playerIndex);

        gameState.addCoins(playerIndex, coinAmount);
        gameState.addScore(getPlayerIndex(playerId), coinAmount);

        logger.info("[ItemEffect - COIN] Player " + playerId + " : " + beforeCoin + " + " + coinAmount + " -> " + gameState.getCoins(playerIndex));
    }

    /**
     * Applies the heal item effect to the specified player.
     *
     * @param gameState
     *            current game state instance.
     * @param playerId
     *            ID of the player to apply the effect to.
     * @param lifeAmount
     *            amount of lives to add.
     */
    public static void applyHealItem(final GameState gameState, final int playerId, int lifeAmount) {
        if (gameState == null) return;
        final int beforeLife = gameState.getLivesRemaining();

        // if 2p mode
        if (gameState.isCoop()) {
            if (gameState.getTeamLives() + lifeAmount > gameState.getTeamLivesCap()) {
                // if adding life exceeds max, convert to score instead
                gameState.addScore(getPlayerIndex(playerId), lifeAmount * 20);
            } else {
                gameState.addLife(getPlayerIndex(playerId), lifeAmount);
            }
        } else { // 1p mode
            if (gameState.get1PlayerLives() + lifeAmount > 3) {
                // if adding life exceeds max, convert to score instead

                gameState.addScore(getPlayerIndex(playerId), lifeAmount * 20);
            } else {
                System.out.println(gameState.get1PlayerLives());
                gameState.addLife(getPlayerIndex(playerId), lifeAmount);
            }
        }


        logger.info("[ItemEffect - COIN] Player " + playerId + " : " + beforeLife + " + " + lifeAmount + " -> " + gameState.getLivesRemaining());
    }

    /**
     * Applies the score item effect to the specified player.
     *
     * @param gameState
     *            current game state instance.
     * @param playerId
     *            ID of the player to apply the effect to.
     * @param scoreAmount
     *            amount of score to add.
     */
    public static void applyScoreItem(final GameState gameState, final int playerId, int scoreAmount) {
        if (gameState == null) return;
        final int playerIndex = getPlayerIndex(playerId);
        final int beforeScore = gameState.getScore(playerIndex);

        gameState.addScore(getPlayerIndex(playerId), scoreAmount);

        logger.info("[ItemEffect - SCORE] Player " + playerId + " : " + beforeScore + " + " + scoreAmount + " -> " + gameState.getScore(playerIndex));
    }

    /**
     * Applies the score item effect to the specified player.
     *
     * @param gameState
     *            current game state instance.
     * @param playerId
     *            ID of the player to apply the effect to.
     * @param effectDuration
     *            duration of item effect.
     */
    public static void applyTripleShot(final GameState gameState, final int playerId, int effectDuration) {
//        if (gameState == null) return;
//        final bool tripleShotEffect = true;
//
//        // if single player - full duration, if 2p - halved duration
//        final int tripleShotDuration = (effectDuration / 2) ? gameState.isCoop() : effectDuration;
//
//        logger.info("[ItemEffect - TRIPLESHOT] Player " + playerId + " has TripleShot for" + tripleShotDuration);
//        // when item effect wears off
//        if tripleShotEffect
//        logger.info("[ItemEffect - TRIPLESHOT] Player " + playerId + ": TripleShot duration wore off.");
    }

    /**
     * Converts a playerId (unknown : 0, player1 : 1, player2 : 2)
     * to the corresponding array index.
     *
     * @param playerId
     *            ID of the player (0, 1, 2)
     * @return array index (player1 or unknown : 0, player2 : 1)
     */
    private static int getPlayerIndex(final int playerId) {
        return (playerId == 2) ? 1 : 0;
    }
}