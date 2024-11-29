package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.*;

public class Bullet {
    double startX, startY, facingX, facingY, posX, posY, dx, dy, bulletSpeed,angle;
    boolean alive;
    Player player;
    Monster monster;
    Weapon weapon;
    Color white;
    Texture bulletStrikeTexture,bulletTexture;
    Pixmap collisionMap;
    private boolean struck;
    ShapeRenderer shapeRenderer;

    Sprite bulletSprite;

    public Bullet(Pixmap collisionMap, Player player, double bulletSpeed, Monster monster, Weapon weapon) {
        this.collisionMap = collisionMap;
        this.player = player;
        this.bulletSpeed = bulletSpeed;
        this.monster = monster;
        this.weapon = weapon;
        struck = false;
        initVariables();
        initDrawParams();
        initSprites();
        findLifeTimeVariables();
    }

    public void findLifeTimeVariables() {
        dx = facingX - startX;
        dy = facingY - startY;
        angle = Math.atan2(dx, dy);
        dx = bulletSpeed * Math.sin(angle);
        dy = bulletSpeed * Math.cos(angle);
    }

    public void castRay() {
        posX += dx;
        posY += dy;

        checkForWall();
        if (monster.alive) {
            checkForMonster();
        }
        checkOutOfBounds();
    }

    public void drawMyself(SpriteBatch spriteBatch) {
        bulletSprite.setPosition((float)(posX - bulletTexture.getWidth()/2),(float)(posY - bulletTexture.getHeight()/2));
        bulletSprite.setRotation((float) angle);
        bulletSprite.draw(spriteBatch);
    }

    public void drawStrike(SpriteBatch spriteBatch) {
        if (struck) {
            spriteBatch.draw(bulletStrikeTexture, ((float) (posX - bulletStrikeTexture.getWidth() / 2f)), ((float) (posY - bulletStrikeTexture.getHeight() / 2f)));
            player.main.darknessLayer.setBulletStrike(true, posX, posY, weapon.getWeaponType().hitSize);
        }
    }

    public void die() {
        alive = false;
    }

    public void checkForWall() {

        if (getPixelColor((int) posX, (int) posY).equals(white)) {
            struck = true;
            SoundEffects.playSound("bulletStrike");
            die();
        }
    }

    public void checkForMonster() {
        double distance = MathFunctions.distanceFromMe(posX, posY, monster.getCoorX(), monster.getCoorY());
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
        shapeRenderer = new ShapeRenderer();
        white = new Color(255, 255, 255);
    }

    public void initSprites() {
        bulletTexture = new Texture(Gdx.files.internal("TexSprites/Untitled.png"));
        bulletSprite = new Sprite(bulletTexture);
        bulletStrikeTexture = new Texture(Gdx.files.internal("TexSprites/bulletImpactSprite.png"));
    }

}
