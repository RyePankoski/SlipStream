package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

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
    private boolean canCheckSector = true;

    double checkSectorTimer;
    Door firstDoor;
    //endregion

    public void create() {



        DJ = new SoundPlayer();
        collisionMask = new CollisionMask();
        hud = new Hud();
        handler = new InputHandler();
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(9152, 4800, 2, DJ, handler, hud, collisionMask, monster, this);


        firstDoor = new Door(9090,4750, playcor, collisionMask.getPixmap());
        tram = new Tram(playcor);
        initLightSources();
        darknessLayer = new DarknessLayer(playcor, staticLightSources);
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
        collisionMask.setCamera(playcor.getCamera());
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);
        initScenes();
        stage();
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));
    }

    public void initScenes() {

        Scene levelOne = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience/Ambience.mp3")),
            DJ, playcor, image = new Texture("FloorTex/MainMapDarknessGame.png"));
        Scene levelTwo = new Scene("Second Stage", Gdx.audio.newSound(Gdx.files.internal(("Music/MenuTheme.mp3"))),
            DJ, playcor, image = new Texture("FloorTex/MenuScreen.jpg"));

        sceneManager = new SceneManager();
        sceneManager.addScene(levelOne);
        sceneManager.addScene(levelTwo);
    }

    public void initLightSources() {
        staticLightSources = new ArrayList<>();

        StaticLightSource testLight = new StaticLightSource(200, 7800, .2f, MathFunctions.rayCast(500, 181
            , 90, 7800, 200, collisionMask.getPixmap()));
        StaticLightSource nextLight = new StaticLightSource(5000, 200, .2f, MathFunctions.rayCast(100, 181
            , 90, 200, 5000, collisionMask.getPixmap()));

        StaticLightSource stationLight1 = new StaticLightSource(4930, 3200, .5f, MathFunctions.rayCast(500, 181
            , 90, 3200, 4930, collisionMask.getPixmap()));
        StaticLightSource stationLight2 = new StaticLightSource(4930, 4800, .5f, MathFunctions.rayCast(500, 181
            , 90, 4800, 4930, collisionMask.getPixmap()));
        StaticLightSource stationLight3 = new StaticLightSource(4930, 9000, .5f, MathFunctions.rayCast(500, 181
            , 90, 9000, 4930, collisionMask.getPixmap()));
        StaticLightSource stationLight4 = new StaticLightSource(4930, 12900, .5f, MathFunctions.rayCast(500, 181
            , 90, 12900, 4930, collisionMask.getPixmap()));

        staticLightSources.add(stationLight1);
        staticLightSources.add(stationLight2);
        staticLightSources.add(stationLight3);
        staticLightSources.add(stationLight4);

        staticLightSources.add(testLight);
        staticLightSources.add(nextLight);
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

    public void findSectorOfPlayer() {

        if (System.currentTimeMillis() >= checkSectorTimer) {
            canCheckSector = true;
        }

        if (canCheckSector) {
            System.out.println(playerSector);
            canCheckSector = false;
            checkSectorTimer = System.currentTimeMillis() + 250;

            int[] colorValues = {255, 200, 180, 160, 140, 120, 100, 80, 60, 40};
            sectorColor = (MathFunctions.getPixelColor((int) playcor.getCoorX(), (int) playcor.getCoorY(), sectorMap));
            int red = sectorColor.getRed();
            int green = sectorColor.getGreen();
            int blue = sectorColor.getBlue();

            if (red == 255 && green == 255) {
                playerSector = 100;
                return;
            }

            if (red > 0) {
                for (int i = 0; i < colorValues.length; i++) {
                    if (red == colorValues[i]) {
                        this.playerSector = i;
                        return;
                    }
                }
            }
            if (green > 0) {
                for (int i = 0; i < colorValues.length; i++) {
                    if (green == colorValues[i]) {
                        this.playerSector = i + 10;
                        return;
                    }
                }
            }
            if (blue > 0) {
                for (int i = 0; i < colorValues.length; i++) {
                    if (blue == colorValues[i]) {
                        this.playerSector = i + 20;
                        return;
                    }
                }
            }
        }
    }

    public void chooseAndRender() {

    }


    public void render() {



        findSectorOfPlayer();
        chooseAndRender();
        sceneToRender.renderScene();


        if (playerSector == 100 || tram.moving) {
            tram.updateTram();
        }
        playcor.updatePlayer();
        playcor.checkBullets();

        if (monsterAlive) {
            monster.updateMonster();
        }
        firstDoor.updateDoor();

        darknessLayer.render(0f);
        hud.renderHud();
    }


}
