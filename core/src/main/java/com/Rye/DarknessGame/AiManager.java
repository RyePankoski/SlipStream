package com.Rye.DarknessGame;

import java.util.Random;

public class AiManager {
    private final Player player;
    private final Monster monster;
    private final Random random;

    // Use constants for magic numbers
    private static final int MAX_MENACE_LEVEL = 100;
    private static final int HUNT_CHANCE_INTERVAL = 60000; // 60 seconds
    private static final int MAX_HUNT_CHANCE_RANGE = 91;
    private static final int DISTANCE_THRESHOLD = 1000;

    // Encapsulate these fields and use more descriptive names
    private double menaceLevel = 0;
    private boolean isHunting = false;
    private boolean canAttemptHunt = true;
    private long nextHuntAttemptTime;
    private int huntFailedAttempts = 0;
    Main main;

    double justHuntedTimer;

    boolean justHunted;

    public AiManager(Player player, Monster monster, Main main) {
        // Validate input parameters
        if (player == null || monster == null) {
            throw new IllegalArgumentException("Player and Monster cannot be null");
        }

        this.player = player;
        this.monster = monster;
        this.random = new Random();
        this.main = main;
    }

    public void update() {

        if (System.currentTimeMillis() >= nextHuntAttemptTime) canAttemptHunt = true;
        if (System.currentTimeMillis() >= justHuntedTimer) justHunted = false;

        DebugUtility.updateVariable("Chance of Hunt: ", String.valueOf(calculateHuntChance()));
        DebugUtility.updateVariable("Menace Level: ", String.valueOf(menaceLevel));
        DebugUtility.updateVariable("Hunting? : ", String.valueOf(isHunting));
        DebugUtility.updateVariable("Distance to Monster", String.valueOf(player.monsterDistance));


        if(player.monsterDistance > 1000){
            monster.updateAttackStatus(true);
        }

        if (menaceLevel >= MAX_MENACE_LEVEL) {
            justHuntedTimer = System.currentTimeMillis() + 50000;
            nextHuntAttemptTime = System.currentTimeMillis() + 30000;

            monster.updateAttackStatus(false);
            monster.updateHuntStatus(false);

            menaceLevel = 0;

            justHunted = true;
            canAttemptHunt = false;
            isHunting = false;
        }

        if (!isHunting && canAttemptHunt) {
            canAttemptHunt = false;
            nextHuntAttemptTime = System.currentTimeMillis() + HUNT_CHANCE_INTERVAL;

            int currentHuntChance = calculateHuntChance();
            int chanceCheck = random.nextInt(0, MAX_HUNT_CHANCE_RANGE + 1);

            if (chanceCheck <= currentHuntChance) {
                huntFailedAttempts = 0;
                monster.updateHuntStatus(true);
                monster.updateAttackStatus(true);
                isHunting = true;
            } else {
                huntFailedAttempts++;
            }
        }
        monsterFear();
        updateMenaceLevel();
    }

    public void monsterFear(){

        if(monster.angry && monster.health < 20){
            menaceLevel = 110;
        }

    }
    private int calculateHuntChance() {
        int huntChance = 0;

        // Increase hunt chance based on failed attempts
        huntChance += huntFailedAttempts * 10;

        // Base chance when no menace
        if (menaceLevel == 0) {
            huntChance += 20;
        }

        // Adjust hunt chance based on player's state
        huntChance += player.flashLightIsOn ? -10 : 30;
        huntChance += player.searchPatternIsOn ? -10 : 10;

        if (main.lightsOn) {
            huntChance = 0;
        }

        if (justHunted) {
            huntChance -= 30;
            return huntChance;
        } else {
            return Math.max(huntChance, 0);
        }
    }

    private void updateMenaceLevel() {
        if (player.monsterDistance > DISTANCE_THRESHOLD && menaceLevel > 0) {
            menaceLevel = Math.max(0, menaceLevel - 1);
        } else if (player.monsterDistance < DISTANCE_THRESHOLD && menaceLevel < MAX_MENACE_LEVEL) {
            menaceLevel = Math.min(MAX_MENACE_LEVEL, menaceLevel + 1);
        }
    }
}
