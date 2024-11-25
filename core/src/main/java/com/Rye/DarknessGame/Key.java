package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Key {
    int x;
    int y;
    int sector;
    int doorNumber;
    ShapeRenderer shapeRenderer;
    Player player;
    boolean pickedUp = false;

    public Key(int x, int y, int sector, int doorNumber, Player player) {
        this.x = x;
        this.y = y;
        this.sector = sector;
        this.doorNumber = doorNumber;
        this.player = player;
        shapeRenderer = new ShapeRenderer();
    }

    public void updateKey() {
        drawMyself();
        isPlayerNear();
    }

    public void drawMyself() {
        if(!pickedUp) {
            shapeRenderer.setProjectionMatrix(player.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.circle(x, y, 5);
            shapeRenderer.end();
        }
    }

    public void isPlayerNear() {
        if (MathFunctions.distanceFromMe(x, y, player.getCoorX(), player.getCoorY()) < 10) {
            pickedUp = true;
            SoundEffects.playSound("subObjectiveComplete");
            player.addKey(String.valueOf(sector) + String.valueOf(doorNumber),this);
            System.out.println(String.valueOf(sector) + String.valueOf(doorNumber));
//            dispose();
        }
    }

    public int getSector() {
        return sector;
    }

    public int getDoorNumber() {
        return doorNumber;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
