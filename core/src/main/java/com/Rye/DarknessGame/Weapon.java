package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


import java.util.ArrayList;


public class Weapon {
    float fireRate;
    int magazines;
    int magazineSize;
    int damage;
    int ammo;
    ArrayList<Sound> sounds;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    Player player;
    private boolean canDryFire = true;
    protected boolean canFire = true;
    protected boolean canReload = true;
    protected double timeTillReload;
    double timeTillNextDryFire;
    double timeTillNextShot;
    Sound emptyGunSound;
    Sound gunShotOne;
    Sound reloadSound;
    Hud hud;
    double[] pointInFrontVector;
    Bullet bullet;
    double bulletSpeed;
    SpriteBatch spriteBatch;
    Texture gunFlashTexture;
    Sprite gunFlashSprite;
    protected boolean canAmmoSound = true;
    Sound outOfAmmoSound;
    Monster monster;
    public int hitSize;

    int maxMagazines;


    public Weapon(float fireRate, int magazines, int magazineSize, int damage, ArrayList<Sound> sounds,
                  OrthographicCamera camera, Player player, Hud hud, double bulletSpeed, Monster monster, int hitSize, int maxMagazines) {
        this.maxMagazines = maxMagazines;
        this.fireRate = fireRate;
        this.magazines = magazines;
        this.magazineSize = magazineSize;
        this.damage = damage;
        this.sounds = sounds;
        this.camera = camera;
        this.player = player;
        this.hud = hud;
        this.bulletSpeed = bulletSpeed;
        this.monster = monster;
        this.hitSize = hitSize;

        initVariables();
        initTextures();
        initSounds();
        initDrawParams();
    }



    public void updateInfo() {
        hud.updateWeaponStats(ammo, magazines, magazineSize, maxMagazines);
    }

    public void fireWeapon() {

        if (canFire && ammo > 0) {

            player.main.darknessLayer.setJustFired(true);

            player.updateBullets(new Bullet(player.getCollisionMask(), player, bulletSpeed, monster, this));

            gunShotOne.play();

            pointInFrontVector = player.getPointInFrontVector();
            spriteBatch.setProjectionMatrix(camera.combined);

            spriteBatch.begin();
            gunFlashSprite.setPosition((float) pointInFrontVector[0] - gunFlashSprite.getWidth() / 2,
                (float) pointInFrontVector[1] - gunFlashSprite.getHeight() / 2);
            gunFlashSprite.setRotation(player.getFacingAngle() + 90);
            gunFlashSprite.draw(spriteBatch);
            spriteBatch.end();

            ammo--;
            canFire = false;

            timeTillNextShot = System.nanoTime() + MathFunctions.secondsToNano(fireRate);
        }
        if (System.nanoTime() >= timeTillNextShot) {
            canFire = true;
        }

        //DryFire
        if (canDryFire && ammo == 0) {
            emptyGunSound.play();
            canDryFire = false;
            double dryFireRate = 500;
            timeTillNextDryFire = System.currentTimeMillis() + dryFireRate;
        }
        if (System.currentTimeMillis() >= timeTillNextDryFire) {
            canDryFire = true;
        }
        updateInfo();
    }

    public void reloadWeapon() {
        if (magazines > 0 && canReload) {
            canAmmoSound = true;
            reloadSound.play();
            ammo = magazineSize;
            magazines--;
            canReload = false;
            timeTillReload = System.currentTimeMillis() + 2000;
        }
        if (System.currentTimeMillis() >= timeTillReload) {
            canReload = true;
        }
        updateInfo();
    }

    public int getMagazines() {
        return magazines;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public int getDamage() {
        return damage;
    }

    public Weapon getWeaponType() {
        return null;
    }

    public int getAmmo() {
        return ammo;
    }

    public void initVariables() {
        ammo = magazineSize;
    }

    public void initDrawParams() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
    }

    public void initTextures() {
        gunFlashTexture = new Texture(Gdx.files.internal("TexSprites/shotFlash.png"));
        gunFlashSprite = new Sprite(gunFlashTexture);
    }

    public void initSounds() {
        outOfAmmoSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/outOfAmmoSound.mp3"));
        gunShotOne = sounds.get(0);
        reloadSound = sounds.get(1);
        emptyGunSound = sounds.get(2);
    }
}
