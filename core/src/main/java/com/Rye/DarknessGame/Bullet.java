package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.*;

public class Bullet {
    double startX, startY, facingX, facingY, posX, posY, dx, dy, bulletSpeed;
    boolean alive;
    Player player;
    Monster monster;
    Weapon weapon;
    Color white;
    Texture bulletStrikeTexture;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    Pixmap collisionMap;

    public Bullet(Pixmap collisionMap, Player player, double bulletSpeed, Monster monster, Weapon weapon) {
        this.collisionMap = collisionMap;
        this.player = player;
        this.bulletSpeed = bulletSpeed;
        this.monster = monster;
        this.weapon = weapon;

        initVariables();
        initDrawParams();
        initSprites();
        findXYIncrements();
    }

    public void findXYIncrements() {
        dx = facingX - startX;
        dy = facingY - startY;
        double angle = Math.atan2(dx, dy);
        dx = bulletSpeed * Math.sin(angle);
        dy = bulletSpeed * Math.cos(angle);
    }

    public void castRay() {
        posX += dx;
        posY += dy;

        shapeRenderer.setProjectionMatrix(player.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(255 / 255f, 255 / 255f, 0 / 255f, 1f);
        shapeRenderer.circle((float) posX, (float) posY, 2);
        shapeRenderer.end();

        checkForWall();
        if (monster.alive) {
            checkForMonster();
        }
        checkOutOfBounds();
    }

    public void die() {
        alive = false;
    }

    public void checkForWall() {

        if (getPixelColor((int) posX, (int) posY).equals(white)) {
            double distance = MathFunctions.distanceFromMe(startX,startY,facingX,facingY);
            double percent = ((500 - distance) / 500);
            percent = Math.max(0, Math.min(1, percent));

            SoundEffects.playSoundWithParameters("bulletStrike",(float)percent, 1f);

            spriteBatch.setProjectionMatrix(player.getCamera().combined);
            spriteBatch.begin();
            spriteBatch.draw(bulletStrikeTexture, ((float) (posX - bulletStrikeTexture.getWidth() / 2f)), ((float) (posY - bulletStrikeTexture.getHeight() / 2f)));
            spriteBatch.end();

            player.main.darknessLayer.setBulletStrike(true, posX, posY, weapon.getWeaponType().hitSize);

            die();
        }
    }

    public void checkForMonster() {
        double distance = MathFunctions.distanceFromMe(posX,posY,monster.getCoorX(),monster.getCoorY());
        if (distance < 20) {
            monster.hitByBullet(weapon.getWeaponType());
            SoundEffects.playSound("monsterStrikeSound");
            die();
        }
    }

    public void checkOutOfBounds() {
        if (posX < 0 || posY < 0) {
            die();
        }
        if (posX > collisionMap.getWidth() || posY > collisionMap.getHeight()) {
            die();
        }
    }

    private Color getPixelColor(int x, int y) {
        int pixel = collisionMap.getPixel(x, collisionMap.getHeight() - y);

        float r = ((pixel >> 24) & 0xFF) / 255f; // Red component
        float g = ((pixel >> 16) & 0xFF) / 255f; // Green component
        float b = ((pixel >> 8) & 0xFF) / 255f;  // Blue component
        float a = (pixel & 0xFF) / 255f;         // Alpha component

        return new Color(r, g, b, a);
    }

    public boolean isAlive() {
        return alive;
    }

    public void initVariables() {
        alive = true;
        startX = player.getCoorX();
        startY = player.getCoorY();
        facingX = player.getFaceX();
        facingY = player.getFaceY();
        posX = startX;
        posY = startY;
    }

    public void initDrawParams() {
        white = new Color(255, 255, 255);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
    }
    public void initSprites() {
        bulletStrikeTexture = new Texture(Gdx.files.internal("TexSprites/bulletImpactSprite.png"));
    }

}
