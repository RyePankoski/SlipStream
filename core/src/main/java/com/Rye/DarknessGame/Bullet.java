package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.*;

public class Bullet {

    CollisionMask collisionMask;
    Pixmap pixmap;
    double startX;
    double startY;
    double facingX;
    double facingY;
    double posX;
    double posY;
    boolean alive;
    Color white;
    Sound bulletStrike;

    Sound monsterStrike;
    ShapeRenderer shapeRenderer;
    double bulletSpeed;
    Texture bulletStrikeTexture;
    SpriteBatch spriteBatch;
    Player player;
    Monster monster;
    Weapon weapon;
    double dx;
    double dy;

    public Bullet(CollisionMask collisionMask, Player player, double bulletSpeed, Monster monster, Weapon weapon) {
        this.collisionMask = collisionMask;
        this.player = player;
        this.bulletSpeed = bulletSpeed;
        this.monster = monster;
        this.weapon = weapon;

        initVariables();
        initSounds();
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
        shapeRenderer.circle((float) posX, (float) posY, 10);
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

            double max = 1000;
            double xzes = facingX - startX;
            double yzes = facingY - startY;
            double distance = Math.sqrt((xzes * xzes) + (yzes * yzes));
            double percent = ((max - distance) / max);

            // Clamp percent to the range [0.0, 1.0]
            if (percent < 0) {
                percent = 0;
            } else if (percent > 1) {
                percent = 1;
            }

            // Play the sound with the calculated volume
            bulletStrike.play((float) percent);
            spriteBatch.setProjectionMatrix(player.getCamera().combined);
            spriteBatch.begin();
            spriteBatch.draw(bulletStrikeTexture, ((float) (posX - bulletStrikeTexture.getWidth() / 2f)), ((float) (posY - bulletStrikeTexture.getHeight() / 2f)));
            spriteBatch.end();

            player.main.darknessLayer.setBulletStrike(true, posX, posY, weapon.getWeaponType().hitSize);
            die();
        }
    }

    public void checkForMonster() {
        double x1 = posX;
        double y1 = posY;

        double x2 = monster.coorX;
        double y2 = monster.coorY;

        double newX = x2 - x1;
        double newY = y2 - y1;
        double distance = Math.sqrt((newX * newX) + (newY * newY));

        if (distance < 100) {
            monster.hitByBullet(weapon.getWeaponType());
            monsterStrike.play();
            die();
        }
    }

    public void checkOutOfBounds() {
        if (posX < 0 || posY < 0) {
            die();
        }
        if (posX > pixmap.getWidth() || posY > pixmap.getHeight()) {
            die();
        }
    }

    private Color getPixelColor(int x, int y) {
        int pixel = pixmap.getPixel(x, pixmap.getHeight() - y);

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
        pixmap = collisionMask.getPixmap();
        white = new Color(255, 255, 255);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        pixmap = collisionMask.getPixmap();
    }

    public void initSounds() {
        bulletStrike = Gdx.audio.newSound(Gdx.files.internal("BulletSFX/bulletStrike.mp3"));
        monsterStrike = Gdx.audio.newSound((Gdx.files.internal(("BulletSFX/monsterStrikeSound.mp3"))));
    }

    public void initSprites() {
        bulletStrikeTexture = new Texture(Gdx.files.internal("TexSprites/bulletImpactSprite.png"));
    }

}
