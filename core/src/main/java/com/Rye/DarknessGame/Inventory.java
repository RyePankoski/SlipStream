package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class Inventory {

    Player player;
    ShapeRenderer shapeRenderer;
    boolean isOpen = false;
    final float WIDTH = 200;
    final float HEIGHT = 120;

    public Array<InventoryObject> playerInventory;
    public static Inventory instance;

    Array<InventoryObject> currentItemProperties;

    InventoryObject currentItem;

    public Inventory(Player player){
        this.player = player;
        shapeRenderer = new ShapeRenderer();
        playerInventory = player.getItems();
    }

    public void update(){
        handleInputs();
        if (isOpen){
            updateInventory();
            beginRender();
        }
    }

    public void updateInventory(){
            playerInventory = player.getItems();
    }

    public void handleInputs(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)){
            isOpen = !isOpen;
        }
    }

    public void beginRender(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(player.getCamera().combined);
        render();
    }
    public void endRender(){
        shapeRenderer.end();
    }


    public void render(){
        float x = player.getCoorX();
        float y = player.getCoorY();


        shapeRenderer.setColor(.1f,.1f,.1f,1f);
        shapeRenderer.rect(player.getCoorX() - WIDTH/2 ,player.getCoorY() - HEIGHT/2 ,WIDTH,HEIGHT);
        shapeRenderer.setColor(1f,0f,0f,1f);

        for (int i = 0; i < playerInventory.size; i++) {
            currentItem = playerInventory.get(i);
            shapeRenderer.rect(x - currentItem.getWidth()/2 + currentItem.getPositionX(), y - currentItem.getHeight()/2 + currentItem.getGetPositionY(),
                currentItem.getWidth(), currentItem.getHeight());
        }
        endRender();
    }

    public static Inventory getInstance(){
        return instance;
    }
}
