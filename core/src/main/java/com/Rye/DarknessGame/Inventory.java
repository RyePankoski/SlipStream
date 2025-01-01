package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class Inventory {

    Player player;
    ShapeRenderer shapeRenderer;
    boolean isOpen = false;
    boolean[][] emptySpaces;
    final float WIDTH = 280, HEIGHT = 160, ITEM_SIZE = 40;
    public Array<InventoryObject> playerInventory;
    public static Inventory instance;
    int index = 0;
    InventoryObject currentItem, selectedItem;


    public Inventory(Player player) {
        this.player = player;
        shapeRenderer = new ShapeRenderer();
        playerInventory = player.getItems();
        emptySpaces = new boolean[(int) (WIDTH / ITEM_SIZE)][(int) (HEIGHT / ITEM_SIZE)];
        updateInventory();
        setInitialPositions();
    }

    public void update() {
        handleInputs();
        if (isOpen) {
            updateInventory();
            beginRender();
        }
    }

    public void setInitialPositions() {
        for (int x = 0; x < emptySpaces.length; x++) {
            for (int y = 0; y < emptySpaces[0].length; y++) {
                emptySpaces[x][y] = true;
            }
        }
    }


    public void updateInventory() {
        playerInventory = player.getItems();
    }

    public void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            isOpen = !isOpen;
        }
        selectItem();
        moveItem();
    }

    public void selectItem() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            index++;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            index--;
        }
        if (index > playerInventory.size - 1) {
            index = 0;
        }
        if (index < 0) {
            index = playerInventory.size - 1;
        }
        selectedItem = playerInventory.get(index);
    }

    public void moveItem() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            float newPosition = selectedItem.getPositionX() + ITEM_SIZE;
            if (newPosition < WIDTH) {
                selectedItem.incrementPositionX((int) ITEM_SIZE);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            float newPosition = selectedItem.getPositionX() - ITEM_SIZE;
            if (newPosition >= 0) {
                selectedItem.incrementPositionX((int) -ITEM_SIZE);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            float newPosition = selectedItem.getPositionY() + ITEM_SIZE;
            if (newPosition < HEIGHT) {
                selectedItem.incrementPositionY((int) ITEM_SIZE);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            float newPosition = selectedItem.getPositionY() - ITEM_SIZE;
            if (newPosition >= 0) {
                selectedItem.incrementPositionY((int) -ITEM_SIZE);
            }
        }
    }

    public void beginRender() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(player.getCamera().combined);
        render();
    }

    public void render() {
        float x = player.getCoorX();
        float y = player.getCoorY();

        shapeRenderer.setColor(.1f, .1f, .1f, 1f);
        shapeRenderer.rect(player.getCoorX() - WIDTH / 2, player.getCoorY() - HEIGHT / 2, WIDTH, HEIGHT);
        shapeRenderer.setColor(1f, 0f, 0f, 1f);

        for (int i = 0; i < playerInventory.size; i++) {
            currentItem = playerInventory.get(i);
            shapeRenderer.rect(x - currentItem.getWidth() / 2 + currentItem.getPositionX(), y - currentItem.getHeight() / 2 + currentItem.getPositionY(),
                currentItem.getWidth(), currentItem.getHeight());
        }
        endRender();
    }

    public void endRender() {
        shapeRenderer.end();
    }


    public static Inventory getInstance() {
        return instance;
    }
}
