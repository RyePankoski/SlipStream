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

    int sector;

    int instantiationNumber;
    int width;
    int height;

    public Door(int posX, int posY,int width, int height, int sector,int instantiationNumber, Player player, Pixmap pixmap) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.sector = sector;
        this.instantiationNumber = instantiationNumber;
        this.player = player;
        this.pixmap = pixmap;


        doorOpeningSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3"));
        doorClosingSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3"));

        shapeRenderer = new ShapeRenderer();
    }

    public void updateDoor() {
//        System.out.println("Door " + instantiationNumber + "checking in!");
        isPlayerNear();  // Check if the player is near and update door state
    }

    public void open() {
        if (!hasStateChanged) {

            if(!doorOpeningSound.isPlaying()){
                doorOpeningSound.play();
            }
            hasStateChanged = true;
            pixmap.setColor(0f, 0f, 0f, 0f);  // Set to transparent
            pixmap.fillRectangle(posX, pixmap.getHeight() - posY - height, width, height);  // Open door by making it transparent (using correct width and height)
        }
    }

    public void close() {
        if (hasStateChanged) {
            if(!doorClosingSound.isPlaying()){
                doorClosingSound.play();
            }
            hasStateChanged = false;
            pixmap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            pixmap.fillRectangle(posX, pixmap.getHeight() - posY - height, width, height);  // Close door by drawing opaque rectangle (using correct width and height)
        }
    }

    public void isPlayerNear() {
        // Check if the player is close to the door
        double playerDistance = MathFunctions.distanceFromMe(posX + (double)width/2, posY + (double)height/2, player.getCoorX(), player.getCoorY());
        if (playerDistance < 100) {
            open();
        } else {
            close();
        }
    }

    public int getSector() {
        return sector;
    }
}
