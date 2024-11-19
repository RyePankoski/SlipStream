package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.*;
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
    public double moveSpeed = 5;

    double angerTime = 1;

    ShapeRenderer shapeRenderer;

//endregion

    public Monster(Pixmap pixmap) {
        initVariables();
        initSounds();
        initDrawParams(pixmap);
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public void updateMonster() {

        amIAngry();
        monitorHealth();
        if (alive) {
            move();
            drawMyself();
            distanceToPlayer();
            if (health < 100) {
                health += 0.02;
            }
        }


        if (coorX > 5000 || coorY > 5000 || coorX < 0 || coorY < 0) {
            System.out.println("Im off the map");
        }
    }

    public void distanceToPlayer() {
        double x1 = coorX;
        double y1 = coorY;
        double x2 = player.getCoorX();
        double y2 = player.getCoorY();

        double max = 3000;

        double newX = x2 - x1;
        double newY = y2 - y1;
        double distance = Math.sqrt((newX * newX) + (newY * newY));
        double percent = (max - distance) / max;


        if (distance < max) {
            if (eerieMusic.isPlaying()) {
                eerieMusic.setVolume((float) percent);
            } else {
                eerieMusic.play();
            }
        }
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
        angerTime = System.nanoTime() + player.main.secondsToNano(5);
    }

    public void hitByBullet(Weapon weapon) {
        angry = true;
        angerTime = System.nanoTime() + player.main.secondsToNano(7);
        health -= weapon.getDamage();
    }

    public double getCoorX() {
        return coorX;
    }

    public double getCoorY() {
        return coorY;
    }

    public void amIAngry() {

        if (System.nanoTime() >= angerTime) {
            angry = false;
        }
    }

    public void move() {

        int textureSize = monsterTexture.getWidth() / 2 - 50;

        moveSpeed = 5;
        moveSpeed = angry ? moveSpeed * 3 : moveSpeed;

        if (coorX + dx > pixmap.getWidth()) {
            dx *= -1;
            dy += ran.nextDouble(-5, 5);
        } else if (coorX + dx < 0) {
            dx *= -1;
            dy += ran.nextDouble(-5, 5);
        }

        if (coorY + dy > pixmap.getHeight()) {
            dy *= -1;
            dx += ran.nextDouble(-5, 5);

        } else if (coorY + dy < 0) {
            dy *= -1;
            dx += ran.nextDouble(-5, 5);
        }

        if (dx > 0) {
            dx = moveSpeed;
            if (getPixelColor((int) coorX + textureSize, (int) coorY).equals(white)) {
                dx *= -1;
                dy += ran.nextDouble(-5, 5);
            }
        } else if (dx < 0) {
            dx = -moveSpeed;
            if (getPixelColor((int) coorX - textureSize, (int) coorY).equals(white)) {
                dx *= -1;
                dy += ran.nextDouble(-5, 5);
            }
        }

        if (dy > 0) {
            dy = moveSpeed;
            if (getPixelColor((int) coorX, (int) coorY + textureSize).equals(white)) {
                dy *= -1;
                dx += ran.nextDouble(-5, 5);
            }
        } else if (dy < 0) {
            dy = -moveSpeed;
            if (getPixelColor((int) coorX, (int) coorY - textureSize).equals(white)) {
                dy *= -1;
                dx += ran.nextDouble(-5, 5);
            }
        }

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
        shapeRenderer.circle((float) coorX, (float) coorY, 50);
        shapeRenderer.end();

    }

    public void die() {
        eerieMusic.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        monsterTexture.dispose();
        player.main.killMonster(this);
    }

    public void initDrawParams(Pixmap pixmap){

        this.pixmap = pixmap;
        monsterTexture = new Texture(Gdx.files.internal("MonsterTex/monster.png"));
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);
    }

    public void initSounds(){
        eerieMusic = Gdx.audio.newMusic(Gdx.files.internal("MonsterSFX/eerieMusic.mp3"));
    }

    public void initVariables() {
        dx = 5;

        coorX = 2500;
        coorY = 2500;
        health = 100;
        ran = new Random();

    }


}
