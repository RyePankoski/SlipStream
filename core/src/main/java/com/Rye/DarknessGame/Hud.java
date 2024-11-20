package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Hud {
    BitmapFont bitmapFont;
    String ammoNumberString;
    String staminaValue;
    SpriteBatch spriteBatch;
    double hardStaminaValue;
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    double ammo;
    double magazines;
    int maxAmmo;
    double stamina;
    OrthographicCamera camera;
    String currentWeapon;

    Texture plaque;
    double cameraZoom;

    double GiraffeX;
    double GiraffeY;

    double flashlightBattery;
    String flashlightBatteryString;
    double maxMagazines;

    double health;

    float offsetX = 500;

    float offsetY = 500;

    String healthValue;
    Player player;

    public Hud() {
        this.player = player;
        spriteBatch = new SpriteBatch();
        bitmapFont = new BitmapFont();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/computaFont.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        bitmapFont = generator.generateFont(parameter);
        generator.dispose();

        plaque = new Texture(Gdx.files.internal("TexSprites/dataPlaque.png"));

        GiraffeX = Gdx.graphics.getWidth();
        GiraffeY = Gdx.graphics.getHeight();
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public void renderHud() {
        drawPlayerStats();
        drawWeaponInfo();
    }

    public void drawPlayerStats() {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        double redTotal;
        double greenTotal;

        spriteBatch.draw(plaque, (int)(camera.position.x - cameraZoom/2), (float) (camera.position.y + cameraZoom / 2) - 50);
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, currentWeapon, (camera.position.x - 240), (float) (camera.position.y + cameraZoom / 2) - 10);
        bitmapFont.draw(spriteBatch, "BATT:", (camera.position.x - 240), (float) (camera.position.y + cameraZoom / 2 - 30));

        redTotal = Math.min(255, 2 * (1 - (health / 100)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (health / 100))) * 255);

        bitmapFont.setColor((float) redTotal, (float) greenTotal, 0, 1f);
        healthValue = String.valueOf((int) health);
        bitmapFont.draw(spriteBatch, healthValue, (camera.position.x - 100), (float) (camera.position.y + cameraZoom / 2 - 30));

        redTotal = Math.min(255, 2 * (1 - (stamina/100)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (stamina/100))) * 255);

        hardStaminaValue = stamina;
        hardStaminaValue = stamina;
        hardStaminaValue = Math.floor(hardStaminaValue);
        staminaValue = String.valueOf(hardStaminaValue);
        bitmapFont.setColor(Color.GREEN);

        bitmapFont.draw(spriteBatch, "+:", (camera.position.x - 120), (float) (camera.position.y + cameraZoom / 2 - 30));
        bitmapFont.draw(spriteBatch, "STAMINA:", camera.position.x, (float) (camera.position.y + cameraZoom / 2 - 10));

        bitmapFont.draw(spriteBatch,"X:" + String.valueOf((int)(camera.position.x)/23),camera.position.x + 180,(float)(camera.position.y + cameraZoom/2 - 10));
        bitmapFont.draw(spriteBatch,"Y:" + String.valueOf((int)(camera.position.y)/32),camera.position.x + 180,(float)(camera.position.y + cameraZoom/2 -30));

        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);

        bitmapFont.draw(spriteBatch, staminaValue, camera.position.x + 60, (float) (camera.position.y + cameraZoom / 2 - 10));
        flashlightBattery = Math.ceil(flashlightBattery);
        flashlightBatteryString = String.valueOf((int) flashlightBattery);

        redTotal = Math.min(255, 2 * (1 - (flashlightBattery / 100)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (flashlightBattery / 100))) * 255);

        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);
        bitmapFont.draw(spriteBatch, flashlightBatteryString + "%", (camera.position.x - 200), (float) (camera.position.y + cameraZoom / 2 -30));

        if (player.searchPattern) {
            bitmapFont.setColor(Color.RED);
            bitmapFont.draw(spriteBatch, "<-", (camera.position.x - 160), (float) (camera.position.y + cameraZoom / 2 - 30));
        }

        spriteBatch.end();
    }

    public void drawWeaponInfo() {

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        double redTotal = Math.min(255, 2 * (1 - (ammo / maxAmmo)) * 255);
        double greenTotal = 255 - Math.max(0, (1 - (2 * (ammo / maxAmmo))) * 255);

        if (ammo == 0) {
            bitmapFont.setColor(Color.RED);
            bitmapFont.draw(spriteBatch, "Reload!", (camera.position.x + 40 ), (float) (camera.position.y + cameraZoom / 2 - 30));
        }

        ammoNumberString = String.valueOf((int) ammo);
        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);
        bitmapFont.draw(spriteBatch, ammoNumberString, (camera.position.x +20), (float) (camera.position.y + cameraZoom / 2 - 30));

        redTotal = Math.min(255, 2 * (1 - (magazines / maxMagazines)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (magazines / maxMagazines))) * 255);

        bitmapFont.setColor((float) redTotal / 255f, (float) greenTotal / 255f, 0, 1f);
        String magazineNumber = String.valueOf((int) magazines);
        bitmapFont.draw(spriteBatch, magazineNumber + ":", camera.position.x, (float) (camera.position.y + cameraZoom / 2 - 30));
        spriteBatch.end();
    }

    public void setCamera(OrthographicCamera camera, double cameraZoom, double flashlightBattery) {
        this.camera = camera;
        this.cameraZoom = cameraZoom;
        this.flashlightBattery = flashlightBattery;
    }

    public void updateWeaponStats(double ammo, int magazines, int maxAmmo, int maxMagazines) {
        this.ammo = ammo;
        this.magazines = magazines;
        this.maxAmmo = maxAmmo;
        this.maxMagazines = maxMagazines;
    }

    public void updatePlayerStats(double stamina, String currentWeapon, double flashlightBattery, double health) {
        this.flashlightBattery = flashlightBattery;
        this.stamina = stamina;
        this.currentWeapon = currentWeapon;
        this.health = health;
    }
}
