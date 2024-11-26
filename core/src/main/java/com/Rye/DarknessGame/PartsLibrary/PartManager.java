package com.Rye.DarknessGame.PartsLibrary;


import com.Rye.DarknessGame.Player;

import java.util.Map;

public class PartManager {

    Map<String, Part> parts;
    Player player;

    public PartManager(Player player) {
        this.player = player;
    }
    public void initParts() {
        Part generatorCoupler = new Part("generatorCoupler", player);
    }
    public Part getDoor(String path) {
        return parts.get(path);
    }
}
