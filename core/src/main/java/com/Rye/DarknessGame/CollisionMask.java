package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;

public class CollisionMask {

    SpriteBatch spriteBatch;
    Texture collisionMap;
    OrthographicCamera camera;
    Pixmap pixmap;


    public CollisionMask() {
        spriteBatch = new SpriteBatch();
        pixmap = new Pixmap(Gdx.files.internal("CollisionMap/collisionMap.png"));
        pixmap.setBlending(Pixmap.Blending.None);
        collisionMap = new Texture(pixmap);
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }
}
