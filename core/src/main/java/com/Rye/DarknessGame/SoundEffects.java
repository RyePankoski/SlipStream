package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;
import java.util.Map;

public class SoundEffects {
    private static Map<String, Music> sounds;

    static boolean playingSound;

    public SoundEffects() {}

    public static void initSounds() {
        sounds = new HashMap<>();
        sounds.put("subObjectiveComplete", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/subObjectiveComplete.mp3")));
        sounds.put("unlockDoor", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/unlockDoor.mp3")));
    }

    public static void playSound(String path) {
        Music music = sounds.get(path);
        if(!music.isPlaying()) {
            music.play();
        }
    }
    public static boolean isPlaying(){
        return playingSound;
    }
}
