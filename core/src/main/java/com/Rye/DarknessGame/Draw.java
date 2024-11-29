package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class Draw {

    SpriteBatch batch;

    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;

    Player player;

    Monster monster;

    ArrayList<Bullet> bullets;

    Scene scene;


    public Draw(OrthographicCamera camera) {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }



    public void updateAll() {
        scene = Scene.getInstance();
        player = Player.getInstance();
        monster = Monster.getInstance();
        bullets = player.getBullets();

        startBatch();
        scene.renderScene(batch);
        player.drawMyself(batch);
        player.drawCursor(batch);
        monster.drawMyself(batch);

        for(Bullet bullet : bullets){
            bullet.drawMyself(batch);
            bullet.drawStrike(batch);
        }

        endBatch();
    }

    public void startBatch() {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    public void endBatch(){
        batch.end();
        shapeRenderer.end();
    }
}
