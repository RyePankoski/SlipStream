package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.Random;


public class PASystem {

    Music[] voiceLines = new Music[1];
    Random ran = new Random();

    double timeTillVoiceLines;

    boolean canPlayVoiceLine = false;

    Music receivingVoiceLine;


    public PASystem(){
        timeTillVoiceLines = System.currentTimeMillis() + 40000;
        receivingVoiceLine = Gdx.audio.newMusic(Gdx.files.internal("paVoiceLines/receivingVoiceLine.mp3"));
        voiceLines[0] = receivingVoiceLine;
    }

    public void updatePA(){
        if(System.currentTimeMillis() >= timeTillVoiceLines) canPlayVoiceLine = true;
        if (canPlayVoiceLine){
           canPlayVoiceLine = false;
           timeTillVoiceLines = System.currentTimeMillis() + 300000;
           playVoiceLines();
        }
    }
    public void playVoiceLines(){
        receivingVoiceLine.play();
    }
}
