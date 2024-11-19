package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class Main extends ApplicationAdapter {

    //region Variables
    Player playcor;
    InputHandler handler;
    Texture image;
    SceneManager sceneManager;
    int sceneNumber = 1;
    Scene sceneToRender;
    Hud hud;
    CollisionMask collisionMask;
    DarknessLayer darknessLayer;
    Monster monster;

    boolean monsterAlive = true;
    SoundPlayer DJ;

    ArrayList<StaticLightSource> StaticlightSources;
    //endregion

    public void create() {
        DJ = new SoundPlayer();
        collisionMask = new CollisionMask();
        hud = new Hud();
        handler = new InputHandler();
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(100,5000, 8, DJ, handler, hud, collisionMask, monster, this);

        initLightSources();
        darknessLayer = new DarknessLayer(playcor, StaticlightSources);

        monster.setPlayer(playcor);
        collisionMask.setCamera(playcor.getCamera());

        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());
        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);

        initScenes();
        stage();
    }

    public void initScenes() {
        RoomManager menu = new RoomManager();
        menu.addRoom(new Room("Menu", 1920, 1080, image = new Texture("CollisionMap/collisionMap.png")));

        RoomManager firstLevel = new RoomManager();
        firstLevel.addRoom(new Room("Start", 5000, 5000, image = new Texture("CollisionMap/collisionMap.png")));


        Scene menuScene = new Scene("Menu", Gdx.audio.newSound(Gdx.files.internal("Music/MenuTheme.mp3")), menu, DJ, playcor);
        Scene scene1 = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience/Ambience.mp3")), firstLevel, DJ, playcor);
        sceneManager = new SceneManager();
        sceneManager.addScene(menuScene);
        sceneManager.addScene(scene1);
    }

    public void initLightSources(){
        StaticLightSource testLight = new StaticLightSource(5000,1000, .2f,MathFunctions.rayCast(1000,181
            ,90,1000,5000, collisionMask.getPixmap()));
        StaticLightSource nextLight = new StaticLightSource(5000,2000, .2f,MathFunctions.rayCast(1000,181
            ,90,2000,5000, collisionMask.getPixmap()));
        StaticlightSources = new ArrayList<>();
        StaticlightSources.add(testLight);
        StaticlightSources.add(nextLight);
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

    public void render() {


        System.out.println("(main) FPS:"+Gdx.graphics.getFramesPerSecond());
        sceneToRender.renderScene();
        playcor.updatePlayer();
        playcor.checkBullets();


        if (monsterAlive) {
            monster.updateMonster();
        }

        darknessLayer.render(0f);
        hud.renderHud();
    }
}
