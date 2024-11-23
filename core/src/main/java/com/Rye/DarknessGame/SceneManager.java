package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;

public class SceneManager extends Main {

    Scene sceneToRender;

    Player player;
    SoundPlayer DJ;

ArrayList<Scene> scenes;
    public SceneManager(Player player,SoundPlayer DJ){
        this.player = player;
        this.DJ = DJ;
        scenes = new ArrayList<>();
        initScenes();
    }

    public void initScenes() {
        Scene levelOne = new Scene("First Stage", Gdx.audio.newSound(Gdx.files.internal("Ambience/Ambience.mp3")), DJ, player, image = new Texture("FloorTex/MainMapDarknessGame.png"));
//        Scene levelTwo = new Scene("Second Stage", Gdx.audio.newSound(Gdx.files.internal(("Music/MenuTheme.mp3"))), DJ, player, image = new Texture("FloorTex/MenuScreen.jpg"));
        addScene(levelOne);
//        addScene(levelTwo);
        sceneToRender = getScenes().get(sceneNumber);
    }

    public void addScene(Scene seen){
        scenes.add(seen);
    }
    public ArrayList<Scene> getScenes() {
        return scenes;
    }
}
