package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class LOS implements Screen {

    private final ShapeRenderer shapeRenderer;
    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;
    private final OrthographicCamera camera;
    SpriteBatch batch;

    private final Player player;

    public LOS(Player player) {
        this.player = player;
        this.camera = player.getCamera();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        initializeLightBuffer();
    }

    private void initializeLightBuffer() {
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            false);
        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
        lightBufferRegion.flip(false, true);
    }

    @Override
    public void render(float delta) {
        beginLightBufferRendering();
        renderLOS();
        endLightBufferRendering();
    }

    private void beginLightBufferRendering() {
        lightBuffer.begin();
        Gdx.gl.glClearColor(0f, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    public void renderLOS() {
        float[] lightVertices = MathFunctions.rayCast(
            400, 181,
            0,
            (int) player.getCoorX(),
            (int) player.getCoorY(),2,
            player.getCollisionMap()
        );
        shapeRenderer.setColor(1, 1, 1, 1);
        for (int i = 0; i < lightVertices.length - 2; i += 2) {
            shapeRenderer.triangle(
                player.getCoorX(), player.getCoorY(),
                lightVertices[i], lightVertices[i + 1],
                lightVertices[i + 2], lightVertices[i + 3]
            );
        }
    }

    private void endLightBufferRendering() {
        shapeRenderer.end();
        lightBuffer.end();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float x = camera.position.x - camera.viewportWidth / 2;
        float y = camera.position.y - camera.viewportHeight / 2;
        batch.draw(lightBufferRegion, x, y, camera.viewportWidth, camera.viewportHeight);
        batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
