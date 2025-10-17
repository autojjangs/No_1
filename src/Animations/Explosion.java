package Animations;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Explosion {

    private static final int NUM_PARTICLES = 40;
    private Particle[] particles;
    private boolean active;
    private boolean enemy;
    private int size;

    private static final Random random = new Random();

    public Explosion(double startX, double startY, boolean enemy, boolean finalExplosion) {
        this.particles = new Particle[NUM_PARTICLES];
        this.active = true;
        this.enemy = enemy;
        if(finalExplosion)
            this.size = 20;
        else
            this.size = 4;

        for (int i = 0; i < NUM_PARTICLES; i++) {
            double angle = 2 * Math.PI * random.nextDouble();
            double speed = 2 + random.nextDouble() * 2;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed;

            Color color = new Color(255, random.nextInt(150), 0, 255);
            int life = 60;

            particles[i] = new Particle(startX, startY, dx, dy, color, life);
        }
    }

    public void update() {
        if (!active) return;

        boolean anyAlive = false;
        for (Particle p : particles) {
            if (!p.active) continue;

            // update position
            p.x += p.dx;
            p.y += p.dy;

            // gravity and damping
            p.dy += 0.1;
            p.dx *= 0.98;
            p.dy *= 0.98;

            // fade color
            int alpha = (int) (255 * ((double) p.life / 60));
            alpha = Math.max(alpha, 0);
            p.color = new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), alpha);

            // decrease life
            p.life--;
            if (p.life <= 0) p.active = false;

            if (p.active) anyAlive = true;
        }

        // deactivate explosion if all particles are dead
        if (!anyAlive) active = false;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public boolean enemy(){return this.enemy;}

    public boolean isActive() {
        return active;
    }

    public int getSize(){return this.size;}

    public static class Particle {
        public double x, y;
        public double dx, dy;
        public Color color;
        public int life;
        public boolean active;

        public Particle(double x, double y, double dx, double dy, Color color, int life) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.color = color;
            this.life = life;
            this.active = true;
        }
    }
}

