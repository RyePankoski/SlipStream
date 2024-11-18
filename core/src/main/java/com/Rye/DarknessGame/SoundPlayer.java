package com.Rye.DarknessGame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {

    private Map<String, Long> activeSounds = new HashMap<>();

    public SoundPlayer() {
    }
    public void playSound(String key, Sound sound) {
        if (!activeSounds.containsKey(key)) {
            long soundId = sound.loop();
            activeSounds.put(key, soundId);
        }
    }
    public void stopSound(String key, Sound sound) {
        if (activeSounds.containsKey(key)) {
            long soundId = activeSounds.get(key);
            sound.stop(soundId);
            activeSounds.remove(key);
        }
    }
    public void fadeOutSound(String key, Sound sound, float fadeDuration) {
        if (activeSounds.containsKey(key)) {
            long soundId = activeSounds.get(key);

            final float fadeStep = 0.1f;
            final int fadeSteps = (int) (fadeDuration / fadeStep);
            final float initialVolume = 1.0f;

            Timer.schedule(new Task() {
                int stepsRemaining = fadeSteps;
                float currentVolume = initialVolume;

                @Override
                public void run() {
                    currentVolume -= fadeStep;
                    if (currentVolume < 0) currentVolume = 0;

                    sound.setVolume(soundId, currentVolume);

                    if (stepsRemaining <= 0) {
                        sound.stop(soundId);
                        activeSounds.remove(key);
                        this.cancel();
                    }
                    stepsRemaining--;
                }
            }, 0, fadeStep);
        }
    }
    public void stopAllSounds(Sound sound) {
        for (Map.Entry<String, Long> entry : activeSounds.entrySet()) {
            sound.stop(entry.getValue());
        }
        activeSounds.clear();
    }
}
