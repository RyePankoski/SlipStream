package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;

public class SceneManager extends Main {
    Scene sceneToRender;
    Player player;


ArrayList<Scene> scenes;
    public SceneManager(Player player){
        this.player = player;
        scenes = new ArrayList<>();
        initScenes();
    }

    public void initScenes() {
        Scene levelOne = new Scene("First Stage", player, new Texture("FloorTex/MainMapDarknessGame.png"));
        addScene(levelOne);
        sceneToRender = getScenes().get(sceneNumber);
    }

    public void addScene(Scene seen){
        scenes.add(seen);
    }
    public ArrayList<Scene> getScenes() {
        return scenes;
    }
}
