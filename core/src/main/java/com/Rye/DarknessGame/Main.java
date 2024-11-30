package com.Rye.DarknessGame;

import com.Rye.DarknessGame.InteractableLibrary.KeyPad;
import com.Rye.DarknessGame.KeyLibrary.Key;
import com.Rye.DarknessGame.PartsLibrary.PartManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;


import java.io.IOException;

@SuppressWarnings({"ParameterCanBeLocal", "UnusedAssignment"})
public class Main extends ApplicationAdapter {

    //region Variables
// boolean variables
    public boolean monsterAlive = true, canRenderSlow = true, renderGame = true, canRenderVeryFast = true, canRenderFast, lightsOn = true;

    // int variables
    int sceneNumber = 0, playerSector;

    // long variables
    private long renderVeryFastTimer, renderFastTimer;

    // double variables
    double RenderSlowTimer, lightsOffTimer, lightsOffWarningTimer;

    // Object variables
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



    //endregion
    public void create() {

//        batch = new SpriteBatch();
//
//        String vertexShaderCode = Gdx.files.internal("Shaders/vertex.glsl").readString();
//
//        String fragmentShaderCode = Gdx.files.internal("Shaders/fragment.glsl").readString();
//
//        shader  = new ShaderProgram(vertexShaderCode, fragmentShaderCode);
//
//        if (!shader.isCompiled()) {
//            throw new GdxRuntimeException("Shader compilation failed: " + shader.getLog());
//        }
//
//        // Use a FrameBuffer for post-processing
//        frameBuffer= new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);



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



            //only for drawn elements!
            if (canRenderVeryFast) {
                canRenderVeryFast = false;
                renderVeryFastTimer = System.currentTimeMillis() + 8;



                if (playerSector == 24) {
                    tram.updateTram();
                }

                artist.updateAll();

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
//            frameBuffer.begin();
//            frameBuffer.end();
//
//            batch.setShader(shader);
//            shader.bind();
//            shader.setUniformf("u_time", System.currentTimeMillis() / 1000f);
//            shader.setUniformf("u_intensity",50); // Set based on player health
//
//            batch.begin();
//            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, 1800, 1350);
//            batch.end();
//            batch.setShader(null);
        }
    }
}
