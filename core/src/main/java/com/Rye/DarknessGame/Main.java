package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

@SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
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
    private boolean canRenderSlow = true;
    double RenderSlowTimer;
    LOS los;
    private boolean renderGame = true;
    private boolean canRenderVeryFast = true;
    private long renderVeryFastTimer;
    SceneManager sceneManager;
    LightingManager lightingManager;
    DoorManager doorManager;
    private boolean canRenderFast;
    private long renderFastTimer;
    boolean lightsOn = true;
    double lightsOffTimer;
    double lightsOffWarningTimer;
    Sound warningSound;
    LightMask lightMask;
    PASystem paSystem;
    TaskManager taskManager;

    Key key1;

    //endregion
    public void create() {
        //non-dependent objects
        SoundEffects.initSounds();
        paSystem = new PASystem();
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));
        warningSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/shutDownAlert.mp3"));
        DJ = new SoundPlayer();
        collisionMask = new CollisionMask();
        lightMask = new LightMask();
        hud = new Hud();
        handler = new InputHandler();

        //temp stuff
        lightsOffTimer = System.currentTimeMillis() + 70000;
        lightsOffWarningTimer = lightsOffTimer - 10000;

        //dependent objects
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(7800, 200, 2, DJ, handler, hud, collisionMask.getPixmap(), monster, this);

        key1 = new Key(7800,300,7,0,playcor);

        taskManager = new TaskManager(playcor);
        doorManager = new DoorManager(sectorMap, collisionMask.getPixmap(), lightMask.getPixmap(), playcor);
        los = new LOS(playcor, lightMask.getPixmap());
        tram = new Tram(playcor);
        lightingManager = new LightingManager(lightMask.getPixmap());
        darknessLayer = new DarknessLayer(playcor, lightingManager.getStaticLightSources(), lightMask.getPixmap());
        sceneManager = new SceneManager(playcor, DJ);

        //setters
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
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
            if (System.currentTimeMillis() >= RenderSlowTimer) canRenderSlow = true;
            if (System.currentTimeMillis() >= renderVeryFastTimer) canRenderVeryFast = true;
            if (System.currentTimeMillis() >= renderFastTimer) canRenderFast = true;
            if (System.currentTimeMillis() >= lightsOffTimer) lightsOn = false;


            if (canRenderFast) {
                canRenderFast = false;
                renderFastTimer = System.currentTimeMillis() + 20;
                doorManager.updateDoors(playerSector);
            }

            //only for drawn elements!
            if (canRenderVeryFast) {
                canRenderVeryFast = false;
                renderVeryFastTimer = System.currentTimeMillis() + 8;

                sceneManager.getScenes().get(sceneNumber).renderScene();
                key1.updateKey();

                if (playerSector == 24) {
                    tram.updateTram();
                }
                playcor.updatePlayer();
                playcor.checkBullets();

                if (monsterAlive) {
                    monster.updateMonster();
                }

                if (System.currentTimeMillis() >= lightsOffWarningTimer && System.currentTimeMillis() < lightsOffWarningTimer + 10) {
                    warningSound.play();
                }

                if (!lightsOn) {
                    darknessLayer.render(0f);
                }

                los.render(0f);
                hud.renderHud();
            }
            if (canRenderSlow) {
                canRenderSlow = false;
                RenderSlowTimer = System.currentTimeMillis() + 250;
                playerSector = MathFunctions.findSector((int) playcor.getCoorX(), (int) playcor.getCoorY(), sectorMap);
                paSystem.updatePA();
            }
        }
    }
}
