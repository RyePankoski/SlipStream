package com.Rye.DarknessGame;
import com.Rye.DarknessGame.InteractableLibrary.KeyPad;
import com.Rye.DarknessGame.KeyLibrary.Key;
import com.Rye.DarknessGame.PartsLibrary.PartManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

@SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
public class Main extends ApplicationAdapter {

    //region Variables
    public Player playcor;
    Texture image;
    int sceneNumber = 0;
    Hud hud;
    CollisionMask collisionMask;
    DarknessLayer darknessLayer;
    Monster monster;
    boolean monsterAlive = true;
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
    LightMask lightMask;
    PASystem paSystem;
    TaskManager taskManager;
    Key key1;
    public KeyPad keyPad;

    PDA testPDA;
    PartManager partManager;

    //endregion
    public void create() {
        //non-dependent objects


        paSystem = new PASystem();
        sectorMap = new Pixmap(Gdx.files.internal("CollisionMap/sectorMap.png"));
        collisionMask = new CollisionMask();
        lightMask = new LightMask();
        hud = new Hud();
        //temp stuff
        lightsOffTimer = System.currentTimeMillis() + 70000;
        lightsOffWarningTimer = lightsOffTimer - 10000;

        //dependent objects
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(7800, 200, 2, hud, collisionMask.getPixmap(), monster, this);


        //test classes to be later removed or implemented;
        partManager = new PartManager(playcor);
        key1 = new Key(8530, 180, 3, 30, playcor);

        testPDA = new PDA(playcor);

        taskManager = new TaskManager(playcor);
        doorManager = new DoorManager(sectorMap, collisionMask.getPixmap(), lightMask.getPixmap(), playcor);

        keyPad = new KeyPad(playcor, doorManager.getDoor(3, 7));

        los = new LOS(playcor, lightMask.getPixmap());
        tram = new Tram(playcor);
        lightingManager = new LightingManager(lightMask.getPixmap());
        darknessLayer = new DarknessLayer(playcor, lightingManager.getStaticLightSources(), lightMask.getPixmap());
        sceneManager = new SceneManager(playcor, DJ);

        //setters
        hud.setPlayer(playcor);
        monster.setPlayer(playcor);
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
                    SoundEffects.playMusic("shutdownAlert");
                }
                if (!lightsOn) {
                    darknessLayer.render(0f);
                }
                los.render(0f);
                hud.renderHud();
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
            }

            keyPad.isPlayerNear();
            testPDA.updatePDA();
            UIManager.getInstance().render(Gdx.graphics.getDeltaTime());
        }
    }
}
