package com.Rye.DarknessGame;

import java.util.ArrayList;

public class SceneManager {


ArrayList<Scene> scenes;
    public SceneManager(){
        scenes = new ArrayList<>();
    }
    public void addScene(Scene seen){
        scenes.add(seen);
    }
    public ArrayList<Scene> getScenes() {
        return scenes;
    }
    public void setScenes(ArrayList<Scene> scenes) {
        this.scenes = scenes;
    }
}
