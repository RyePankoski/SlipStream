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

    public Hud() {
        spriteBatch = new SpriteBatch();
        bitmapFont = new BitmapFont();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("computaFont.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 160;
        bitmapFont = generator.generateFont(parameter);
        generator.dispose();

        plaque = new Texture(Gdx.files.internal("dataPlaque.png"));

        GiraffeX = Gdx.graphics.getWidth();
        GiraffeY = Gdx.graphics.getHeight();
    }

    public void renderHud() {
        drawPlayerStats();
        drawWeaponInfo();
    }

    public void drawPlayerStats() {
        spriteBatch.begin();

        double redTotal = Math.min(255, 2 * (1 - (stamina / 100)) * 255);
        double greenTotal = 255 - Math.max(0, (1 - (2 * (stamina / 100))) * 255);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.draw(plaque, camera.position.x - 1000, (float) (camera.position.y + cameraZoom / 2 - 300));
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, currentWeapon, camera.position.x - 900, (float) (camera.position.y + cameraZoom / 2) - 70);
        bitmapFont.draw(spriteBatch, "BATT:", camera.position.x - 900, (float) (camera.position.y + cameraZoom / 2 - 180));
        hardStaminaValue = stamina;
        hardStaminaValue = Math.floor(hardStaminaValue);
        staminaValue = String.valueOf(hardStaminaValue);
        bitmapFont.draw(spriteBatch, "STAMINA:", camera.position.x + 0, (float) (camera.position.y + cameraZoom / 2 - 70));
        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);
        bitmapFont.draw(spriteBatch, staminaValue, camera.position.x + 500, (float) (camera.position.y + cameraZoom / 2 - 70));
        flashlightBattery = Math.ceil(flashlightBattery);
        flashlightBatteryString = String.valueOf((int) flashlightBattery);

        redTotal = Math.min(255, 2 * (1 - (flashlightBattery / 100)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (flashlightBattery / 100))) * 255);

        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);
        bitmapFont.draw(spriteBatch, flashlightBatteryString + "%", camera.position.x - 600, (float) (camera.position.y + cameraZoom / 2 - 180));

        spriteBatch.end();
    }

    public void drawWeaponInfo() {

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        double redTotal = Math.min(255, 2 * (1 - (ammo / maxAmmo)) * 255);
        double greenTotal = 255 - Math.max(0, (1 - (2 * (ammo / maxAmmo))) * 255);

        if (ammo == 0) {
            bitmapFont.setColor(Color.RED);
            bitmapFont.draw(spriteBatch, "Reload!", (camera.position.x + 300), (float) (camera.position.y + cameraZoom / 2 - 180));
        }

        ammoNumberString = String.valueOf((int) ammo);
        bitmapFont.setColor((float) redTotal / 255, (float) greenTotal / 255, 0, 1f);
        bitmapFont.draw(spriteBatch, ammoNumberString, (camera.position.x + 160), (float) (camera.position.y + cameraZoom / 2 - 180));

        redTotal = Math.min(255, 2 * (1 - (magazines / maxMagazines)) * 255);
        greenTotal = 255 - Math.max(0, (1 - (2 * (magazines / maxMagazines))) * 255);

        bitmapFont.setColor((float) redTotal / 255f, (float) greenTotal / 255f, 0, 1f);
        String magazineNumber = String.valueOf((int) magazines);
        bitmapFont.draw(spriteBatch, magazineNumber + ":", camera.position.x, (float) (camera.position.y + cameraZoom / 2 - 180));
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

    public void updatePlayerStats(double stamina, String currentWeapon, double flashlightBattery) {
        this.flashlightBattery = flashlightBattery;
        this.stamina = stamina;
        this.currentWeapon = currentWeapon;
    }
}
