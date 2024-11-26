package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;

public class Door {

    int posX;
    int posY;
    Player player;
    boolean hasStateChanged = true;
    Pixmap collisionMap;
    int sector;
    int instantiationNumber;
    int width;
    int height;
    boolean justInstantiated = true;
    boolean locked;
    int openIncrement;
    boolean orientedHorizontal;
    Pixmap lightMap;

    public Door(int posX, int posY, int width, int height, int sector, int instantiationNumber,
                boolean locked, Player player, Pixmap collisionMap, Pixmap lightMap) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.sector = sector;
        this.instantiationNumber = instantiationNumber;
        this.locked = locked;
        this.player = player;
        this.collisionMap = collisionMap;
        this.lightMap = lightMap;

        orientedHorizontal = width > height;
        openIncrement = Math.max(width, height);

    }

    public void updateDoor() {
//        System.out.println("Door in sector: " + sector + " number: " + instantiationNumber + " checking in");
        isPlayerNear();
    }

    public void open() {

        if (!hasStateChanged) {
            SoundEffects.playMusic("openDoor");

            if (openIncrement > 0 && orientedHorizontal) {
                collisionMap.setColor(0f, 0f, 0f, 0f);  // Set to transparent
                collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width - openIncrement, height);
                lightMap.setColor(0, 0, 0, 0);  // Set to opaque (white color)
                lightMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width - openIncrement, height);
                openIncrement -= 4;
            }
            if (openIncrement > 0 && !orientedHorizontal) {
                collisionMap.setColor(0f, 0f, 0f, 0f);  // Set to transparent
                collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height - openIncrement);
                lightMap.setColor(0, 0, 0, 0);  // Set to opaque (white color)
                lightMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height - openIncrement);
                openIncrement -= 4;
            }

            if (openIncrement == 0) {
                hasStateChanged = true;
                openIncrement = Math.max(width, height);
            }
        }
    }

    public void close() {
        if (hasStateChanged) {
            SoundEffects.playMusic("doorClose");
            hasStateChanged = false;
            collisionMap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);
            lightMap.setColor(0, 0, 0, 1);  // Set to opaque (white color)
            lightMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);// Close door by drawing opaque rectangle (using correct width and height)
        }
        justInstantiated = false;
    }

    public void isPlayerNear() {

        double playerDistance = MathFunctions.distanceFromMe(posX + (double) width / 2, posY + (double) height / 2, player.getCoorX(), player.getCoorY());

        if(playerDistance < 75){
            System.out.println(String.valueOf(sector) + instantiationNumber);
        }

        if (player.getKeys().get(String.valueOf(this.sector) + String.valueOf(this.instantiationNumber)) != null && playerDistance < 100 && locked) {
            SoundEffects.playMusic("unlockDoor");
            locked = false;
        }

        if (playerDistance < 75 && locked) {
            double[] points = MathFunctions.pointInFront(player.getCoorX(), player.getCoorY(), player.getFaceX(), player.getFaceY(), 25);
            PopUpManager.displayPopUp((float) points[0], (float) points[1], "LOCKED", player);
            SoundEffects.playMusic("lockedDoorSound");
        }

        if (playerDistance < 75 && !locked) {
            double[] points = MathFunctions.pointInFront(player.getCoorX(), player.getCoorY(), player.getFaceX(), player.getFaceY(), 25);
            PopUpManager.displayPopUp((float) points[0], (float) points[1], "E", player);
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                open();
            }
        } else {
            close();
        }
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
