package com.Rye.DarknessGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

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
    //endregion

    public void create() {
        SoundPlayer DJ = new SoundPlayer();

        collisionMask = new CollisionMask();

        hud = new Hud();
        handler = new InputHandler();
        monster = new Monster(collisionMask.getPixmap());
        playcor = new Player(1000, 1000, 8, DJ, handler, hud, collisionMask, monster, this);

        monster.setPlayer(playcor);

        collisionMask.setCamera(playcor.getCamera());
        hud.setCamera(playcor.getCamera(), playcor.cameraZoom, playcor.getBattery());

        com.badlogic.gdx.Gdx.input.setInputProcessor(handler);
        RoomManager menu = new RoomManager();
        menu.addRoom(new Room("Menu", 1920, 1080, image = new Texture("MenuScreen.jpg")));

        RoomManager firstLevel = new RoomManager();
        firstLevel.addRoom(new Room("Start", 5000, 5000, image = new Texture("mainLevel.png")));
        firstLevel.addRoom(new Room("Second Room", 5000, 5000, image = new Texture("basicFloor3.jpg")));

        Scene menuScene = new Scene("Menu", Gdx.audio.newSound(Gdx.files.internal("MenuTheme.mp3")), menu, DJ, playcor);
        Scene scene1 = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience.mp3")), firstLevel, DJ, playcor);
        sceneManager = new SceneManager();
        sceneManager.addScene(menuScene);
        sceneManager.addScene(scene1);

        darknessLayer = new DarknessLayer(playcor);
        stage();
    }

    public void stage() {
        sceneToRender = sceneManager.getScenes().get(sceneNumber);
    }

    public float secondsToNano(float seconds){
        return (seconds * 1000000000);
    }

    public void killMonster(Monster monster){
        monster = null;
        monsterAlive = false;
        System.gc();
    }

    public void render() {

        sceneToRender.renderScene();
        playcor.updatePlayer();
        playcor.checkBullets();

        if(monsterAlive) {
            monster.updateMonster();
        }

        darknessLayer.render(0f);
        hud.renderHud();
    }
}
