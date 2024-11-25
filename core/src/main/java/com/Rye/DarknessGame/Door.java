package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Stage;
import jdk.javadoc.internal.doclets.formats.html.Table;

import java.awt.*;

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

    int openIncrement;

    boolean orientedHorizontal;
    Pixmap lightMap;

    BitmapFont bitmapFont;

    SpriteBatch batch;

    FreeTypeFontGenerator generator;

    FreeTypeFontGenerator.FreeTypeFontParameter parameter;


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


        initSounds();
        initDrawParams();
    }

    public void updateDoor() {
        isPlayerNear();  // Check if the player is near and update door state
    }

    public void open() {

        if (!hasStateChanged) {
            if (!doorOpeningSound.isPlaying()) {
                doorOpeningSound.play();
            }
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
            if (!doorClosingSound.isPlaying() && !justInsantiated) {
                doorClosingSound.play();
            }
            hasStateChanged = false;
            collisionMap.setColor(1, 1, 1, 1);  // Set to opaque (white color)
            collisionMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);
            lightMap.setColor(0, 0, 0, 1);  // Set to opaque (white color)
            lightMap.fillRectangle(posX, collisionMap.getHeight() - posY - height, width, height);// Close door by drawing opaque rectangle (using correct width and height)
        }
        justInsantiated = false;
    }

    public void isPlayerNear() {
        double playerDistance = MathFunctions.distanceFromMe(posX + (double) width / 2, posY + (double) height / 2, player.getCoorX(), player.getCoorY());

        if (playerDistance < 70){
            popUp();
//            System.out.println(String.valueOf(this.sector) + String.valueOf(this.instantiationNumber));
        }

        if (playerDistance < 100 && locked) {
            if (player.getKeys().get(String.valueOf(this.sector) + String.valueOf(this.instantiationNumber)) != null) {
                SoundEffects.playSound("unlockDoor");
                locked = false;
            }

            if (!lockedDoorSound.isPlaying()) {
                lockedDoorSound.play();
            }
        }

        if (playerDistance < 100 && !locked) {
            if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                open();
            }
        } else {
            close();
        }
    }

    public void initDrawParams() {
        bitmapFont = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/computaFont.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        bitmapFont = generator.generateFont(parameter);
        generator.dispose();
    }

    public void initSounds(){
        doorOpeningSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3"));
        doorClosingSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3"));
        lockedDoorSound = Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/lockedDoorSound.mp3"));
    }

    public void popUp() {
        double[] points = MathFunctions.pointInFront(player.getCoorX(), player.getCoorY(), player.getFaceX(), player.getFaceY(), 25);
        batch.setProjectionMatrix(player.getCamera().combined);
        batch.begin();
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(batch,"E", (float)points[0], (float)points[1]);
        batch.end();
    }
}
