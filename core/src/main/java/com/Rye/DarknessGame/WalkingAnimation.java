package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class WalkingAnimation {
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private Texture spriteSheet;

    private Player player;
    private TextureRegion currentFrame;

    // Frame duration constants
    private final float walkFrameDuration = 0.15f;  // Normal walking speed
    private final float sprintFrameDuration = 0.08333f; // Faster sprinting speed

    public WalkingAnimation(Player player) {
        this.player = player;
    }

    public void initialize() {
        // Load the sprite sheet
        spriteSheet = new Texture(Gdx.files.internal("TexSprites/soldierWalking.png"));

        // Split the sprite sheet into individual frames
        TextureRegion[][] tempFrames = TextureRegion.split(spriteSheet, 64, 64);

        int rows = tempFrames.length;
        int cols = tempFrames[0].length;
        TextureRegion[] animationFrames = new TextureRegion[rows * cols];
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                animationFrames[index++] = tempFrames[i][j];
            }
        }

        // Initialize the walk animation with the default frame duration
        walkAnimation = new Animation<>(walkFrameDuration, animationFrames);

        stateTime = 0f; // Initialize state time
    }

    public void render(SpriteBatch batch, float x, float y) {
        // Get delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Dynamically adjust the frame duration based on the player's movement state
        if (player.getSprint()) {
            // Speed up the animation when sprinting
            walkAnimation.setFrameDuration(sprintFrameDuration);
        } else {
            // Use normal walking frame duration when not sprinting
            walkAnimation.setFrameDuration(walkFrameDuration);
        }

        // Update state time with the delta time
        stateTime += deltaTime;

        // Get the current frame of the animation
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        // Draw the current frame
        batch.draw(currentFrame,
            x, y,                          // Position of the sprite
            currentFrame.getRegionWidth() / 2f, currentFrame.getRegionHeight() / 2f, // Origin point (center of sprite)
            currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), // Width and height
            1f, 1f,                         // Scale (optional)
            player.getFacingAngle() + 90);  // Rotation based on the player's facing angle
    }

    public void dispose() {
        // Dispose of the texture to free resources
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}
