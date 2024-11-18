package com.Rye.DarknessGame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class Scene {

    Sound ambience;
    SoundPlayer soundPlayer;
    String stageName;
    RoomManager roomManager;
    Player player;
    Texture image;
    Room room;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    Matrix4 projection;

    public Scene(String stageName, Sound ambience, RoomManager roomManager, SoundPlayer soundPlayerRef, Player player) {
        this.stageName = stageName;
        this.ambience = ambience;
        this.roomManager = roomManager;
        soundPlayer = soundPlayerRef;
        this.player = player;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    public void renderScene() {

        projection = player.getCamera().combined;
        batch.setProjectionMatrix(projection);
        shapeRenderer.setProjectionMatrix(projection);
        batch.setProjectionMatrix(projection);

        this.room = roomManager.getRooms().get(0);
        this.image = room.getImage();

        batch.begin();
//        for (float x = 0; x < room.getWidth(); x += image.getWidth()) {
//            for (float y = 0; y < room.getHeight(); y += image.getHeight()) {
//                batch.draw(image, x, y);
//            }
//        }

        batch.draw(image,0,0);
        batch.end();
        soundPlayer.playSound(stageName, ambience);
    }
}
