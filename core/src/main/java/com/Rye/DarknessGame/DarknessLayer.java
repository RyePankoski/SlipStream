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
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class DarknessLayer implements Screen {
    // Constants
    private static final int FLASH_TOTAL_FRAMES = 8;
    private static final float AMBIENT_ALPHA = 0.05f;
    private static final float DARKNESS_ALPHA = 1f;
    private static final float MIN_BATTERY_THRESHOLD = 20f;
    private static final float BRIGHTNESS_DECAY_RATE = 0.5f;
    private static final int RAYCAST_DISTANCE = 250;
    private static final int RAYCAST_RAYS = 15;

    // Rendering components
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;
    private final OrthographicCamera camera;

    // Light state
    private final Player player;
    private final LightState lightState;
    private final FlashState flashState;
    private final BulletStrikeState bulletStrikeState;
    ArrayList<StaticLightSource> staticLights;
    Pixmap lightMap;


    public DarknessLayer(Player player, ArrayList<StaticLightSource> lights,Pixmap lightMap) {
        this.lightMap = lightMap;
        this.player = player;
        this.camera = player.getCamera();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.lightState = new LightState();
        this.flashState = new FlashState();
        this.bulletStrikeState = new BulletStrikeState();
        this.staticLights = lights;

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
        renderStaticLights();
        renderLightEffects();
        endLightBufferRendering();
    }


    private void beginLightBufferRendering() {

        lightBuffer.begin();
        if (player.playerHurt) {
            player.playerHurt = false;
            Gdx.gl.glClearColor(.2f, 0, 0, DARKNESS_ALPHA);
        } else {
            Gdx.gl.glClearColor(0, 0, 0, DARKNESS_ALPHA);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    public void renderStaticLights() {

        shapeRenderer.setColor(1, 1, 1, (float) lightState.getBrightness() / 100 + 0.1f);
        for (StaticLightSource light : staticLights) {
            shapeRenderer.setColor(1, 1, 1, light.brightness);
            renderLightTriangles(light.getVertices(), light.getX(), light.getY());
            if (light.getAngle() ==180){
                float[] lightVertices = light.getVertices();
                float[] finalVertices = new float[4];
                finalVertices[0] = lightVertices[0];
                finalVertices[1] = lightVertices[1];
                finalVertices[2] = lightVertices[lightVertices.length -2];
                finalVertices[3] = lightVertices[lightVertices.length - 1];
                renderLightTriangles(finalVertices, light.x, light.y);
            }
        }
    }

    private void renderLightEffects() {
        if (bulletStrikeState.isActive()) {
            renderBulletStrike();
        }
        if (flashState.isActive()) {
            renderFlashEffect();
        }
        renderPlayerLight();
    }

    private void renderLightTriangles(float[] vertices, double centerX, double centerY) {
        for (int i = 0; i < vertices.length - 2; i += 2) {
            shapeRenderer.triangle(
                (float) centerX, (float) centerY,
                vertices[i], vertices[i + 1],
                vertices[i + 2], vertices[i + 3]
            );
        }
    }

    private void renderBulletStrike() {
        shapeRenderer.setColor(1f, 1f, 0f, (float)(bulletStrikeState.getStrikeSize() / 300f));
        shapeRenderer.circle(
            bulletStrikeState.getPosition().x,
            bulletStrikeState.getPosition().y,
            (float) bulletStrikeState.getStrikeSize()
        );
        bulletStrikeState.reset();
    }

    private void renderFlashEffect() {

        float alpha = flashState.getCurrentFrame() / (float) FLASH_TOTAL_FRAMES;
        shapeRenderer.setColor(1f, 1f, 0f, alpha);

        float[] lightVertices = MathFunctions.rayCast(
            200, 180,
            (int) player.getFacingAngle(),
            (int) player.getCoorX(),
            (int) player.getCoorY(), 3,
            lightMap
        );

        renderLightTriangles(lightVertices, player.getCoorX(), player.getCoorY());
        flashState.update();

        float[] finalVertices = new float[4];
        finalVertices[0] = lightVertices[0];
        finalVertices[1] = lightVertices[1];
        finalVertices[2] = lightVertices[lightVertices.length -2];
        finalVertices[3] = lightVertices[lightVertices.length - 1];

        renderLightTriangles(finalVertices, player.getCoorX(), player.getCoorY());
    }

    private void renderPlayerLight() {
        if (player.getFlashLight()) {
            renderFlashlight();
        }
        renderAmbientLight();
    }

    private void renderFlashlight() {
        updateLightState();

        // Render directional light
        float[] lightVertices = MathFunctions.rayCast(
            RAYCAST_DISTANCE, RAYCAST_RAYS,
            (int) player.getFacingAngle(),
            (int) player.getCoorX(),
            (int) player.getCoorY(), 2,
            lightMap
        );

        shapeRenderer.setColor(1, 1, 1, (float) lightState.getBrightness() / 100 + 0.1f);
        renderLightTriangles(lightVertices, player.getCoorX(), player.getCoorY());
    }

    private void renderAmbientLight() {


        float[] lightVertices = MathFunctions.rayCast(
            RAYCAST_DISTANCE, 180,
            (int) player.getFacingAngle(),
            (int) player.getCoorX(),
            (int) player.getCoorY(), 4,
            player.getCollisionMap()
        );
        shapeRenderer.setColor(1, 1, 1, AMBIENT_ALPHA);
        renderLightTriangles(lightVertices, player.getCoorX(), player.getCoorY());

        float[] finalVertices = new float[4];
        finalVertices[0] = lightVertices[0];
        finalVertices[1] = lightVertices[1];
        finalVertices[2] = lightVertices[lightVertices.length -2];
        finalVertices[3] = lightVertices[lightVertices.length - 1];
        renderLightTriangles(finalVertices, player.getCoorX(), player.getCoorY());

        if (player.flashLightIsOn) {
            shapeRenderer.setColor(1f, 1f, 1f, .5f);
            shapeRenderer.circle(player.getCoorX(), player.getCoorY(), 15);
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

    private void updateLightState() {
        float battery = (float) player.getBattery();

        if (battery > MIN_BATTERY_THRESHOLD) {
            lightState.setDefaults();
        } else {
            lightState.updateLowBattery(battery);
        }
    }

    public void setBulletStrike(boolean active, double x, double y, double size) {
        bulletStrikeState.set(active, x, y, size);
    }

    public void setJustFired(boolean fired) {
        if (fired && !flashState.isActive()) {
            flashState.start();
        }
    }

    private static class LightState {
        private double brightness = 100;
        private double ambientLight = 20;

        void setDefaults() {
            brightness = 100;
            ambientLight = 20;
        }

        void updateLowBattery(float battery) {
            if (brightness > battery) {
                brightness -= DarknessLayer.BRIGHTNESS_DECAY_RATE;
            }
            if (brightness < 20) {
                ambientLight = brightness;
            }
        }

        double getBrightness() {
            return brightness;
        }

        double getAmbientLight() {
            return ambientLight;
        }
    }

    private static class FlashState {
        private boolean active;
        private int framesRemaining;

        void start() {
            active = true;
            framesRemaining = DarknessLayer.FLASH_TOTAL_FRAMES;
        }

        void update() {
            if (framesRemaining > 0) {
                framesRemaining--;
                if (framesRemaining == 0) {
                    active = false;
                }
            }
        }

        boolean isActive() {
            return active;
        }

        int getCurrentFrame() {
            return framesRemaining;
        }
    }

    private static class BulletStrikeState {
        private boolean active;
        private final Vector2 position = new Vector2();
        private double strikeSize;

        void set(boolean active, double x, double y, double size) {
            this.active = active;
            this.position.set((float) x, (float) y);
            this.strikeSize = size;
        }

        void reset() {
            active = false;
        }

        boolean isActive() {
            return active;
        }

        Vector2 getPosition() {
            return position;
        }

        double getStrikeSize() {
            return strikeSize;
        }
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

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (lightBuffer != null) lightBuffer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (lightBuffer != null) {
            lightBuffer.dispose();
        }
        initializeLightBuffer();
    }

}
