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
        batch.setProjectionMatrix(projection);

        batch.draw(image,
            (int)(player.getCoorX() - player.cameraZoom/2),  // Screen X (centered on player)
            (int)(player.getCoorY() - player.cameraZoom/2),  // Screen Y (centered on player)
            (int)player.cameraZoom,                          // Width to draw on screen
            (int)player.cameraZoom,                          // Height to draw on screen
            (int)(player.getCoorX() - player.cameraZoom/2),  // Start X in texture
            (int)(image.getHeight() - (player.getCoorY() + player.cameraZoom/2)),  // Start Y in texture, flipped
            (int)player.cameraZoom,                          // Width of texture region
            (int)player.cameraZoom,                          // Height of texture region
            false, false);                                   // No flipping needed anymore
        batch.end();

        soundPlayer.playSound(stageName, ambience);
    }
}
