package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Music;

public class Door {

    int posX;
    int posY;
    Player player;
    boolean hasStateChanged = true;

    Pixmap pixmap;
    ShapeRenderer shapeRenderer;

    Music doorOpeningSound;

    Music doorClosingSound;

    public Door(int posX, int posY, Player player, Pixmap pixmap) {
        this.posX = posX;
        this.posY = posY;
        this.player = player;
        this.pixmap = pixmap;


        doorOpeningSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3"));
        doorClosingSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3"));

        shapeRenderer = new ShapeRenderer();
        close();
    }

    public void updateDoor() {
        isPlayerNear();  // Check if the player is near and update door state
    }

    public void open() {
        if (!hasStateChanged) {

            if(!doorOpeningSound.isPlaying()){
                doorOpeningSound.play();
            }
            hasStateChanged = true;
            pixmap.setColor(0f, 0f, 0f, 0f);  // Set to transparent
            pixmap.fillRectangle(posX, pixmap.getHeight() - posY, 96, 32);  // Open door by making it transparent (using correct width and height)
        }
    }

    public void close() {
        if (hasStateChanged) {
            if(!doorClosingSound.isPlaying()){
                doorClosingSound.play();
            }
            hasStateChanged = false;
            pixmap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            pixmap.fillRectangle(posX, pixmap.getHeight() -posY, 96, 32);  // Close door by drawing opaque rectangle (using correct width and height)
        }
    }

    public void isPlayerNear() {
        // Check if the player is close to the door
        double playerDistance = MathFunctions.distanceFromMe(posX, posY, player.getCoorX(), player.getCoorY());
        if (playerDistance < 100) {
            open();
        } else {
            close();
        }
    }
}
