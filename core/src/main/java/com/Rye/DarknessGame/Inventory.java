package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class Inventory {
    private final Player player;
    private final Array<InventoryItem> items;
    private boolean isOpen;
    private InventoryItem selectedItem;

    ShapeRenderer shapeRenderer;
    private static Inventory instance;

    private static final float WINDOW_WIDTH = 400;
    private static final float WINDOW_HEIGHT = 200;
    private static final float CELL_SIZE = 40; // Size of each grid cell

    public Inventory(Player player) {
        this.player = player;
        this.items = new Array<InventoryItem>();
        this.isOpen = false;
        shapeRenderer = new ShapeRenderer();
        instance = this;

        // Add some test items
        addItem(ItemType.WEAPON, 2, 1); // 2x1 weapon
        addItem(ItemType.POTION, 1, 1); // 1x1 potion
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            isOpen = !isOpen;
        }
        if (isOpen) {
            handleItemMovement();
        }
        DebugUtility.updateVariable("Inv-Status", String.valueOf(isOpen));
    }

    private void handleItemMovement() {
        // Select next/previous item
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            selectNextItem();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            selectPreviousItem();
        }

        // Move selected item
        if (selectedItem != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                selectedItem.setX(selectedItem.getX() - CELL_SIZE);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                selectedItem.setX(selectedItem.getX() + CELL_SIZE);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedItem.setY(selectedItem.getY() + CELL_SIZE);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedItem.setY(selectedItem.getY() - CELL_SIZE);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                selectedItem.rotate();
            }
        }
    }

    private void selectNextItem() {
        int currentIndex = selectedItem != null ? items.indexOf(selectedItem, true) : -1;
        if (items.size > 0) {
            currentIndex = (currentIndex + 1) % items.size;
            selectedItem = items.get(currentIndex);
        }
    }

    private void selectPreviousItem() {
        int currentIndex = selectedItem != null ? items.indexOf(selectedItem, true) : 0;
        if (items.size > 0) {
            currentIndex = (currentIndex - 1 + items.size) % items.size;
            selectedItem = items.get(currentIndex);
        }
    }

    public void render(SpriteBatch batch) {
        if (!isOpen) return;

        batch.end();

        float windowX = player.getCoorX() - WINDOW_WIDTH/2;
        float windowY = player.getCoorY() - WINDOW_HEIGHT/2;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw main window
        shapeRenderer.setColor(new Color(0, 0, 0, 1f));
        shapeRenderer.rect(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Draw items
        for (InventoryItem item : items) {
            // Highlight selected item with a different color
            if (item == selectedItem) {
                shapeRenderer.setColor(item.getColor().cpy().add(0.2f, 0.2f, 0.2f, 0));
            } else {
                shapeRenderer.setColor(item.getColor());
            }
            shapeRenderer.rect(windowX + item.getX(),
                windowY + item.getY(),
                item.getWidth() * CELL_SIZE,
                item.getHeight() * CELL_SIZE);
        }

        shapeRenderer.end();
        batch.begin();
    }

    public void addItem(ItemType type, int width, int height) {
        InventoryItem newItem = new InventoryItem(type, width, height);
        // Set initial position
        newItem.setX(items.size * CELL_SIZE);
        newItem.setY(0);
        items.add(newItem);

        // Select first item added
        if (selectedItem == null) {
            selectedItem = newItem;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public static Inventory getInstance() {
        return instance;
    }
}
