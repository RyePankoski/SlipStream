package com.Rye.DarknessGame;
import com.badlogic.gdx.audio.Sound;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SoundManager {

    ArrayList<Sound> sounds;

    public SoundManager(){
        sounds = new ArrayList<>();
    }
    public void addSound(Sound sound){
        sounds.add(sound);
    }
}
