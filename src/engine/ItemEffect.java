package engine;

import java.util.logging.Logger;

public class ItemEffect {

    private static final Logger logger = Core.getLogger();

    public enum ItemEffectType {
        TRIPLESHOT,
        SCOREBOOST,
        BULLETSPEEDUP
    }

    /**=========================SINGLE USE=================================**/

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
        final int beforeCoin = gameState.getCoins();

        gameState.addCoins(playerIndex, coinAmount);

        logger.info("Player " + playerId + " added " + coinAmount + " coins. before : " + beforeCoin + ", after : " + gameState.getCoins());
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
                // if adding life exceeds max, add score and coin instead
                gameState.addCoins(getPlayerIndex(playerId), lifeAmount * 20);
                gameState.addScore(getPlayerIndex(playerId), lifeAmount * 20);
            } else {
                gameState.addLife(getPlayerIndex(playerId), lifeAmount);
            }
        } else { // 1p mode
            if (gameState.get1PlayerLives() + lifeAmount > 3) {
                // if adding life exceeds max, add score and coin instead
                gameState.addScore(getPlayerIndex(playerId), lifeAmount * 20);
                gameState.addCoins(getPlayerIndex(playerId), lifeAmount * 20);
            } else {

                gameState.addLife(getPlayerIndex(playerId), lifeAmount);
            }
        }


        logger.info("Player added " + lifeAmount + " lives. before : " + beforeLife + ", after : " + gameState.getLivesRemaining());
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

    /**========================= DURATION ITEM =================================**/

    /**
     * Applies the TripleShot timed effect to the specified player.
     */
    public static void applyTripleShot(final GameState gameState, final int playerId, int effectValue, int duration) {
        if (gameState == null) return;
        int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.TRIPLESHOT, effectValue, duration);
    }

    public static void applyScoreBoost(final GameState gameState, final int playerId, int effectValue, int duration){
        if (gameState == null) return;
        final int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.SCOREBOOST, effectValue, duration);
        logger.info("[ItemEffect - SCOREBOOST] Player " + playerId + " applied for " + duration + "s. Score gain will be multiplied by " + effectValue + ".");
    }

    /**
     * Applies the BulletSpeedUp timed effect to the specified player.
     */
    public static void applyBulletSpeedUp(final GameState gameState, final int playerId, int effectValue, int duration) {
        if (gameState == null) return;
        int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.BULLETSPEEDUP, effectValue, duration);
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