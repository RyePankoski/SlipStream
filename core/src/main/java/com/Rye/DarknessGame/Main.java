package com.Rye.DarknessGame;

import com.Rye.DarknessGame.InteractableLibrary.KeyPad;
import com.Rye.DarknessGame.KeyLibrary.Key;
import com.Rye.DarknessGame.PartsLibrary.PartManager;
import com.Rye.DarknessGame.TaskLibrary.TaskManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import java.io.IOException;

@SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
public class Main extends ApplicationAdapter {
    public boolean monsterAlive = true, canRenderSlow = true, renderGame = true, canRenderVeryFast = true, canRenderFast, lightsOn = true;
    int sceneNumber = 0, playerSector;
    private long renderVeryFastTimer, renderFastTimer;
    double RenderSlowTimer, lightsOffTimer, lightsOffWarningTimer;

    //region Objects
    Player playcor;
    Hud hud;
    CollisionMask collisionMask;
    CollisionMask monsterCollisionMask;
    DarknessLayer darknessLayer;
    Monster ronald;
    Pixmap sectorMap;
    Tram tram;
    LOS los;
    SceneManager sceneManager;
    LightingManager lightingManager;
    DoorManager doorManager;
    LightMask lightMask;
    PASystem paSystem;
    TaskManager taskManager;
    Key key1;
    KeyPad keyPad;
    PDA testPDA;
    PartManager partManager;
    AiManager aiManager;
    Draw artist;
    Inventory inventory;

    //endregion

    public void create() {
        //non-dependent objects
        DebugUtility.initialize();

        paSystem = new PASystem();
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));
        collisionMask = new CollisionMask();
        monsterCollisionMask = new CollisionMask();
        lightMask = new LightMask();
        hud = new Hud();
        lightsOffTimer = System.currentTimeMillis() + 30000;
        lightsOffWarningTimer = lightsOffTimer - 10000;

        //dependent objects
        try {
            ronald = new Monster(monsterCollisionMask.getPixmap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playcor = new Player(7800, 200, 2, hud, collisionMask.getPixmap(), ronald, this);

        aiManager = new AiManager(playcor, ronald,this);
        inventory = new Inventory(playcor);

        //test classes to be later removed or implemented;
        partManager = new PartManager(playcor);
        key1 = new Key(8530, 180, 3, 30, playcor);

        doorManager = new DoorManager(sectorMap, collisionMask.getPixmap(), lightMask.getPixmap(), playcor, ronald);

        artist = new Draw(playcor.getCamera());
        testPDA = new PDA(playcor);
        taskManager = new TaskManager(playcor);
        keyPad = new KeyPad(playcor, doorManager.getDoor(3, 7));

        los = new LOS(playcor, lightMask.getPixmap());
        tram = new Tram(playcor);
        lightingManager = new LightingManager(lightMask.getPixmap());
        darknessLayer = new DarknessLayer(playcor, lightingManager.getStaticLightSources(), lightMask.getPixmap());
        sceneManager = new SceneManager(playcor);

        //setters
        hud.setPlayer(playcor);
        ronald.setPlayer(playcor);
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        Gdx.input.setCursorCatched(true);

        //init
        sceneManager.initScenes();
        PopUpManager.init();
        SoundEffects.initSounds();
    }

    public void killMonster(Monster monster) {
        monster = null;
        monsterAlive = false;
        System.gc();
    }

    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) renderGame = !renderGame;

        if (renderGame) {
            if (System.currentTimeMillis() >= RenderSlowTimer) canRenderSlow = true;
            if (System.currentTimeMillis() >= renderVeryFastTimer) canRenderVeryFast = true;
            if (System.currentTimeMillis() >= renderFastTimer) canRenderFast = true;
            if (System.currentTimeMillis() >= lightsOffTimer) lightsOn = false;

            if (canRenderVeryFast) {
                canRenderVeryFast = false;
                renderVeryFastTimer = System.currentTimeMillis() + 8;


                artist.updateAll();

                if (playerSector == 24) {
                    tram.updateTram();
                }

                keyPad.isPlayerNear();
                testPDA.updatePDA();
                key1.updateKey();

                playcor.updatePlayer();
                playcor.checkBullets();

                if (monsterAlive) {
                    ronald.updateMonster();
                }
                if (System.currentTimeMillis() >= lightsOffWarningTimer && System.currentTimeMillis() < lightsOffWarningTimer + 10) {
                    SoundEffects.playMusic("shutdownAlert");
                }
                if (!lightsOn) {
                    darknessLayer.render(0f);
                }
                los.render(0f);
                hud.renderHud();
                inventory.update();
            }

            if (canRenderFast) {
                canRenderFast = false;
                renderFastTimer = System.currentTimeMillis() + 20;
                doorManager.updateDoors(playerSector);

            }
            if (canRenderSlow) {
                canRenderSlow = false;
                RenderSlowTimer = System.currentTimeMillis() + 250;
                playerSector = MathFunctions.findSector((int) playcor.getCoorX(), (int) playcor.getCoorY(), sectorMap);
                paSystem.updatePA();
                aiManager.update();

            }

            UIManager.getInstance().render(Gdx.graphics.getDeltaTime());
        }
    }
}
