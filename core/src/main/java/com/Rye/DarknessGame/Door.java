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

    boolean justInsantiated = true;

    Music lockedDoorSound;

    boolean locked;

    public Door(int posX, int posY,int width, int height, int sector,int instantiationNumber, boolean locked, Player player, Pixmap pixmap) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.sector = sector;
        this.instantiationNumber = instantiationNumber;
        this.player = player;
        this.pixmap = pixmap;
        this.locked = locked;

        doorOpeningSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3"));
        doorClosingSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3"));
        lockedDoorSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/lockedDoorSound.mp3"));

        shapeRenderer = new ShapeRenderer();
    }

    public void updateDoor() {
//        System.out.println("Door in " + sector + ", number:" + instantiationNumber + " checking in!");
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
            if(!doorClosingSound.isPlaying() && !justInsantiated){
                doorClosingSound.play();
            }
            hasStateChanged = false;
            pixmap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            pixmap.fillRectangle(posX, pixmap.getHeight() - posY - height, width, height);  // Close door by drawing opaque rectangle (using correct width and height)
        }
        justInsantiated = false;
    }

    public void isPlayerNear() {
        double playerDistance = MathFunctions.distanceFromMe(posX + (double)width/2, posY + (double)height/2, player.getCoorX(), player.getCoorY());

        if (playerDistance < 100 && locked){
            if(!lockedDoorSound.isPlaying()){
                lockedDoorSound.play();
            }
        }

        if (playerDistance < 100 && !locked) {
            open();
        } else {
            close();
        }
    }

    public int getSector() {
        return sector;
    }
}
