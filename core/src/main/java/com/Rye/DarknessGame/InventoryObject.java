package com.Rye.DarknessGame;

public class InventoryObject {

    protected float width;
    protected float height;
    protected int positionX = 0;
    protected int positionY = 0;
    public InventoryObject(){}
    public void rotate(){
        float temp = 0;
        temp = width;
        width = height;
        height = temp;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public  int getPositionX(){
        return positionX;
    }
    public int getPositionY(){
        return positionY;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void incrementPositionX(int offset){
        positionX += offset;
    }
    public void incrementPositionY(int offset){
        positionY += offset;
    }

}
