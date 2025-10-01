package Animations;

import java.util.Random;
/*
* The basic background stars effect during the game
* */
public class BasicGameSpace {

    public final Star[] stars;
    private final Random rand = new Random();
    private int[][] positions;
    private int speed;
    private int numStars;

    public BasicGameSpace(int numStars) {

        this.numStars = numStars;
        this.stars = new Star[this.numStars];
        this.positions = new int[this.numStars][3];

        for (int i = 0; i < this.numStars; i++) {

            stars[i] = new Star(rand.nextInt(10,448), rand.nextInt(-500, 5), (randomSpeed()) ? 2 : 1);
            positions[i][0] = stars[i].x;
            positions[i][1] = stars[i].y;
            positions[i][2] = stars[i].speed;
        }
    }

    // Update star locations
    public void update() {
        int i = 0;
        for (Star star : stars) {
            star.y += star.speed;
            positions[i][1] = star.y;

            if (star.y >= 525) {
                star.y = 0;
                positions[i][1] = 0;
            }
            i++;
        }
    }

    public int[][] getStarLocations(){
        return this.positions;
    }

    public int getNumStars(){
        return this.numStars;
    }

    public boolean randomSpeed(){
        double r = Math.random();

        return (r < 0.85);
    }

    // Star format
    private static class Star {
        int x, y, speed;

        Star(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }

    }
}
