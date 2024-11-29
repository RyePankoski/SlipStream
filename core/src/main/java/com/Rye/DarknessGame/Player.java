package com.Rye.DarknessGame;

import com.Rye.DarknessGame.KeyLibrary.Key;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player {
    //region Variables

    boolean sprint, staminaPause, staminaRegen, canChangeGun, canMelee, canFlashLight;
    float camX, camY;
    private float coorX, coorY, faceX, faceY, facingAngle, factor;
    public float speed, cameraZoom;
    OrthographicCamera camera;
    int mapWidth, mapHeight;
    double monsterDistance, flashlightBattery, health, beepTimer;
    private double stamina, waitUntil, haltUntil, timeTillChange, timeTillMelee, timeTillCanFlash, timeTillCanToggleSearch;
    public boolean playerHurt, searchPatternIsOn, canToggleSearch, moving, inPopUp, flashLightIsOn;
    String equippedWeaponName;
    Weapon equippedWeapon;
    SubMachineGun smg;
    Rifle rifle;
    ArrayList<Bullet> bullets;
    Map<String, Key> keys;
    private Sprite playerSprite, mouseCursorSprite;
    private SpriteBatch spriteBatch;
    BitmapFont bitmapFont;
    ShapeRenderer shapeRenderer;
    Color white;
    Hud hud;
    Monster monster;

    public Main main;
    public Pixmap collisionMap;
    //endregion

    public Player(int x, int y, int speed, Hud hud,
                  Pixmap collisionMap, Monster monster, Main main) {
        this.main = main;
        this.coorX = x;
        this.coorY = y;
        this.speed = speed;
        this.hud = hud;
        this.collisionMap = collisionMap;
        this.monster = monster;

        initVariables();
        initDrawParams();
        initCamera();
        initWeapons();
    }

    public void updatePlayer() {
        if (System.currentTimeMillis() >= haltUntil) staminaRegen = true;
        if (System.currentTimeMillis() >= timeTillCanFlash) canFlashLight = true;
        if (System.currentTimeMillis() >= timeTillChange) canChangeGun = true;
        if (System.currentTimeMillis() >= timeTillMelee) canMelee = true;
        if (System.currentTimeMillis() >= timeTillCanToggleSearch) canToggleSearch = true;


        if (!inPopUp) {
            handleMouse();
            move();
            weapon();
        }

        variableUpdates();
        updateCamera();
        manageHealth();
        startShapeRender();
        drawMyself();
        drawCursor();
        stopShapeRender();
        melee();
        flashLight();
        ronaldProximity();
    }

    public void variableUpdates() {
        hud.updatePlayerStats(stamina, equippedWeaponName, flashlightBattery, health);
        hud.updateWeaponStats(equippedWeapon.getAmmo(), equippedWeapon.getMagazines(), equippedWeapon.getMagazineSize(), equippedWeapon.maxMagazines);
        facingAngle = (float) MathFunctions.facingAngle(coorX, coorY, faceX, faceY);
        monsterDistance = (float) MathFunctions.distanceFromMe(coorX, coorY, monster.getCoorX(), monster.getCoorY());
    }

    public void manageHealth() {

        if (health < 100) {
            health += 0.01;
        }

        if (monsterDistance < 50) {
            playerHurt = true;

            SoundEffects.playMusic("playerDamagedSound");
            SoundEffects.playMusic("playerDamagedGrunt");
            health -= .5;
        } else {
            SoundEffects.stopMusic("playerDamagedGrunt");
        }

        if (health <= 0) {
            health = 0;
        }
    }

    public void flashLight() {

        if ((int) (flashlightBattery) == 20 && flashLightIsOn) {
            SoundEffects.playMusic("flashLightWarning");
        }

        if (flashlightBattery <= 0) {
            flashlightBattery = 0;
            flashLightIsOn = false;
            searchPatternIsOn = false;
        }


        if (flashLightIsOn && flashlightBattery > 0) {
            flashlightBattery -= 0.005;
        } else if (flashlightBattery <= 100 && !searchPatternIsOn) {
            flashlightBattery += 0.02;
            if (flashlightBattery > 100) {
                flashlightBattery = 100;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && canFlashLight) {
            SoundEffects.playSound("flashLightSound");
            flashLightIsOn = !flashLightIsOn;
            canFlashLight = false;
            timeTillCanFlash = System.currentTimeMillis() + 1000;
        }
    }

    public void weapon() {

        if (canChangeGun) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                SoundEffects.playSound("changeGun");

                canChangeGun = false;
                timeTillChange = System.currentTimeMillis() + 1500;

                equippedWeaponName = "SMG";
                equippedWeapon = smg;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                SoundEffects.playSound("changeGun");

                canChangeGun = false;
                timeTillChange = System.currentTimeMillis() + 1500;

                equippedWeaponName = "RIFLE";
                equippedWeapon = rifle;
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && canChangeGun) {
            equippedWeapon.fireWeapon();
            if (equippedWeapon.getAmmo() > 0) {
                staminaRegen = false;
            }
            haltUntil = System.currentTimeMillis() + 1000;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && canChangeGun) {
            equippedWeapon.reloadWeapon();
        }
    }

    public void melee() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            if (canMelee) {
                if (monsterDistance < 200) {
                    monster.hitByMelee();
                    SoundEffects.playSound("meleeHit");
                } else {
                    SoundEffects.playSound("meleeMiss");
                }
                canMelee = false;
                timeTillMelee = MathFunctions.secondsToNano(2);
            }
        }
    }

    public void ronaldProximity() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.T) && canToggleSearch) {
            SoundEffects.playMusic("turnOnSearch");
            searchPatternIsOn = !searchPatternIsOn;
            canToggleSearch = false;
            timeTillCanToggleSearch = System.currentTimeMillis() + 1000;
        }
        if (searchPatternIsOn) {
            SoundEffects.playMusic("searchPatternSound");
        } else {
            SoundEffects.stopMusic("searchPatternSound");
        }

        if (searchPatternIsOn && monsterDistance < 7000) {
            flashlightBattery -= 0.02;
            if (beepTimer <= 0) {
                if (monsterDistance < 500) {
                    beepTimer = 10;
                } else if (monsterDistance < 2000) {
                    beepTimer = 20;
                } else if (monsterDistance < 3500) {
                    beepTimer = 40;
                } else if (monsterDistance < 5000) {
                    beepTimer = 80;
                } else if (monsterDistance < 6500) {
                    beepTimer = 160;
                } else {
                    beepTimer = 320;
                }
                SoundEffects.playSound("searchBeep");
            }
            if (beepTimer > 0) {
                beepTimer--;
            }
        }
    }

    public void checkBullets() {
        if (!bullets.isEmpty()) {
            for (int i = bullets.size() - 1; i >= 0; i--) {
                if (bullets.get(i).isAlive()) {
                    bullets.get(i).castRay();
                } else {
                    bullets.remove(i);
                    System.gc();
                }
            }
        }
    }

    public void updateBullets(Bullet bulletz) {
        bullets.add(bulletz);
    }

    public void handleMouse() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        setFacing(mousePos.x, mousePos.y);
    }

    public void move() {
        moving = false;
        boolean movingUp = false;
        boolean movingDown = false;
        boolean movingLeft = false;
        boolean movingRight = false;

        float dy = 0;
        float dx = 0;
        float moveSpeed;

        sprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        if (stamina <= 0) {
            sprint = false;
        }

        moveSpeed = sprint ? speed * factor : speed;
        moveSpeed *= (float) ((0.4 + (0.6 * (stamina / 100))));


        //region key input management
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy = moveSpeed;
            movingUp = true;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy = -moveSpeed;
            movingDown = true;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx = moveSpeed;
            movingRight = true;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx = -moveSpeed;
            movingLeft = true;
            moving = true;
        }
        //endregion

        //region stamina management
        if (stamina <= 0) {
            if (!staminaPause) {
                staminaPause = true;
                waitUntil = System.currentTimeMillis() + 3000;
            }
            if (System.currentTimeMillis() >= waitUntil) {
                staminaPause = false;
            }
        }

        if (sprint && stamina > 0 && (Math.abs(dy) + Math.abs(dx)) > 0) {
            stamina -= 0.1;
        } else if (stamina < 100 && !staminaPause && staminaRegen) {
            if (dx == 0 && dy == 0) {
                stamina += 0.03;
            } else {
                stamina += 0.01;
            }
        }
        if (stamina < 0) {
            stamina = 0;
        }
        //endregion

        //region sound management
        if (sprint) {
            SoundEffects.stopMusic("walking");
            SoundEffects.playMusic("running");
        } else {
            SoundEffects.stopMusic("running");
            SoundEffects.playMusic("walking");
        }

        if (dx == 0 && dy == 0) {
            SoundEffects.stopMusic("walking");
            SoundEffects.stopMusic("running");
        }
        //endregion

        //region Normalization
        float inputX = Math.abs(dx);
        float inputY = Math.abs(dy);
        double magnitude = MathFunctions.fastSqrt((dx * dx) + (dy * dy));
        if (magnitude != 0) {
            dx /= (float) magnitude;
            dy /= (float) magnitude;
        }

        dx *= inputX;
        dy *= inputY;
        //endregion

        //region edge of level collision
        if (coorX + dx < 10) {
            coorX = 10;
        } else if (coorY + dy < 10) {
            coorY = 10;
        } else if (coorX + dx > collisionMap.getWidth() - 10) {
            coorX = collisionMap.getWidth() - 10;
        } else if (coorY + dy > collisionMap.getHeight() - 10) {
            coorY = collisionMap.getHeight() - 10;
        }
        //endregion

        //region collision
        if (movingRight) {
            if (MathFunctions.getPixelColor((int) coorX + 10, (int) coorY, collisionMap).equals(white)) {
                dx = 0;
            }
        } else if (movingLeft) {
            if (MathFunctions.getPixelColor((int) coorX - 10, (int) coorY, collisionMap).equals(white)) {
                dx = 0;
            }
        }
        if (movingUp) {
            if (MathFunctions.getPixelColor((int) coorX, (int) coorY + 10, collisionMap).equals(white)) {
                dy = 0;
            }
        } else if (movingDown) {
            if (MathFunctions.getPixelColor((int) coorX, (int) coorY - 10, collisionMap).equals(white)) {
                dy = 0;
            }
        }
        //endregion

        coorX += dx;
        coorY += dy;
    }

    public void updateCamera() {

        if (coorX + (cameraZoom / 2) > mapWidth) {
            camX = mapWidth - cameraZoom / 2;
        } else if (coorX - (cameraZoom / 2) < 0) {
            camX = cameraZoom / 2;
        } else {
            camX = coorX;
        }

        if (coorY + (cameraZoom / 2) > mapHeight) {
            camY = mapHeight - cameraZoom / 2;
        } else if (coorY - (cameraZoom / 2) < 0) {
            camY = cameraZoom / 2;
        } else {
            camY = coorY;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            double[] aimPosition = MathFunctions.pointInFront(coorX, coorY, faceX, faceY, 150);
            camX = (float) aimPosition[0];
            camY = (float) aimPosition[1];
        }

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    public void startShapeRender() {
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }

    public void stopShapeRender() {
        spriteBatch.end();
        shapeRenderer.end();
    }

    public void drawCursor() {
        mouseCursorSprite.setPosition(faceX - mouseCursorSprite.getWidth() / 2, faceY - mouseCursorSprite.getWidth() / 2);
        mouseCursorSprite.draw(spriteBatch);
    }

    public void drawMyself() {
        playerSprite.setPosition(getCoorX() - playerSprite.getWidth() / 2, getCoorY() - playerSprite.getHeight() / 2);
        playerSprite.setRotation(getFacingAngle() + 90);
        playerSprite.draw(spriteBatch);
    }

    public void initWeapons() {
        smg = new SubMachineGun(0.12f, 3, 35, 6, initWeaponSounds("smg"), camera, this, hud, 15, this.monster, 50, 3);
        rifle = new Rifle(2, 15, 1, 25, initWeaponSounds("rifle"), camera, this, hud, 30, this.monster, 80, 15);

        equippedWeapon = smg;
    }

    public void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraZoom, cameraZoom);
    }

    public void initVariables() {
        bullets = new ArrayList<>();
        keys = new HashMap<>();

        health = 100;
        stamina = 100;
        equippedWeaponName = "SMG";
        cameraZoom = 500;
        mapWidth = 15000;
        mapHeight = 10000;
        flashlightBattery = 100;
        factor = 1.8f;
    }

    public void initDrawParams() {

        Texture playerTexture = new Texture(Gdx.files.internal("TexSprites/PlayerChar.png"));
        Texture mouseCursor = new Texture(Gdx.files.internal("TexSprites/crosshair003.png"));
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        playerSprite = new Sprite(playerTexture);
        mouseCursorSprite = new Sprite(mouseCursor);
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);
    }

    private ArrayList<Sound> initWeaponSounds(String weaponType) {
        ArrayList<Sound> sounds = new ArrayList<>();
        sounds.add(Gdx.audio.newSound(Gdx.files.internal("SoundEffects/" + weaponType + "Shot.mp3")));
        sounds.add(Gdx.audio.newSound(Gdx.files.internal("SoundEffects/" + weaponType + "Reload.mp3")));
        sounds.add(Gdx.audio.newSound(Gdx.files.internal("SoundEffects/emptyGun.mp3")));
        return sounds;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getFacingAngle() {
        return facingAngle;
    }

    public float getCoorX() {
        return coorX;
    }

    public float getCoorY() {
        return coorY;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = stamina;
    }

    public void setFacing(float x, float y) {
        faceX = x;
        faceY = y;
    }

    public float getFaceX() {
        return faceX;
    }

    public float getFaceY() {
        return faceY;
    }

    public boolean getFlashLight() {
        return flashLightIsOn;
    }

    public double getBattery() {
        return flashlightBattery;
    }

    public Pixmap getCollisionMap() {
        return collisionMap;
    }

    public void addToCoors(int x, int y) {
        coorX += x;
        coorY += y;
    }

    public Map<String, Key> getKeys() {
        return keys;
    }

    public void addKey(String sectorAndNumber, Key key) {
        keys.put(sectorAndNumber, key);
    }

    public void setInPopUp(boolean inPopUp) {
        this.inPopUp = inPopUp;
    }
}
