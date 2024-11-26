package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoorManager {
    Pixmap collisionMap;
    Pixmap sectorMap;
    Color sectorColor;
    Door door;
    Player player;
    Pixmap lightMap;
    Map<String, ArrayList<Door>> sectorsHashMap;
    ArrayList<Door> sectorDoorsArray;

    public DoorManager(Pixmap sectorMap, Pixmap collisionMap, Pixmap lightMap, Player player) {
        this.sectorMap = sectorMap;
        this.lightMap = lightMap;
        this.collisionMap = collisionMap;
        this.player = player;

        sectorsHashMap = new HashMap<>();
        loadMapAndInstantiateDoors("CollisionMap/objectMap.tmx");
    }

    public int findSector(int x, int y) {

        int sector = 0;

        int[] colorValues = {255, 200, 180, 160, 140, 120, 100, 80, 60, 40};
        sectorColor = (MathFunctions.getPixelColor(x, y, sectorMap));
        int red = sectorColor.getRed();
        int green = sectorColor.getGreen();
        int blue = sectorColor.getBlue();


        if (red > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (red == colorValues[i]) {
                    sector = i;
                }
            }
        }
        if (green > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (green == colorValues[i]) {
                    sector = i + 10;
                }
            }
        }
        if (blue > 0) {
            for (int i = 0; i < colorValues.length; i++) {
                if (blue == colorValues[i]) {
                    sector = i + 20;
                }
            }
        }

        if (red == 255 && green == 255) {
            sector = 24;
        }
        return sector;
    }

    public void updateDoors(int playerSector) {

        ArrayList<Door> updateDoors = sectorsHashMap.get(String.valueOf(playerSector));

        if (updateDoors != null) {
            for (Door door : updateDoors) {
                door.updateDoor();
            }
        }
    }

    public void loadMapAndInstantiateDoors(String mapPath) {
        int[] numberDoorsInSector = new int[25];
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load(mapPath);
        MapLayer objectLayer = map.getLayers().get("doorLayer");

        if (objectLayer == null) {
            System.out.println("No 'Objects' layer found in the map.");
            return;
        }

        MapObjects objects = objectLayer.getObjects();
        for (MapObject object : objects) {

            String objectClass = object.getProperties().get("type", String.class);
            if ("Door".equals(objectClass)) {

                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                float width = object.getProperties().get("width", Float.class);
                float height = object.getProperties().get("height", Float.class);
                boolean isLocked = object.getProperties().get("locked", Boolean.class);

                int sector = findSector((int) x, (int) y);

                if (!sectorsHashMap.containsKey(String.valueOf(sector))) {
                    sectorDoorsArray = new ArrayList<>();
                    sectorsHashMap.put(String.valueOf(sector), sectorDoorsArray);
                }

                ArrayList<Door> temp = sectorsHashMap.get(String.valueOf(sector));

                door = new Door((int) x, (int) y, (int) width, (int) height, sector, numberDoorsInSector[sector], isLocked, player, collisionMap, lightMap);
                numberDoorsInSector[sector]++;
                temp.add(door);

                sectorsHashMap.put(String.valueOf(sector), temp);
            }
        }
    }

    public Door getDoor(int sector, int number) {
        return sectorsHashMap.get(String.valueOf(sector)).get(number);
    }
}
