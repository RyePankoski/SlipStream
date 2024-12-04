package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Rectangle;


public class Tram {
    Player player;
    Texture tramTexture;
    int coorX = 9000, coorY = 4930;
    boolean moving;

    int[] tramStops;

    int whichStop;

    private static Tram instance;

    public Tram(Player player) {
        this.player = player;
        instance = this;

        initTexture();
        initVariables();
    }

    public void updateTram() {
        handleInputs();
        move();
    }

    public void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (!(whichStop + 1 > tramStops.length - 1)) {
                whichStop++;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (!(whichStop - 1 < 0)) {
                whichStop--;
            }
        }
    }

    public void move() {
        Rectangle interactionZone = new Rectangle(coorX - (float) tramTexture.getWidth() / 2 + 160, coorY + (float) tramTexture.getHeight() / 2 - 95, (float) tramTexture.getWidth(), (float) tramTexture.getHeight());
        int tramSpeed = 3;

        if (moving) {
            SoundEffects.playMusic("tramMoving");
        } else {
            SoundEffects.stopMusic("tramMoving");
        }

        if (coorX > tramStops[whichStop]) {
            moving = true;
            coorX -= tramSpeed;
            if (interactionZone.contains(player.getCoorX(), player.getCoorY())) {
                player.addToCoors(-tramSpeed, 0);
            }

        } else if (coorX < tramStops[whichStop]) {
            moving = true;
            coorX += tramSpeed;
            if (interactionZone.contains(player.getCoorX(), player.getCoorY())) {
                player.addToCoors(tramSpeed, 0);
            }
        } else {
            moving = false;
        }
    }

    public void initVariables() {
        whichStop = 2;
        tramStops = new int[4];
        tramStops[0] = 3210;
        tramStops[1] = 4800;
        tramStops[2] = 9000;
        tramStops[3] = 12900;
    }

    public void drawMyself(SpriteBatch spriteBatch) {
        spriteBatch.draw(tramTexture, coorX, coorY);
    }

    public void initTexture() {
        tramTexture = new Texture("TexSprites/tram.png");
    }

    public static Tram getInstance() {
        return instance;
    }
}




