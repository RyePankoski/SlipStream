package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monster {

    //region Variables

    // Instance control
    private static Monster instance;

    // Core coordinates and movement
    double coorX, coorY, dx, dy;
    public double moveSpeed = 2;

    // State variables
    boolean alive = true;
    boolean angry = false;
    boolean hunting, iSeeThePlayer;
    boolean followingPath = false;
    boolean canFindNextPoint = true, canHuntSearch = true, attacking;

    // Timers and status
    double health, detectMeter;
    double angerTime = 1;
    double timeTillNextHuntSearch;

    // Graphics and resources
    Texture monsterTexture;
    SpriteBatch spriteBatch;
    public Pixmap pixmap;
    Color white;
    Music eerieMusic;

    // Path finding and navigation
    int[][] theMap;
    List<int[]> thePath;
    int thePathSpot = 0;
    int intermediatePointIndex;
    List<double[]> intermediatePoints;
    int[] currentPoint, nextPoint;
    double[] intermediateCoordinate;

    // Utilities
    Random ran;
    Player player;
    ShapeRenderer shapeRenderer;

    double distanceToPlayer;

    int sightRange = 13;

    boolean canIAttack = true;


//endregion

    public Monster(Pixmap pixmap) throws IOException {
        intermediatePoints = new ArrayList<>();
        initVariables();
        initDrawParams(pixmap);
        instance = this;
    }

    public void updateAttackStatus(boolean update){
        canIAttack = update;
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

        if (attacking && canIAttack) {
            attackPlayer();
        } else {

            if (canHuntSearch && hunting) {
                getPathToPlayer();
                followingPath = true;
                canHuntSearch = false;
                timeTillNextHuntSearch = System.currentTimeMillis() + 30000;
            }

            if (!followingPath && player.monsterDistance < 1500) {
                wander();
            } else if (followingPath) {
                followPath();
            }
        }
    }

    public void updateMonster() {
        if (alive) {
            if (System.currentTimeMillis() >= angerTime) angry = false;
            if (System.currentTimeMillis() >= timeTillNextHuntSearch) canHuntSearch = true;
            monitorHealth();
            movementBehavior();
            canISeeThePlayer();
            attackState();
            distanceToPlayer();

            DebugUtility.updateVariable("CanISeeYou?", String.valueOf(iSeeThePlayer));
            DebugUtility.updateVariable("Can I attack?", String.valueOf(canIAttack));


            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setProjectionMatrix(player.getCamera().combined);
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
            shapeRenderer.line((float) coorX, (float) coorY, player.getCoorX(), player.getCoorY());
            shapeRenderer.end();
        }
    }


    public void attackState() {

        double detectSpeed = 0;

        DebugUtility.updateVariable("Detect-Meter", String.valueOf(detectMeter));
        DebugUtility.updateVariable("Attacking?", String.valueOf(attacking));

        if (distanceToPlayer < 64) {
            detectSpeed = .3;
        } else if (distanceToPlayer < 120) {
            detectSpeed = .1;
        } else if (distanceToPlayer < 200) {
            detectSpeed = .05;
        } else if (distanceToPlayer < 300) {
            detectSpeed = 0.01;
        } else {
            detectSpeed = 0.001;
        }

        DebugUtility.updateVariable("Detect Speed", String.valueOf(detectSpeed));


        if (iSeeThePlayer && detectMeter < 3) {
            detectMeter += detectSpeed;
        } else if (detectMeter > 0) {
            detectMeter -= .01;
        }

        if (detectMeter >= 3) {
            SoundEffects.playMusic("detectedNoise");
            attacking = true;
        }

        if (detectMeter <= 0) {
            attacking = false;
            detectMeter = 0;
        }
    }

    public void attackPlayer() {
        double fineness = 2;
        double dx = player.getCoorX() - coorX;
        double dy = player.getCoorY() - coorY;
        double angle = Math.atan2(dx, dy);

        dx = fineness * Math.sin(angle);
        dy = fineness * Math.cos(angle);

        coorX += dx;
        coorY += dy;
    }

    public void canISeeThePlayer() {

        double fineness = 30;
        double sightPointX = coorX;
        double sightPointY = coorY;
        double dx = player.getCoorX() - coorX;
        double dy = player.getCoorY() - coorY;
        double angle = Math.atan2(dx, dy);

        dx = fineness * Math.sin(angle);
        dy = fineness * Math.cos(angle);

        for (int i = 0; i < sightRange; i++) {

            iSeeThePlayer = false;

            if (getPixelColor((int) sightPointX, (int) sightPointY).equals(white)) {
                break;
            }

            if (MathFunctions.distanceFromMe(sightPointX, sightPointY, player.getCoorX(), player.getCoorY()) < 32) {
                iSeeThePlayer = true;
                break;
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle((float) sightPointX, (float) sightPointY, 5);
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
            shapeRenderer.end();

            sightPointX += dx;
            sightPointY += dy;
        }
    }


    public void getPathToPlayer() {
        System.out.println("finding path to player");
        thePathSpot = 0;

        int[] start = {(int) coorX / 32, (int) coorY / 32};

        boolean noValidSpot = true;

        int goalX = 0;
        int goalY = 0;

        while (noValidSpot) {

            goalX = ((int) player.getCoorX() + ran.nextInt(-288, 288)) / 32;
            goalY = ((int) player.getCoorY() + ran.nextInt(-288, 288)) / 32;

            if (getPixelColor(goalX, goalY) != white) {
                noValidSpot = false;
            }
        }

        int[] goal = {goalX, goalY};
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

        if (canFindNextPoint && thePathSpot + 1 < thePath.size()) {
            canFindNextPoint = false;
            currentPoint = thePath.get(thePathSpot);
            nextPoint = thePath.get(thePathSpot + 1);

            double distance = MathFunctions.distanceFromMe(currentPoint[0], currentPoint[1], nextPoint[0], nextPoint[1]);
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
        distanceToPlayer = distance;
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
            SoundEffects.stopMusic("eerieMusic");
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

    public void wander() {

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

    public void drawMyself(SpriteBatch spriteBatch) {
        spriteBatch.draw(monsterTexture, (float) coorX - monsterTexture.getWidth() / 2f, (float) coorY - monsterTexture.getHeight() / 2f);
    }

    public void die() {
        monsterTexture.dispose();
        player.main.killMonster(this);
    }

    public void initDrawParams(Pixmap pixmap) throws IOException {
        this.pixmap = pixmap;
        shapeRenderer = new ShapeRenderer();
        theMap = AStar.imageToGrid("assets/CollisionMap/collisionMap.png");
        monsterTexture = new Texture(Gdx.files.internal("MonsterTex/monster.png"));
        white = new Color(255, 255, 255);
    }

    public void initVariables() {
        dx = 1;
//        coorX = 800;
//        coorY = 9000;
        coorX = 7800;
        coorY = 300;

        health = 100;
        ran = new Random();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static Monster getInstance() {
        return instance;
    }
}
