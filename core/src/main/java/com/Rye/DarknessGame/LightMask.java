package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LightMask {

    SpriteBatch spriteBatch;
    Texture collisionMap;

    private final Pixmap pixmap;

    public LightMask() {
        spriteBatch = new SpriteBatch();
        pixmap = new Pixmap((Gdx.files.internal("CollisionMap/lightMap.png")));
        pixmap.setBlending(Pixmap.Blending.None);
        collisionMap = new Texture(pixmap);
    }

    public Pixmap getPixmap() {
        return pixmap;
    }
}
