package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Camera;

public class PopUpManager {

    private static BitmapFont bitmapFont;
    private static SpriteBatch batch;
    private static boolean initialized = false;

    // Static initialization method
    public static void init() {
        if (initialized) return;

        // Initialize the resources
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/computaFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        bitmapFont = generator.generateFont(parameter);

        generator.dispose();
        initialized = true;
    }

    // Static method for displaying pop-ups
    public static void displayPopUp(float x, float y, String message,Player player) {
        if (!initialized) {
            throw new IllegalStateException("PopUpManager not initialized. Call init() first.");
        }

        batch.setProjectionMatrix(player.getCamera().combined);
        batch.begin();
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(batch, message, x, y);
        batch.end();
    }

    // Static method to dispose resources
    public static void dispose() {
        if (batch != null) batch.dispose();
        if (bitmapFont != null) bitmapFont.dispose();
        initialized = false;
    }
}
