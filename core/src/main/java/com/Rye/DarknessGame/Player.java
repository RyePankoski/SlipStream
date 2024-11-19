package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

import static java.lang.Math.sqrt;

public class Player {

    //region Variables

    // **Booleans**
    boolean sprint, staminaPause, staminaRegen, canChangeGun = true, flashLightIsOn = true, canMelee = true, canFlashLight;

    // **Coordinates and Positioning**
    private float coorX;
    private float coorY;
    private float faceX;
    private float faceY;

    // **Camera and Viewport**
    OrthographicCamera camera;
    float cameraZoom;

    // **Speed, Angles, and Movement Factors**
    public float speed;
    float facingAngle;
    float factor = 2.5f;

    // **Room Dimensions and Overlaps**
    int roomWidth, roomHeight;

    // **Stamina and Timing Control**
    private double stamina = 100, waitUntil, haltUntil, timeTillChange, timeTillMelee, timeTillCanFlash;

    // **Weapon and Shooting Mechanics**
    String equippedWeaponName;
    Weapon equippedWeapon;
    SubMachineGun smg;
    Rifle rifle;
    ArrayList<Bullet> bullets;

    // **Sound Effects**
    private Sound running, walking, changeGun, meleeHit, meleeMiss, flashLightSound;
    private Music flashLightWarning, playerDamagedSound;
    SoundPlayer soundManager;

    // **Input Handling**
    InputHandler handler;
    Set<Integer> keyStrokes, pressedMouseHash;

    // **Graphics and Rendering**
    private Sprite playerSprite;
    private SpriteBatch spriteBatch;
    BitmapFont bitmapFont;
    ShapeRenderer shapeRenderer;
    Pixmap pixmap;
    Color white;

    // **Collision and Vectors**
    double[] pointInFrontVector;
    CollisionMask collisionMask;

    // **HUD and UI Elements**
    Hud hud;

    // **Monster and Interaction**
    Monster monster;
    double monsterDistance;

    // **Temporary Vectors**
    private final Vector2 tempVector = new Vector2();

    // **Flashlight Battery**
    double flashlightBattery;

    // **Game Main Reference**
    public Main main;

    double health;

    public boolean playerHurt;
    public Music playerDamagedGrunt;
    float camX;
    float camY;

    //endregion

    public Player(int x, int y, int speed, SoundPlayer soundManager, InputHandler handler, Hud hud,
                  CollisionMask collisionMask, Monster monster, Main main) {

        this.main = main;
        this.handler = handler;
        this.soundManager = soundManager;
        this.coorX = x;
        this.coorY = y;
        this.speed = speed;
        this.hud = hud;
        this.collisionMask = collisionMask;
        this.monster = monster;

        initVariables();
        initDrawParams();
        initPlayerSounds();
        initCamera();
        initWeapons();

        hud.updatePlayerStats(stamina, equippedWeaponName, flashlightBattery, health);
        hud.updateWeaponStats(equippedWeapon.getAmmo(), equippedWeapon.getMagazines(), equippedWeapon.getMagazineSize(), equippedWeapon.maxMagazines);
    }

    public void updatePlayer() {
        keyStrokes = handler.getPressedKeysHash();
        pressedMouseHash = handler.getPressedMouseHash();
        if (System.nanoTime() >= haltUntil) staminaRegen = true;

        move();
        facingAngle();
        updateCamera();
        drawMyself();
        weapon();
        distanceToMonster();
        manageHealth();
        melee();
        flashLight();
        pointInFront();

        hud.updatePlayerStats(stamina, equippedWeaponName, flashlightBattery, health);
        hud.updateWeaponStats(equippedWeapon.getAmmo(), equippedWeapon.getMagazines(), equippedWeapon.getMagazineSize(), equippedWeapon.maxMagazines);
    }

    public void distanceToMonster() {
        double x1 = coorX;
        double y1 = coorY;
        double x2 = monster.getCoorX();
        double y2 = monster.getCoorY();

        double newX = x2 - x1;
        double newY = y2 - y1;
        monsterDistance = Math.sqrt((newX * newX) + (newY * newY));
    }

    public void manageHealth() {

        if (health < 100) {
            health += 0.01;
        }

        if (monsterDistance < 200) {
            playerHurt = true;
            if (!playerDamagedSound.isPlaying()) {
                playerDamagedSound.play();
            }
            if (!playerDamagedGrunt.isPlaying()) {
                playerDamagedGrunt.play();
            }
            health -= .5;
        } else {
            playerDamagedGrunt.stop();
        }

        if (health <= 0) {
            health = 0;
        }

    }

    public void pointInFront() {
        tempVector.set(faceX - coorX, faceY - coorY);
        float distance = tempVector.len();

        if (distance != 0) {
            tempVector.scl(200f / distance);
            pointInFrontVector[0] = coorX + tempVector.x;
            pointInFrontVector[1] = coorY + tempVector.y;
        }
    }

    public void flashLight() {

        if ((int) (flashlightBattery) == 20 && flashLightIsOn) {
            if (!flashLightWarning.isPlaying()) {
                flashLightWarning.play();
            }
        }

        if (flashlightBattery <= 0) {
            flashLightSound.play();
            flashlightBattery = 0;
            flashLightIsOn = false;
        }


        if (flashLightIsOn && flashlightBattery > 0) {
            flashlightBattery -= 0.005;
        } else if (flashlightBattery <= 100) {
            flashlightBattery += 0.02;
            if (flashlightBattery > 100) {
                flashlightBattery = 100;
            }


        }
        if (System.nanoTime() >= timeTillCanFlash) {
            canFlashLight = true;
        }
        if (keyStrokes.contains(Input.Keys.F) && canFlashLight) {
            flashLightSound.play();
            flashLightIsOn = !flashLightIsOn;
            canFlashLight = false;
            timeTillCanFlash = System.nanoTime() + main.secondsToNano(1);
        }
    }

    public void weapon() {

        if (canChangeGun) {

            if (keyStrokes.contains(Input.Keys.NUM_1)) {
                changeGun.play();

                canChangeGun = false;
                timeTillChange = System.nanoTime() + main.secondsToNano(1.5f);

                equippedWeaponName = "SMG";
                equippedWeapon = smg;
            }
            if (keyStrokes.contains(Input.Keys.NUM_2)) {
                changeGun.play();

                canChangeGun = false;
                timeTillChange = System.nanoTime() + main.secondsToNano(1.5f);

                equippedWeaponName = "RIFLE";
                equippedWeapon = rifle;
            }
        }

        if (System.nanoTime() >= timeTillChange) {
            canChangeGun = true;
        }

        if (pressedMouseHash.contains(Input.Buttons.LEFT) && canChangeGun) {


            equippedWeapon.fireWeapon();

            if (equippedWeapon.getAmmo() > 0) {
                staminaRegen = false;
            }
            haltUntil = System.nanoTime() + main.secondsToNano(1);
        }
        if (keyStrokes.contains(Input.Keys.R) && canChangeGun) {
            equippedWeapon.reloadWeapon();
        }


    }

    public void melee() {
        if (System.nanoTime() == timeTillMelee) {
            canMelee = true;
        }
        if (keyStrokes.contains(Input.Keys.V)) {
            if (canMelee) {
                if (monsterDistance < 200) {
                    monster.hitByMelee();
                    meleeHit.play();
                } else {
                    meleeMiss.play();
                }
                canMelee = false;
                timeTillMelee = main.secondsToNano(2);
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

    public void move() {
        boolean movingUp = false;
        boolean movingDown = false;
        boolean movingLeft = false;
        boolean movingRight = false;

        float dy = 0;
        float dx = 0;
        float moveSpeed;

        sprint = keyStrokes.contains(Input.Keys.SHIFT_LEFT);
        if (stamina <= 0) {
            sprint = false;
        }

        moveSpeed = sprint ? speed * factor : speed;
        moveSpeed *= (float) ((0.4 + (0.6 * (stamina / 100))));


        //region key input management
        if (keyStrokes.contains(Input.Keys.W)) {
            dy = moveSpeed;
            movingUp = true;
        }
        if (keyStrokes.contains(Input.Keys.S)) {
            dy = -moveSpeed;
            movingDown = true;
        }
        if (keyStrokes.contains(Input.Keys.D)) {
            dx = moveSpeed;
            movingRight = true;
        }
        if (keyStrokes.contains(Input.Keys.A)) {
            dx = -moveSpeed;
            movingLeft = true;
        }
        //endregion

        //region stamina management
        if (stamina <= 0) {
            if (!staminaPause) {
                staminaPause = true;
                waitUntil = System.nanoTime() + main.secondsToNano(3);
            }
            if (System.nanoTime() >= waitUntil) {
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
            soundManager.stopSound("walk", walking);
            soundManager.playSound("run", running);
        } else {
            soundManager.stopSound("run", running);
            soundManager.playSound("walk", walking);
        }

        if (dx == 0 && dy == 0) {
            soundManager.stopSound("run", running);
            soundManager.stopSound("walk", walking);
        }
        //endregion

        //region Normalization
        float inputX = Math.abs(dx);
        float inputY = Math.abs(dy);
        double magnitude = sqrt((dx * dx) + (dy * dy));

        if (magnitude != 0) {
            dx /= (float) magnitude;
            dy /= (float) magnitude;
        }
        dx *= inputX;
        dy *= inputY;
        //endregion

        //region edge of level collision
        if (coorX + dx < 80) {
            coorX = 80;
        } else if (coorY + dy < 80) {
            coorY = 80;
        } else if (coorX + dx > pixmap.getWidth() - 80) {
            coorX = pixmap.getWidth() - 80;
        } else if (coorY + dy > pixmap.getHeight() - 80) {
            coorY = pixmap.getHeight() - 80;
        }
        //endregion

        //region collision
        if (movingRight) {
            if (MathFunctions.getPixelColor((int) coorX + 80, (int) coorY, pixmap).equals(white)) {
                dx = 0;
            }
        } else if (movingLeft) {
            if (MathFunctions.getPixelColor((int) coorX - 80, (int) coorY, pixmap).equals(white)) {
                dx = 0;
            }
        }
        if (movingUp) {
            if (MathFunctions.getPixelColor((int) coorX, (int) coorY + 80, pixmap).equals(white)) {
                dy = 0;
            }
        } else if (movingDown) {
            if (MathFunctions.getPixelColor((int) coorX, (int) coorY - 80, pixmap).equals(white)) {
                dy = 0;
            }
        }
        //endregion

        coorX += dx;
        coorY += dy;

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        setFacing(mousePos.x, mousePos.y);
    }

    public void updateCamera() {

        if (coorX + (cameraZoom / 2) > roomWidth) {
            camX = roomWidth - cameraZoom / 2;
        } else if (coorX - (cameraZoom / 2) < 0) {
            camX = cameraZoom / 2;
        } else {
            camX = coorX;
        }

        if (coorY + (cameraZoom / 2) > roomHeight) {
            camY = roomHeight - cameraZoom / 2;
        } else if (coorY - (cameraZoom / 2) < 0) {
            camY = cameraZoom / 2;
        } else {
            camY = coorY;
        }

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    public void drawMyself() {
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        playerSprite.setPosition(getCoorX() - playerSprite.getWidth() / 2, getCoorY() - playerSprite.getHeight() / 2);
        playerSprite.setRotation(getFacingAngle() + 90);
        playerSprite.draw(spriteBatch);
        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.PINK);
        shapeRenderer.end();
    }

    public void facingAngle() {
        float delX = getFaceX() - getCoorX();
        float delY = getFaceY() - getCoorY();

        this.facingAngle = (float) Math.toDegrees(Math.atan2(delY, delX));
    }

    public void initWeapons() {
        smg = new SubMachineGun(0.11f, 3, 35, 6, initWeaponSounds("smg"), camera, this, hud, 60, this.monster, 150, 3);
        rifle = new Rifle(2, 15, 1, 25, initWeaponSounds("rifle"), camera, this, hud, 120, this.monster, 300, 15);

        equippedWeapon = smg;
    }

    public void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraZoom, cameraZoom);
    }

    public void initVariables() {
        health = 100;
        bullets = new ArrayList<>();
        pointInFrontVector = new double[2];
        equippedWeaponName = "SMG";
        cameraZoom = 3000;
        roomWidth = 15000;
        roomHeight = 10000;
        flashlightBattery = 100;
    }

    public void initDrawParams() {

        Texture playerTexture = new Texture(Gdx.files.internal("TexSprites/PlayerChar.png"));
        pixmap = collisionMask.getPixmap();
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        playerSprite = new Sprite(playerTexture);
        spriteBatch = new SpriteBatch();
        white = new Color(255, 255, 255);

    }

    public void initPlayerSounds() {
        running = Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/Running.mp3"));
        walking = Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/Walking.mp3"));
        changeGun = Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/changeGuns.mp3"));
        meleeHit = Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/meleeSound.mp3"));
        meleeMiss = Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/meleeMiss.mp3"));
        flashLightSound = Gdx.audio.newSound((Gdx.files.internal("PlayerSFX/flashLightSound.mp3")));
        flashLightWarning = Gdx.audio.newMusic((Gdx.files.internal("PlayerSFX/flashlightWarning.mp3")));
        playerDamagedSound = Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/playerDamaged.mp3"));
        playerDamagedGrunt = Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/playerDamagedGrunt.mp3"));
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

    public CollisionMask getCollisionMask() {
        return collisionMask;
    }

    public double[] getPointInFrontVector() {
        return pointInFrontVector;
    }

    public float getCamX() {
        return camX;
    }

    public float getCamY() {
        return camY;
    }
}
