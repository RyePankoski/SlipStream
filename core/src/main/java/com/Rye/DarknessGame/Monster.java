package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    int tpAwayTimer = 0;

    ShapeRenderer shapeRenderer;
    private Sound escapeSound;

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
        if (System.currentTimeMillis() >= angerTime) angry = false;
        monitorHealth();

        if (alive) {
            move();
            drawMyself();
            distanceToPlayer();
            if (health < 100) {
                health += 0.02;
            }
        }

        if(tpAwayTimer > 0) {
            shouldITeleport();
        }
    }

    public void distanceToPlayer() {

        double max = 2000;
        double distance = player.monsterDistance;
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
        angerTime = System.currentTimeMillis() + 5000;
    }

    public void hitByBullet(Weapon weapon) {
        angry = true;
        angerTime = System.currentTimeMillis() + 7000;
        health -= weapon.getDamage();
        tpAwayTimer = 1000;
    }

    public double getCoorX() {
        return coorX;
    }

    public void teleportAway(){
        health = 100;
        if(eerieMusic.isPlaying()){
            eerieMusic.stop();
        }
        escapeSound.play();
        coorX = ran.nextInt(100,pixmap.getWidth()-100);
        coorY = ran.nextInt(100,pixmap.getHeight()-100);
    }

    public void shouldITeleport(){

        if(tpAwayTimer > 0){
            tpAwayTimer--;
        }
        if(tpAwayTimer == 0){
            teleportAway();
        }

    }

    public double getCoorY() {
        return coorY;
    }

    public void move() {

        int textureSize = monsterTexture.getWidth() / 2;

        moveSpeed = 0;
        moveSpeed = angry ? moveSpeed * 2 : moveSpeed;

        //region out of bounds detection
        if (coorX + dx > pixmap.getWidth()) {
            dx *= -1;
            dy += ran.nextDouble(-2,2);
        } else if (coorX + dx < 0) {
            dx *= -1;
            dy += ran.nextDouble(-2,2);
        }
        if (coorY + dy > pixmap.getHeight()) {
            dy *= -1;
            dx += ran.nextDouble(-2,2);

        } else if (coorY + dy < 0) {
            dy *= -1;
            dx += ran.nextDouble(-2,2);
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

    public void initDrawParams(Pixmap pixmap){
        this.pixmap = pixmap;
        monsterTexture = new Texture(Gdx.files.internal("MonsterTex/monster.png"));
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);
    }

    public void initSounds(){
        eerieMusic = Gdx.audio.newMusic(Gdx.files.internal("MonsterSFX/eerieMusic.mp3"));
        escapeSound = Gdx.audio.newSound(Gdx.files.internal("MonsterSFX/monsterEscape.mp3"));
    }

    public void initVariables() {
        dx = 1;
        coorX = 9000;
        coorY = 5000;
        health = 100;
        ran = new Random();
    }
}
