package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.awt.*;
import java.util.ArrayList;

public class Main extends ApplicationAdapter implements Screen {

    //region Variables
    Player playcor;
    InputHandler handler;
    Texture image;
    SceneManager sceneManager;
    int sceneNumber = 0;
    Scene sceneToRender;
    Hud hud;
    CollisionMask collisionMask;
    DarknessLayer darknessLayer;
    Monster monster;
    boolean monsterAlive = true;
    SoundPlayer DJ;
    ArrayList<StaticLightSource> staticLightSources;

    Pixmap sectorMap;

    Tram tram;

    int playerSector;
    private boolean canRenderFast = true;
    double RenderFastTimer;

    LOS los;
    private boolean renderGame = true;
    private boolean canRenderVeryFast = true;
    private long renderVeryFastTimer;
    Color sectorColor;

    LightingManager lightingManager;

    DoorManager doorManager;

    public void create() {
        //non-dependent objects
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));
        DJ = new SoundPlayer();
        collisionMask = new CollisionMask();
        hud = new Hud();
        handler = new InputHandler();

        //dependent objects
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(9152, 4800, 2, DJ, handler, hud, collisionMask.getPixmap(), monster, this);
        doorManager = new DoorManager(sectorMap, collisionMask.getPixmap(), playcor);
        los = new LOS(playcor);
        tram = new Tram(playcor);
        lightingManager = new LightingManager(collisionMask.getPixmap());
        darknessLayer = new DarknessLayer(playcor, lightingManager.getStaticLightSources());

        //setters
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        collisionMask.setCamera(playcor.getCamera());
        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);
        Gdx.input.setCursorCatched(true);

        //inits
        initScenes();
        prepareScenes();
    }

    public void initScenes() {
        Scene levelOne = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience/Ambience.mp3")), DJ, playcor, image = new Texture("FloorTex/MainMapDarknessGame.png"));
        Scene levelTwo = new Scene("Second Stage", Gdx.audio.newSound(Gdx.files.internal(("Music/MenuTheme.mp3"))), DJ, playcor, image = new Texture("FloorTex/MenuScreen.jpg"));
        sceneManager = new SceneManager();
        sceneManager.addScene(levelOne);
        sceneManager.addScene(levelTwo);
    }

    public void prepareScenes() {
        sceneToRender = sceneManager.getScenes().get(sceneNumber);
    }

    public void killMonster(Monster monster) {
        monster = null;
        monsterAlive = false;
        System.gc();
    }

    public int findSector(int x, int y) {

        int sector = 0;

        int[] colorValues = {255, 200, 180, 160, 140, 120, 100, 80, 60, 40};
        sectorColor = (MathFunctions.getPixelColor(x, y, sectorMap));
        int red = sectorColor.getRed();
        int green = sectorColor.getGreen();
        int blue = sectorColor.getBlue();


        if (red > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (red == colorValues[i]) {
                    sector = i;
                }
            }
        }
        if (green > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (green == colorValues[i]) {
                    sector = i + 10;
                }
            }
        }
        if (blue > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (blue == colorValues[i]) {
                    sector = i + 20;
                }
            }
        }

        if (red == 255 && green == 255) {
            sector = 24;
        }
        return sector;
    }


    public void render() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) renderGame = !renderGame;

        if (renderGame) {
            if (System.currentTimeMillis() >= RenderFastTimer) canRenderFast = true;
            if (System.currentTimeMillis() >= renderVeryFastTimer) canRenderVeryFast = true;

            //only for drawn elements!
            if (canRenderVeryFast) {
                canRenderVeryFast = false;
                renderVeryFastTimer = System.currentTimeMillis() + 8;

                sceneToRender.renderScene();
                if (playerSector == 24) {
                    tram.updateTram();
                }
                playcor.updatePlayer();
                playcor.checkBullets();

                if (monsterAlive) {
                    monster.updateMonster();
                }

                darknessLayer.render(0f);
                los.render(0f);
                hud.renderHud();
            }

            //use this mostly for updates.
            if (canRenderFast) {
                canRenderFast = false;
                RenderFastTimer = System.currentTimeMillis() + 250;
                playerSector = findSector((int) playcor.getCoorX(), (int) playcor.getCoorY());
                doorManager.updateDoors(playerSector);
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void hide() {

    }
}
