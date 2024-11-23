package com.Rye.DarknessGame;

import java.util.ArrayList;

public class StaticLightSource {


    float[] vertices;

    int x;

    int y;

    float brightness;

    int angle;

    public StaticLightSource(int posY, int posX, float brightness, int angle, float[] vertices) {
        initVariables(posX, posY, brightness, vertices,angle);
    }

    public void initVariables(int x, int y, float brightness, float[] vertices,int angle) {
        this.x = x;
        this.y = y;
        this.vertices = vertices;
        this.brightness = brightness;
        this.angle = angle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int getAngle() {
        return angle;
    }
}
