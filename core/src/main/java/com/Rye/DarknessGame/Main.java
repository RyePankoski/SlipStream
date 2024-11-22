package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    Color sectorColor;
    int playerSector;
    private boolean canRenderFast = true;
    double RenderTimerFast;
    Door door;
    Door[][] doors;
    LOS los;
    private boolean renderGame = true;

    public void create() {
        DJ = new SoundPlayer();
        collisionMask = new CollisionMask();
        hud = new Hud();
        handler = new InputHandler();
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(9152, 4800, 2, DJ, handler, hud, collisionMask, monster, this);

        los = new LOS(playcor);
        tram = new Tram(playcor);
        initLightSources();
        darknessLayer = new DarknessLayer(playcor, staticLightSources);
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
        collisionMask.setCamera(playcor.getCamera());
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);
        Gdx.input.setCursorCatched(true);

        initScenes();
        stage();
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));

        //if you ever get an out-of-bounds error related to doors, its probably this.
        doors = new Door[25][50];
        loadMapAndInstantiateDoors("CollisionMap/objectMap.tmx");

    }

    public void initScenes() {

        Scene levelOne = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience/Ambience.mp3")), DJ, playcor, image = new Texture("FloorTex/MainMapDarknessGame.png"));
        Scene levelTwo = new Scene("Second Stage", Gdx.audio.newSound(Gdx.files.internal(("Music/MenuTheme.mp3"))), DJ, playcor, image = new Texture("FloorTex/MenuScreen.jpg"));

        sceneManager = new SceneManager();
        sceneManager.addScene(levelOne);
        sceneManager.addScene(levelTwo);
    }

    public void initLightSources() {
        staticLightSources = new ArrayList<>();
        StaticLightSource stationLight1 = new StaticLightSource(4930, 3200, .5f, MathFunctions.rayCast(200, 181, 90, 3200, 4930, collisionMask.getPixmap()));
        StaticLightSource stationLight2 = new StaticLightSource(4930, 4800, .5f, MathFunctions.rayCast(200, 181, 90, 4800, 4930, collisionMask.getPixmap()));
        StaticLightSource stationLight3 = new StaticLightSource(5050, 9150, .5f, MathFunctions.rayCast(250, 181, 90, 9150, 5050, collisionMask.getPixmap()));
        StaticLightSource stationLight4 = new StaticLightSource(4930, 12900, .5f, MathFunctions.rayCast(200, 181, 90, 12900, 4930, collisionMask.getPixmap()));

        staticLightSources.add(stationLight1);
        staticLightSources.add(stationLight2);
        staticLightSources.add(stationLight3);
        staticLightSources.add(stationLight4);
    }

    public void stage() {
        sceneToRender = sceneManager.getScenes().get(sceneNumber);
    }

    public float secondsToNano(float seconds) {
        return (seconds * 1000000000);
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

    public void updateDoors(int playerSector) {
        for (int i = 0; i < 500; i++) {
            if (doors[playerSector][i] != null) {
                doors[playerSector][i].updateDoor();
            } else {
                break;
            }
        }
    }

    public void loadMapAndInstantiateDoors(String mapPath) {

        int[] numberDoorsInSector = new int[25];
        int doorNum = 0;
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load(mapPath);
        MapLayer objectLayer = map.getLayers().get("doorLayer");

        if (objectLayer == null) {
            System.out.println("No 'Objects' layer found in the map.");
            return;
        }

        MapObjects objects = objectLayer.getObjects();
        for (MapObject object : objects) {

            String objectClass = object.getProperties().get("type", String.class);
            if ("Door".equals(objectClass)) {
                doorNum++;
                // Retrieve position and size properties
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                float width = object.getProperties().get("width", Float.class);
                float height = object.getProperties().get("height", Float.class);

                door = new Door((int) x, (int) y, (int) width, (int) height, findSector((int) x, (int) y), doorNum, playcor, collisionMask.getPixmap());

                doors[door.getSector()][numberDoorsInSector[door.getSector()]] = door;
                numberDoorsInSector[door.getSector()]++;
            }
        }
    }

    public void render() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            renderGame = !renderGame;
        }

        if (renderGame) {
            if (System.currentTimeMillis() >= RenderTimerFast) canRenderFast = true;

            if (canRenderFast) {
                canRenderFast = false;
                RenderTimerFast = System.currentTimeMillis() + 250;
                playerSector = findSector((int) playcor.getCoorX(), (int) playcor.getCoorY());
                updateDoors(playerSector);
            }

            sceneToRender.renderScene();

            if (playerSector == 24 || tram.moving) {
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
    }
}
