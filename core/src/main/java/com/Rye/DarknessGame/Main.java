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

public class Main extends ApplicationAdapter {

    //region Variables
    public Player playcor;
    InputHandler handler;
    Texture image;
    int sceneNumber = 0;
    Hud hud;
    CollisionMask collisionMask;
    DarknessLayer darknessLayer;
    Monster monster;
    boolean monsterAlive = true;
    SoundPlayer DJ;
    Pixmap sectorMap;
    Tram tram;
    int playerSector;
    private boolean canRenderFast = true;
    double RenderFastTimer;
    LOS los;
    private boolean renderGame = true;
    private boolean canRenderVeryFast = true;
    private long renderVeryFastTimer;
    SceneManager sceneManager;
    LightingManager lightingManager;
    DoorManager doorManager;

    //endregion
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
        sceneManager = new SceneManager(playcor, DJ);

        //setters
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        collisionMask.setCamera(playcor.getCamera());
        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);
        Gdx.input.setCursorCatched(true);

        //init
        sceneManager.initScenes();
    }
    public void killMonster(Monster monster) {
        monster = null;
        monsterAlive = false;
        System.gc();
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

                sceneManager.getScenes().get(sceneNumber).renderScene();
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
                playerSector = MathFunctions.findSector((int) playcor.getCoorX(), (int) playcor.getCoorY(), sectorMap);
                doorManager.updateDoors(playerSector);
            }
        }
    }
}
