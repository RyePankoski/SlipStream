package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;

public class Door {
    int posX, posY, sector, instantiationNumber, width, height, openIncrement;
    boolean hasStateChanged = true, justInstantiated = true, locked, orientedHorizontal, opening;
    Player player;
    Pixmap collisionMap, lightMap;


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
        if(opening){
            open();
        }
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
                opening = false;
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

//        if(playerDistance < 75){
//            System.out.println(String.valueOf(sector) + instantiationNumber);
//        }

        if (player.getKeys().get(String.valueOf(this.sector) + String.valueOf(this.instantiationNumber)) != null && playerDistance < 75 && locked) {
            SoundEffects.playMusic("unlockDoor");
            locked = false;
        }

        if (playerDistance < 75 && locked) {
            double[] points = MathFunctions.pointInFront(player.getCoorX(), player.getCoorY(), player.getFaceX(), player.getFaceY(), 25);
            PopUpManager.displayPopUp((float) points[0], (float) points[1], "LOCKED", player);
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                SoundEffects.playMusic("lockedDoorSound");
            }
        }

        if (playerDistance < 75 && !locked) {
            double[] points = MathFunctions.pointInFront(player.getCoorX(), player.getCoorY(), player.getFaceX(), player.getFaceY(), 25);
            PopUpManager.displayPopUp((float) points[0], (float) points[1], "E", player);
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                opening = true;
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
