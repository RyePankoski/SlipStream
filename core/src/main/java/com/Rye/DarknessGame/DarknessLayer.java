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

public class DarknessLayer implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;
    private OrthographicCamera camera;
    double[] lightCone;
    Player player;
    boolean justFired;
    double brightness;
    private boolean justFlashed;
    private int flashFramesRemaining;
    double ambientLight;

    boolean bulletStrike = false;
    double bullX;
    double bullY;

    double strikeSize;
    float[] lightVertices;

    public DarknessLayer(Player player) {
        this.player = player;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = player.getCamera();

        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(), false);
        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
        lightBufferRegion.flip(false, true);
    }

    public void bulletStrike(boolean tf, double bullX, double bullY, double strikeSize) {
        bulletStrike = tf;
        this.bullX = bullX;
        this.bullY = bullY;
        this.strikeSize = strikeSize;
    }

    @Override
    public void render(float delta) {
        lightBuffer.begin();

        // Fill the buffer with semi-transparent black
        Gdx.gl.glClearColor(0, 0, 0, 0.97f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Set up blending for the light circle
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        lightCone = player.lightCone();

        if (bulletStrike) {
            bulletStrike = false;
            shapeRenderer.setColor(255 / 255f, 255 / 255f, 0 / 255f, (float) strikeSize / 300);
            shapeRenderer.circle((float) bullX, (float) bullY, (float) strikeSize);
        }

        float battery = (float) player.getBattery();

        lightVertices = player.rayCast(1000, 160, (int) (player.getFacingAngle()));


        int totalFlashFrames = 13;
        if (justFired && !justFlashed) {
            justFlashed = true;
            justFired = false;
            flashFramesRemaining = totalFlashFrames;

        }

        if (flashFramesRemaining > 0) {
            float alpha = flashFramesRemaining / (float) totalFlashFrames; // Fading effect
            shapeRenderer.setColor(1f, 1f, 0f, alpha); // Yellow flash with fading alpha


            for (int i = 0; i < lightVertices.length - 2; i += 2) {
                shapeRenderer.triangle(
                    (float)player.pointInFrontVector[0], (float)player.pointInFrontVector[1],     // First point (center)
                    lightVertices[i], lightVertices[i + 1],   // Second point
                    lightVertices[i + 2], lightVertices[i + 3] // Third point
                );
            }

            flashFramesRemaining--;
            if (flashFramesRemaining == 0) {
                justFlashed = false;
            }
        }

        if (player.getFlashLight()) {
            if (battery > 20) {
                ambientLight = 20;
                brightness = 100;
            } else if (battery < 20 && player.getFlashLight()) {
                if (brightness > battery) {
                    brightness -= .5;
                }
            }
            if (brightness < 20) {
                ambientLight = brightness;
            }


            shapeRenderer.setColor(1, 1, 1, (float) ambientLight / 100 + .1f);


            shapeRenderer.circle(player.getCoorX(), player.getCoorY(), 200);

            lightVertices = player.rayCast(1800, 20, (int) player.getFacingAngle());
            shapeRenderer.setColor(1, 1, 1, (float) brightness / 100 + .1f);

            for (int i = 0; i < lightVertices.length - 2; i += 2) {
                shapeRenderer.triangle(
                    player.getCoorX(), player.getCoorY(),     // First point (center)
                    lightVertices[i], lightVertices[i + 1],   // Second point
                    lightVertices[i + 2], lightVertices[i + 3] // Third point
                );
            }

        } else {
            shapeRenderer.setColor(1, 1, 1, .07f);
            shapeRenderer.circle(player.getCoorX(), player.getCoorY(), 200);
        }

        shapeRenderer.end();
        lightBuffer.end();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        float x = camera.position.x - camera.viewportWidth / 2;
        float y = camera.position.y - camera.viewportHeight / 2;
        batch.draw(lightBufferRegion, x, y, camera.viewportWidth, camera.viewportHeight);
        batch.end();

        if (justFired && !justFlashed) {
            justFlashed = true;
            justFired = false;
            flashFramesRemaining = totalFlashFrames;
        }


        if (flashFramesRemaining > 0) {

            flashFramesRemaining--;

            if (flashFramesRemaining == 0) {
                justFlashed = false;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (lightBuffer != null) {
            lightBuffer.dispose();
        }
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
        lightBufferRegion.flip(false, true);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (lightBuffer != null) lightBuffer.dispose();
    }

    @Override
    public void show() {
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

    public void setJustFired(boolean justFired) {
        this.justFired = justFired;
    }
}
