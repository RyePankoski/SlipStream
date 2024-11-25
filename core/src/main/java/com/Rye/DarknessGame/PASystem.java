package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.Random;

public class PASystem {
    Music[] voiceLines = new Music[21];
    Random ran;
    double timeTillVoiceLines;
    boolean canPlayVoiceLine = false;
    Music currentVoiceLine;

    public PASystem() {
        timeTillVoiceLines = System.currentTimeMillis() + 40000;
        ran = new Random();
        initSounds();
    }

    public void initSounds() {
        voiceLines[0] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/receivingVoiceLine.mp3"));
        voiceLines[1] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/rerouting.mp3"));
        voiceLines[2] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown.mp3"));
        voiceLines[3] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(2).mp3"));
        voiceLines[4] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(3).mp3"));
        voiceLines[5] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(4).mp3"));
        voiceLines[6] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(5).mp3"));
        voiceLines[7] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(6).mp3"));
        voiceLines[8] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(7).mp3"));
        voiceLines[9] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(8).mp3"));
        voiceLines[10] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(9).mp3"));
        voiceLines[11] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(10).mp3"));
        voiceLines[12] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(11).mp3"));
        voiceLines[13] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(12).mp3"));
        voiceLines[14] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(13).mp3"));
        voiceLines[15] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(14).mp3"));
        voiceLines[16] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(15).mp3"));
        voiceLines[17] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(16).mp3"));
        voiceLines[18] = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/Mixdown(17).mp3"));

    }

    public void updatePA() {
        if (System.currentTimeMillis() >= timeTillVoiceLines) canPlayVoiceLine = true;

        if (canPlayVoiceLine) {
            canPlayVoiceLine = false;
            timeTillVoiceLines = System.currentTimeMillis() + 300000;
            playVoiceLines();
        }
    }

    public void playVoiceLines() {
        int randomVoiceLine = ran.nextInt(0, 19);
        currentVoiceLine = voiceLines[randomVoiceLine];
        if (!currentVoiceLine.isPlaying()) {
            currentVoiceLine.play();
        }
    }
}
