package Animations;

import java.awt.*;
import java.util.Random;

public class MenuSpace {

    public final Star[] stars;
    private final Random rand = new Random();
    private int[][] positions;
    private int numStars;
    private Color color;
    private int speed;

    public MenuSpace(int numStars) {

        this.numStars = numStars;
        this.stars = new Star[this.numStars];
        this.positions = new int[this.numStars][2];
        this.color = Color.YELLOW;
        this.speed = 1;

        for (int i = 0; i < this.numStars; i++) {

            stars[i] = new Star(rand.nextInt(0,448), rand.nextInt(0, 520));
            positions[i][0] = stars[i].x;
            positions[i][1] = stars[i].y;
        }

    }

    public void updateStars() {
        int i = 0;
        for (MenuSpace.Star star : stars) {
            star.y += this.speed;
            positions[i][1] = star.y;

            if (star.y >= 525) {
                star.y = 0;
                positions[i][1] = 0;
            }
            i++;
        }
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(final int state){
        switch(state){
            case 0:
                color = Color.YELLOW;
                break;

            case 1:
                color = Color.WHITE;
                break;

            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.PINK;
                break;

            case 4:
                color = Color.RED;
                break;
        }
    }

    public void setSpeed(final boolean exit){
        if(exit)
            this.speed = 3;
        else
            this.speed = 1;
    }


    public int[][] getStarLocations(){
        return this.positions;
    }

    public int getNumStars(){
        return this.numStars;
    }

    private static class Star {
        int x, y;

        Star(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
}

