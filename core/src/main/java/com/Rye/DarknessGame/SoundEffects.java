package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundEffects {
    private static Map<String, Music> musics;
    private static Map<String, Sound> sounds;


    static boolean playingSound;

    public SoundEffects() {
    }

    public static void initSounds() {
        musics = new HashMap<>();
        sounds = new HashMap<>();
        musics.put("subObjectiveComplete", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/subObjectiveComplete.mp3")));
        musics.put("unlockDoor", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/unlockDoor.mp3")));
        musics.put("openDoor", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/openDoor.mp3")));
        musics.put("doorClose", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/doorClose.mp3")));
        musics.put("lockedDoorSound", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/lockedDoorSound.mp3")));
        musics.put("flashLightWarning", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/flashlightWarning.mp3")));
        musics.put("playerDamagedSound", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/playerDamaged.mp3")));
        musics.put("playerDamagedGrunt", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/playerDamagedGrunt.mp3")));
        musics.put("searchPatternSound", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/searchStatic.mp3")));
        musics.put("turnOnSearch", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/turnOnSearch.mp3")));
        musics.put("running", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/Running.mp3")));
        musics.put("walking", Gdx.audio.newMusic(Gdx.files.internal("PlayerSFX/Walking.mp3")));
        sounds.put("bulletStrike", Gdx.audio.newSound(Gdx.files.internal("BulletSFX/bulletStrike.mp3")));
        musics.put("eerieMusic", Gdx.audio.newMusic(Gdx.files.internal("MonsterSFX/eerieMusic.mp3")));
        musics.put("tramMoving", Gdx.audio.newMusic(Gdx.files.internal("MonsterSFX/tramMoving.mp3")));
        musics.put("shutdownAlert",Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/shutDownAlert.mp3")));
        musics.put("pdaFound", Gdx.audio.newMusic(Gdx.files.internal("SoundEffects/pdaFound.mp3")));

        sounds.put("monsterStrikeSound", Gdx.audio.newSound(Gdx.files.internal("BulletSFX/monsterStrikeSound.mp3")));
        sounds.put("changeGun", Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/changeGuns.mp3")));
        sounds.put("meleeHit", Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/meleeSound.mp3")));
        sounds.put("meleeMiss", Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/meleeMiss.mp3")));
        sounds.put("flashLightSound", Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/flashLightSound.mp3")));
        sounds.put("searchBeep", Gdx.audio.newSound(Gdx.files.internal("PlayerSFX/proxBeep.mp3")));
        sounds.put("escapeSound", Gdx.audio.newSound(Gdx.files.internal("MonsterSFX/monsterEscape.mp3")));
        sounds.put("accessDenied", Gdx.audio.newSound(Gdx.files.internal("SoundEffects/deniedComputer.mp3")));
        sounds.put("accessGranted", Gdx.audio.newSound(Gdx.files.internal("SoundEffects/successComputer.mp3")));



    }



    public static void playMusic(String path) {
        Music music = musics.get(path);
        if (!music.isPlaying()) {
            music.play();
        }
    }
    public static void playSound(String path){
        Sound sound = sounds.get(path);
        sound.play();
    }

    public static void stopMusic(String path){
        Music music = musics.get(path);
        music.stop();
    }
    public static void stopSound(String path){
        Sound sound = sounds.get(path);
        sound.stop();
    }

    public static void playSoundWithParameters(String path, float volume, float pitch){
        Sound sound = sounds.get(path);
        sound.play(volume);
    }
    public static void playMusicWithParameters(String path, float volume, float pitch){
        Music music = musics.get(path);
        music.setVolume(volume);
        music.play();
    }

    public static boolean isPlaying() {
        return playingSound;
    }
}
