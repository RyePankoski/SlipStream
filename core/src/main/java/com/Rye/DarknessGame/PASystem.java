package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import java.io.FileFilter;
import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;

public class PASystem {
    Random ran;
    double timeTillVoiceLines;
    boolean canPlayVoiceLine = false;
    Music currentVoiceLine;

    Map<String, Music> voiceLines;

    public PASystem() {
        voiceLines = new HashMap<>();
        timeTillVoiceLines = System.currentTimeMillis() + 60000;
        ran = new Random();
        loadAudioFromDirectory("assets/paVoiceLines");
    }

    public void loadAudioFromDirectory(String directoryPath) {
        FileHandle dirHandle = Gdx.files.internal(directoryPath);

        // Debug: Print the absolute path to verify the directory
        System.out.println("Searching in directory: " + dirHandle.path());

        // List ALL files to see what's actually there
        FileHandle[] allFiles = dirHandle.list();
        System.out.println("Total files found: " + allFiles.length);

        for (FileHandle file : allFiles) {
            // Print each file to debug
            System.out.println("Found file: " + file.name());
        }

        FileHandle[] listOfFiles = dirHandle.list((FileFilter) file ->
            file.getName().toLowerCase().endsWith(".mp3") ||
                file.getName().toLowerCase().endsWith(".wav"));

        System.out.println("Audio files found: " + listOfFiles.length);

        for (FileHandle file : listOfFiles) {
            String fileName = file.name();
            String key = fileName.substring(0, fileName.lastIndexOf('.'));

            Music music = Gdx.audio.newMusic(file);
            voiceLines.put(key, music);
        }
    }

    public Music getRandomSound() {
        List<String> keys = new ArrayList<>(voiceLines.keySet());

        if (keys.isEmpty()) {
            return null;
        }

        // Generate a random index to pick a key
        int randomIndex = random.nextInt(keys.size());
        String randomKey = keys.get(randomIndex);
        return voiceLines.get(randomKey);
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
        currentVoiceLine = getRandomSound();
        if (!currentVoiceLine.isPlaying()) {
            currentVoiceLine.setVolume(1f);
            currentVoiceLine.play();
        }
    }
}
