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

    Pixmap collisionMap;
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

    public Door(int posX, int posY,int width, int height, int sector,int instantiationNumber, boolean locked, Player player, Pixmap collisionMap) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.sector = sector;
        this.instantiationNumber = instantiationNumber;
        this.player = player;
        this.collisionMap = collisionMap;
        this.locked = locked;

        doorOpeningSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3"));
        doorClosingSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3"));
        lockedDoorSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/lockedDoorSound.mp3"));

        shapeRenderer = new ShapeRenderer();
    }

    public void updateDoor() {
        System.out.println("Door in sector:" + sector + ", number:" + instantiationNumber + " checking in!");
        isPlayerNear();  // Check if the player is near and update door state
    }

    public void open() {
        if (!hasStateChanged) {

            if(!doorOpeningSound.isPlaying()){
                doorOpeningSound.play();
            }
            hasStateChanged = true;
            collisionMap.setColor(0f, 0f, 0f, 0f);  // Set to transparent
            collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);  // Open door by making it transparent (using correct width and height)
        }
    }

    public void close() {
        if (hasStateChanged) {
            if(!doorClosingSound.isPlaying() && !justInsantiated){
                doorClosingSound.play();
            }
            hasStateChanged = false;
            collisionMap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);  // Close door by drawing opaque rectangle (using correct width and height)
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
