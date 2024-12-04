package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class Draw {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    Player player;
    Monster monster;
    ArrayList<Bullet> bullets;
    Scene scene;

    Tram tram;

    Inventory inventory;
    private final ShaderProgram shader;
    private float timeElapsed;
    private float intensity;
    private float targetIntensity;
    private static final float MAX_HEALTH = 100f; // Adjust this to match your game's max health
    private static final float INTENSITY_SMOOTHING = 0.1f; // How quickly intensity changes



    public Draw(OrthographicCamera camera) {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/vertex.glsl"),
            Gdx.files.internal("shaders/fragment.glsl")
        );

        if (!shader.isCompiled()) {
            System.err.println("Shader compilation failed:");
            System.err.println(shader.getLog());
        }

        timeElapsed = 0f;
        intensity = 0f;
        targetIntensity = 0f;

        batch.setShader(shader);
    }

    public void updateAll() {
        scene = Scene.getInstance();
        player = Player.getInstance();
        monster = Monster.getInstance();
        bullets = player.getBullets();
        tram = Tram.getInstance();
        inventory = Inventory.getInstance();

        float healthPercent = (float) player.getHealth() / MAX_HEALTH;
        targetIntensity = MathUtils.clamp(0.0025f * (1.0f - healthPercent), 0f, 0.0025f);

        intensity = MathUtils.lerp(intensity, targetIntensity, INTENSITY_SMOOTHING);
        timeElapsed += Gdx.graphics.getDeltaTime();
        shader.bind();
        shader.setUniformf("u_time", timeElapsed);
        shader.setUniformf("u_intensity", intensity);

        startBatch();
        scene.renderScene(batch);
        tram.drawMyself(batch);
        player.drawMyself(batch);
        player.drawCursor(batch);
        monster.drawMyself(batch);

        for (Bullet bullet : bullets) {
            bullet.drawMyself(batch);
            bullet.drawStrike(batch);
        }

        inventory.render(batch);

        endBatch();
    }

    public void startBatch() {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    public void endBatch() {
        batch.end();
        shapeRenderer.end();
    }

    // Add method to control shader intensity
    public void setShaderIntensity(float intensity) {
        this.intensity = intensity;
    }

    // Clean up resources
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (shader != null) shader.dispose();
    }
}
