package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.w3c.dom.Node;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Monster {

    //region Variables

    Texture monsterTexture;
    SpriteBatch spriteBatch;
    Player player;
    public Pixmap pixmap;
    double coorX;
    double coorY;
    double dx;
    double dy;
    Color white;
    Random ran;
    Music eerieMusic;
    boolean alive = true;
    double health;
    boolean angry = false;
    public double moveSpeed = 2;
    double angerTime = 1;
    int tpAwayTimer = 0;

    boolean followingPath = false;

    ShapeRenderer shapeRenderer;
    private Sound escapeSound;

    int[][] theMap;

    List<int[]> thePath;

    int thePathSpot = 0;

    boolean hunting;

    double timeTillNextHuntSearch;

    boolean canFindNextPoint = true, canHuntSearch = true;

    int intermediatePointIndex;

    List<double[]> intermediatePoints;

    int[] currentPoint;

    int[] nextPoint;

    double[] intermediateCoordinate;


//endregion

    public Monster(Pixmap pixmap) throws IOException {
        intermediatePoints = new ArrayList<>();
        initVariables();
        initDrawParams(pixmap);
    }

    public void aiManager(boolean huntStatus) {
        hunting = huntStatus;
        if (!hunting) {
            SoundEffects.playSound("escapeSound");
            getPathAwayFromPlayer();
            followingPath = true;
        }
    }

    public void movementBehavior() {

        if (canHuntSearch && hunting) {
            followingPath = true;
            canHuntSearch = false;
            timeTillNextHuntSearch = System.currentTimeMillis() + 30000;
            getPathToPlayer();
        }

        if (!followingPath && player.monsterDistance < 1500) {
            move();
        } else if (followingPath){
            followPath();
        }
    }

    public void updateMonster() {

        System.out.println(player.monsterDistance);

        if (alive) {
            if (System.currentTimeMillis() >= angerTime) angry = false;
            if (System.currentTimeMillis() >= timeTillNextHuntSearch) canHuntSearch = true;
            monitorHealth();
            movementBehavior();
            drawMyself();
            distanceToPlayer();
        }
    }

    public void getPathToPlayer() {
        System.out.println("finding path to player");
        thePathSpot = 0;
        int[] start = {(int) coorX / 32, (int) coorY / 32};
        int[] goal = {(int) player.getCoorX() / 32, (int) player.getCoorY() / 32};
        thePath = AStar.aStar(start, goal, theMap);
    }

    public void getPathAwayFromPlayer() {
        System.out.println("finding path away from player");
        thePathSpot = 0;
        int[] start = {(int) coorX / 32, (int) coorY / 32};
        int[] goal = {800 / 32, 9000 / 32};
        thePath = AStar.aStar(start, goal, theMap);
    }

    public void followPath() {
//        System.out.println(player.monsterDistance);

        if (canFindNextPoint && thePathSpot + 1 < thePath.size()) {
            canFindNextPoint = false;
            currentPoint = thePath.get(thePathSpot);
            nextPoint = thePath.get(thePathSpot + 1);

            double distance = MathFunctions.distanceFromMe(nextPoint[0], currentPoint[1], nextPoint[0], nextPoint[1]);
            intermediatePoints = interpolatePoints(currentPoint[0], currentPoint[1], nextPoint[0], nextPoint[1], (int) distance / 2);

            intermediatePointIndex = 0;
            setPosition(currentPoint[0], currentPoint[1]);
        }

        if (intermediatePointIndex < intermediatePoints.size()) {
            intermediateCoordinate = intermediatePoints.get(intermediatePointIndex);
            setPosition(intermediateCoordinate[0], intermediateCoordinate[1]);
            intermediatePointIndex++;
        }

        if (intermediatePointIndex >= intermediatePoints.size()) {
            setPosition(nextPoint[0], nextPoint[1]);
            thePathSpot++;

            canFindNextPoint = true;
        }

        if (thePathSpot == thePath.size()) {
            followingPath = false;
        }
    }

    public static List<double[]> interpolatePoints(double x1, double y1, double x2, double y2, int numPoints) {
        List<double[]> points = new ArrayList<>();
        for (int i = 1; i <= numPoints; i++) {
            double t = (double) i / (numPoints + 1); // Divide interval into (numPoints + 1) segments
            double x = x1 + t * (x2 - x1);
            double y = y1 + t * (y2 - y1);
            points.add(new double[]{x, y});
        }
        return points;
    }

    public void distanceToPlayer() {

        double max = 2000;
        double distance = player.monsterDistance;
        double percent = (max - distance) / max;

        if (distance < max) {
            SoundEffects.playMusicWithParameters("eerieMusic", (float) percent, 100f);
        } else {
            SoundEffects.stopMusic("eerieMusic");
        }
    }

    public void setPosition(double x, double y) {
        coorX = x;
        coorY = y;
    }

    private Color getPixelColor(int x, int y) {
        int pixel = pixmap.getPixel(x, pixmap.getHeight() - y);

        float r = ((pixel >> 24) & 0xFF) / 255f; // Red component
        float g = ((pixel >> 16) & 0xFF) / 255f; // Green component
        float b = ((pixel >> 8) & 0xFF) / 255f;  // Blue component
        float a = (pixel & 0xFF) / 255f;         // Alpha component

        return new Color(r, g, b, a);
    }

    public void monitorHealth() {
        if (health < 100) {
            health += 0.02;
        }
        if (health <= 0) {
            health = 0;
            alive = false;
            eerieMusic.stop();
            die();
        }
    }

    public void hitByMelee() {
        health -= 4;
        angry = true;
        angerTime = System.currentTimeMillis() + 5000;
    }

    public void hitByBullet(Weapon weapon) {
        angry = true;
        angerTime = System.currentTimeMillis() + 7000;
        health -= weapon.getDamage();
    }

    public double getCoorX() {
        return coorX;
    }

    public double getCoorY() {
        return coorY;
    }

    public void move() {

        int textureSize = monsterTexture.getWidth() / 2;

        moveSpeed = 1;
        moveSpeed = angry ? moveSpeed * 2 : moveSpeed;

        //region out of bounds detection
        if (coorX + dx > pixmap.getWidth()) {
            dx *= -1;
            dy += ran.nextDouble(-2, 2);
        } else if (coorX + dx < 0) {
            dx *= -1;
            dy += ran.nextDouble(-2, 2);
        }
        if (coorY + dy > pixmap.getHeight()) {
            dy *= -1;
            dx += ran.nextDouble(-2, 2);

        } else if (coorY + dy < 0) {
            dy *= -1;
            dx += ran.nextDouble(-2, 2);
        }
        //endregion

        //region wall collision
        if (dx > 0) {
            dx = moveSpeed;
            if (getPixelColor((int) coorX + textureSize, (int) coorY).equals(white)) {
                dx *= -1;
                dy += ran.nextDouble(-2, 2);
            }
        } else if (dx < 0) {
            dx = -moveSpeed;
            if (getPixelColor((int) coorX - textureSize, (int) coorY).equals(white)) {
                dx *= -1;
                dy += ran.nextDouble(-2, 2);
            }
        }

        if (dy > 0) {
            dy = moveSpeed;
            if (getPixelColor((int) coorX, (int) coorY + textureSize).equals(white)) {
                dy *= -1;
                dx += ran.nextDouble(-2, 2);
            }
        } else if (dy < 0) {
            dy = -moveSpeed;
            if (getPixelColor((int) coorX, (int) coorY - textureSize).equals(white)) {
                dy *= -1;
                dx += ran.nextDouble(-2, 2);
            }
        }
        //endregion

        coorX += dx;
        coorY += dy;

        if (dx < -moveSpeed) {
            dx = -moveSpeed;
        }
        if (dx > moveSpeed) {
            dx = moveSpeed;
        }
        if (dy < -moveSpeed) {
            dy = -moveSpeed;
        }
        if (dy > moveSpeed) {
            dy = moveSpeed;
        }
    }

    public void drawMyself() {
        spriteBatch.setProjectionMatrix(player.getCamera().combined);
        shapeRenderer.setProjectionMatrix(player.getCamera().combined);

        spriteBatch.begin();
        spriteBatch.draw(monsterTexture, (float) coorX - monsterTexture.getWidth() / 2f, (float) coorY - monsterTexture.getHeight() / 2f);
        spriteBatch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(255, 0, 0, (float) (1 - (health / 100)));
        shapeRenderer.circle((float) coorX, (float) coorY, 10);
        shapeRenderer.end();
    }

    public void die() {
        eerieMusic.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        monsterTexture.dispose();
        player.main.killMonster(this);
    }

    public void initDrawParams(Pixmap pixmap) throws IOException {
        this.pixmap = pixmap;
        theMap = AStar.imageToGrid("assets/CollisionMap/collisionMap.png");
        monsterTexture = new Texture(Gdx.files.internal("MonsterTex/monster.png"));
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);
    }

    public void initVariables() {
        dx = 1;
        coorX = 800;
        coorY = 9000;

        health = 100;
        ran = new Random();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
