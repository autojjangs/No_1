package engine;

import java.util.Arrays;

/**
 * Implements a high score record.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class Score implements Comparable<Score> {

    /** Player's name. */
    private String name;
    /** Score points. */
    private int score;

    /** per-player breakdown */
    private int[] playerScores;
    private int[] playerBullets;
    private int[] playerKills;


    /**level reached and lives left */
    private int levelReached;
    private int livesRemaining;

    /** 1P/2P mode*/
    private String mode;
    /**
     * Constructor.
     *
     * @param name  Player name, three letters.
     * @param score Player score.
     */
    public Score(final String name, final int score, final String mode) {
        this.name = name;
        this.score = score;
        this.mode = mode; // add 1P/2P mode
    }

    /**
     * NEW Constructor: (team co-op)
     */
    public Score(final String name, final GameState gs, final String mode) {
        this.name = name;
        this.score = gs.getScore();
        this.levelReached = gs.getLevel();
        this.livesRemaining = gs.getLivesRemaining();
        this.mode = mode; // add 1P/2P mode

        int n = GameState.NUM_PLAYERS;
        this.playerScores = new int[n];
        this.playerBullets = new int[n];
        this.playerKills = new int[n];


        for (int i = 0; i < n; i++) {
            this.playerScores[i] = gs.getScore(i);
            this.playerBullets[i] = gs.getBulletsShot(i);
            this.playerKills[i] = gs.getShipsDestroyed(i);
        }
    }

    /**
     * Getter for the player's name.
     *
     * @return Name of the player.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Getter for the player's score.
     *
     * @return High score.
     */
    public final int getScore() {
        return this.score;
    }

    /** Getter for mode*/
    public String getMode() {
        return this.mode;
    }
    /**
     * Getter for:
     * level reached
     * lives left
     *
     * per-player breakdown
     */
    public final int getLevelReached() {
        return this.levelReached;
    }

    public final int getLivesRemaining() {
        return this.livesRemaining;
    }

    // Per-player (null-safe for legacy scores)
    public final int getPlayerScore(int pid) {
        return playerScores != null ? playerScores[pid] : 0;
    }

    public final int getPlayerBullets(int pid) {
        return playerBullets != null ? playerBullets[pid] : 0;
    }

    public final int getPlayerKills(int pid) {
        return playerKills != null ? playerKills[pid] : 0;
    }

    /**
     * Orders the scores descending by score.
     *
     * @param other
     *              Score to compare the current one with.
     * @return Comparison between the two scores. Positive if the current one is
     *         smaller, positive if its bigger, zero if it's the same.
     */

    @Override
    public final int compareTo(final Score other) {
        return Integer.compare(other.getScore(), this.score); // descending
    }

    @Override
    public String toString() {
        return "Score{name='" + name + "', score=" + score +
                ", perPlayer=" + Arrays.toString(playerScores) + "}";
    }

}