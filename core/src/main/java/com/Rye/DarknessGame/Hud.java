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
    SpriteBatch spriteBatch;
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
    Player player;

    public Hud() {
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

    public float[] redGreenValues(double numerator, double denominator) {

        float[] colors = new float[2];

        colors[0] = (float) (Math.min(255, 2 * (1 - (numerator / denominator)) * 255)) / 255;
        colors[1] = (float) (255 - Math.max(0, (1 - (2 * (numerator / denominator))) * 255)) / 255;

        return colors;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void renderHud() {
        drawPlayerStats();
    }

    public void drawPlaque() {
        spriteBatch.draw(plaque, (int) (camera.position.x - cameraZoom / 2), (float) (camera.position.y + cameraZoom / 2) - 50);
    }

    public void drawHealthInfo() {
        float[] colors = redGreenValues(health, 100);

        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, "+:", (camera.position.x - 120), (float) (camera.position.y + cameraZoom / 2 - 30));
        bitmapFont.setColor(colors[0], colors[1], 0, 1f);
        bitmapFont.draw(spriteBatch, String.valueOf((int) health), (camera.position.x - 100), (float) (camera.position.y + cameraZoom / 2 - 30));
    }

    public void drawWeaponInfo() {
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, currentWeapon, (camera.position.x - 240), (float) (camera.position.y + cameraZoom / 2) - 10);
    }

    public void drawBatteryInfo() {
        bitmapFont.draw(spriteBatch, "BATT:", (camera.position.x - 240), (float) (camera.position.y + cameraZoom / 2 - 30));
        float[] colors = redGreenValues(flashlightBattery, 100);
        bitmapFont.setColor(colors[0], colors[1], 0, 1f);
        flashlightBatteryString = String.valueOf((int) Math.ceil(flashlightBattery));
        bitmapFont.draw(spriteBatch, flashlightBatteryString + "%", (camera.position.x - 200), (float) (camera.position.y + cameraZoom / 2 - 30));
        if (player.searchPatternIsOn) {
            bitmapFont.setColor(Color.RED);
            bitmapFont.draw(spriteBatch, "<-", (camera.position.x - 160), (float) (camera.position.y + cameraZoom / 2 - 30));
        }
    }

    public void drawStaminaInfo() {
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, "STAMINA:", camera.position.x, (float) (camera.position.y + cameraZoom / 2 - 10));
        float[] colors = redGreenValues(stamina,100);
        bitmapFont.setColor(colors[0],colors[1], 0, 1f);
        bitmapFont.draw(spriteBatch, String.valueOf(Math.floor(stamina)), camera.position.x + 60, (float) (camera.position.y + cameraZoom / 2 - 10));
    }

    public void drawMiscInfo() {
        bitmapFont.setColor(Color.GREEN);
        bitmapFont.draw(spriteBatch, "X:" + ((int) (camera.position.x) / 23), camera.position.x + 180, (float) (camera.position.y + cameraZoom / 2 - 10));
        bitmapFont.draw(spriteBatch, "Y:" + ((int) (camera.position.y) / 32), camera.position.x + 180, (float) (camera.position.y + cameraZoom / 2 - 30));
    }

    public void drawPlayerStats() {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        drawPlaque();
        drawHealthInfo();
        drawWeaponInfo();
        drawBatteryInfo();
        drawStaminaInfo();
        drawMiscInfo();
        drawAmmoInfo();
        drawMagazineInfo();
        spriteBatch.end();
    }

    public void drawAmmoInfo(){

        float[] colors = redGreenValues(ammo,maxAmmo);
        bitmapFont.setColor(colors[0], colors[1], 0, 1f);
        bitmapFont.draw(spriteBatch, String.valueOf((int)ammo), (camera.position.x + 20), (float) (camera.position.y + cameraZoom / 2 - 30));



        if (ammo == 0) {
            bitmapFont.setColor(Color.RED);
            bitmapFont.draw(spriteBatch, "Reload!", (camera.position.x + 40), (float) (camera.position.y + cameraZoom / 2 - 30));
        }
    }

    public void drawMagazineInfo(){
        float[] colors = redGreenValues(magazines,maxMagazines);
        bitmapFont.setColor(colors[0], colors[1], 0, 1f);
        bitmapFont.draw(spriteBatch, (int)magazines + ":", camera.position.x, (float) (camera.position.y + cameraZoom / 2 - 30));
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
