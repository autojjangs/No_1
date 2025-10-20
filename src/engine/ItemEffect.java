package engine;

import java.util.logging.Logger;

public class ItemEffect {

    private static final Logger logger = Core.getLogger();

    public enum ItemEffectType {
        TRIPLESHOT,
        SCOREBOOST,
        BULLETSPEEDUP
    }

    /*
     * When a player picks up a duration item,
     * attempt to spend the corresponding amount of coins. If the
     * player doesn't have enough coins the effect is not applied.
     */
    private static final int COST_TRIPLESHOT = 100;
    private static final int COST_SCOREBOOST = 0;
    private static final int COST_BULLETSPEEDUP = 75;

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
     * Attempts to spend coins for the purchase; returns true if the spend succeeded.
     *
     * NOTE: This helper uses the existing GameState API (getCoins and addCoins).
     * It subtracts coins by calling addCoins(playerIndex, -cost). If your GameState
     * provides a dedicated "spend" or "removeCoins" method you can replace the call.
     */
    private static boolean trySpendCoins(final GameState gameState, final int playerId, final int cost) {
        if (gameState == null) return false;
        if (cost <= 0) return true; // free or invalid cost treated as free

        final int playerIndex = getPlayerIndex(playerId);
        final int current = gameState.getCoins();

        // Use the dedicated spend method implemented in GameState
        if (gameState.spendCoins(playerIndex, cost)) {
            logger.info("Player " + playerId + " spent " + cost + " coins. before: " + current + ", after: " + gameState.getCoins());
            return true;
        } else {
            logger.info("Player " + playerId + " cannot afford cost " + cost + ". current coins: " + current);
            return false;
        }
    }
    /**
     * Applies the TripleShot timed effect to the specified player.
     * Returns true if purchase succeeded and effect applied, false if insufficient coins.
     */
    public static boolean applyTripleShot(final GameState gameState, final int playerId, int effectValue, int duration, Integer overrideCost) {
        if (gameState == null) return false;
        final int cost = (overrideCost != null) ? overrideCost : COST_TRIPLESHOT;

        if (!trySpendCoins(gameState, playerId, cost)) {
            return false;
        }
        int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.TRIPLESHOT, effectValue, duration);
        logger.info("[ItemEffect - TRIPLESHOT] Player " + playerId + " applied for " + duration + "s.");
        return true;
    }

    public static boolean applyScoreBoost(final GameState gameState, final int playerId, int effectValue, int duration, Integer overrideCost) {
        if (gameState == null) return false;
        final int cost = (overrideCost != null) ? overrideCost : COST_SCOREBOOST;

        if (!trySpendCoins(gameState, playerId, cost)) {
            return false;
        }
        final int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.SCOREBOOST, effectValue, duration);
        logger.info("[ItemEffect - SCOREBOOST] Player " + playerId + " applied for " + duration + "s. Score gain will be multiplied by " + effectValue + ".");
        return true;
    }

    /**
     * Applies the BulletSpeedUp timed effect to the specified player.
     */
    public static boolean applyBulletSpeedUp(final GameState gameState, final int playerId, int effectValue, int duration, Integer overrideCost) {
        if (gameState == null) return false;
        final int cost = (overrideCost != null) ? overrideCost : COST_BULLETSPEEDUP;

        if (!trySpendCoins(gameState, playerId, cost)) {
            return false;
        }
        int playerIndex = getPlayerIndex(playerId);

        // apply duration
        gameState.addEffect(playerIndex, ItemEffectType.BULLETSPEEDUP, effectValue, duration);
        logger.info("[ItemEffect - BULLETSPEEDUP] Player " + playerId + " applied for " + duration + "s.");
        return true;
    }
    public static boolean applyTripleShot(final GameState gameState, final int playerId, int effectValue, int duration) {
        return applyTripleShot(gameState, playerId, effectValue, duration, null);
    }

    public static boolean applyScoreBoost(final GameState gameState, final int playerId, int effectValue, int duration) {
        return applyScoreBoost(gameState, playerId, effectValue, duration, null);
    }

    public static boolean applyBulletSpeedUp(final GameState gameState, final int playerId, int effectValue, int duration) {
        return applyBulletSpeedUp(gameState, playerId, effectValue, duration, null);
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