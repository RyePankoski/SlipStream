package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public class Inventory {
    private final Player player;
    private Array<InventoryItem> visualItems;

    private boolean isOpen;
    private InventoryItem selectedItem;
    private boolean isMoving = false; // Track if item is being moved
    private float originalX, originalY; // Store original position for cancellation
    ShapeRenderer shapeRenderer;
    private static Inventory instance;
    private static final float WINDOW_WIDTH = 200;
    private static final float WINDOW_HEIGHT = 120;
    private static final float CELL_SIZE = 40; // Size of each grid cell
    private static final float OVERLAP_SCALE = 0.7f; // Scale factor when overlapping
    private static final float SELECTED_SCALE = 0.9f; // Scale factor for selected items
    private static final float SELECTED_ALPHA = 0.8f; // Transparency for selected items
    private static final float MOVING_ALPHA = 0.5f;

    public Inventory(Player player) {
        this.player = player;
        this.visualItems = new Array<>();
        this.isOpen = false;
        shapeRenderer = new ShapeRenderer();
        instance = this;
    }

    private void refreshFromPlayer() {
        visualItems.clear();  // Clear existing InventoryItems

        for(Item playerItem : player.getItems()) {
            InventoryItem visualItem = new InventoryItem(
                playerItem.getType(),
                playerItem.getWidth(),
                playerItem.getHeight()
            );

            // Find first non-colliding position
            float x = 0;
            float y = 0;

            while (wouldCollideWithOtherItems(x, y, visualItem)) {
                x += CELL_SIZE;
                if (x + visualItem.getWidth() * CELL_SIZE > WINDOW_WIDTH) {
                    x = 0;
                    y += CELL_SIZE;
                    if (y + visualItem.getHeight() * CELL_SIZE > WINDOW_HEIGHT) {
                        // No space found
                        return;
                    }
                }
            }
            visualItem.setX(x);
            visualItem.setY(y);
            visualItems.add(visualItem);
        }
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            isOpen = !isOpen;
            if (isOpen) {
                refreshFromPlayer();  // Only refresh when opening
            }
            if (!isOpen) {
                cancelMovement();
            }
        }
        if (isOpen) {
            handleItemMovement();
        }
    }

    private void handleItemMovement() {
        // Select next/previous item
        if (!isMoving) {  // Only allow selection/dropping when not moving an item
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                selectNextItem();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                selectPreviousItem();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT) && selectedItem != null) {
                dropSelectedItem();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (selectedItem == null) {
                    if (visualItems.size > 0) {
                        selectedItem = visualItems.first();
                    }
                } else {
                    startMovement();
                }
            }
        }
        // Move selected item
        if (selectedItem != null) {
            // Start movement
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isMoving) {
                startMovement();
            }

            if (isMoving) {


                float newX = selectedItem.getX();
                float newY = selectedItem.getY();



                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    newX -= CELL_SIZE;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    newX += CELL_SIZE;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    newY += CELL_SIZE;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    newY -= CELL_SIZE;
                }

                // Check if new position is within bounds
                if (isPositionWithinBounds(newX, newY, selectedItem)) {
                    selectedItem.setX(newX);
                    selectedItem.setY(newY);
                }

                // Handle rotation during movement
                if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                    int newWidth = selectedItem.getHeight();
                    int newHeight = selectedItem.getWidth();
                    if (isPositionWithinBounds(selectedItem.getX(), selectedItem.getY(), newWidth, newHeight)) {
                        selectedItem.rotate();
                    }
                }

                // Finalize or cancel movement
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    finalizeMovement();
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                    cancelMovement();
                }
            }
        }
    }

    private void dropSelectedItem() {
        if (selectedItem != null) {
            // Remove the item from player's inventory
            int index = visualItems.indexOf(selectedItem, true);
            if (index >= 0 && index < player.getItems().size) {
                player.getItems().removeIndex(index);
            }

            // Remove from visual inventory
            visualItems.removeValue(selectedItem, true);

            // Clear selection
            selectedItem = null;
            isMoving = false;
        }
    }

    private void startMovement() {
        isMoving = true;
        originalX = selectedItem.getX();
        originalY = selectedItem.getY();
    }

    private void finalizeMovement() {
        if (!wouldCollideWithOtherItems(selectedItem.getX(), selectedItem.getY(), selectedItem)) {
            isMoving = false;
            selectedItem = null;  // Deselect the item after finalizing movement
        } else {
            // If position is invalid, return to original position
            cancelMovement();
        }
    }

    private void cancelMovement() {
        if (selectedItem != null && isMoving) {
            selectedItem.setX(originalX);
            selectedItem.setY(originalY);
        }
        isMoving = false;
        selectedItem = null;  // Also deselect when canceling movement
    }

    private boolean isPositionWithinBounds(float x, float y, InventoryItem item) {
        return isPositionWithinBounds(x, y, item.getWidth(), item.getHeight());
    }

    private boolean isPositionWithinBounds(float x, float y, int width, int height) {
        return x >= 0 &&
            y >= 0 &&
            x + width * CELL_SIZE <= WINDOW_WIDTH &&
            y + height * CELL_SIZE <= WINDOW_HEIGHT;
    }

    private boolean wouldCollideWithOtherItems(float newX, float newY, InventoryItem item) {
        Rectangle itemBounds = new Rectangle(
            newX,
            newY,
            item.getWidth() * CELL_SIZE,
            item.getHeight() * CELL_SIZE
        );

        // Use index-based iteration instead of iterator
        for (int i = 0; i < visualItems.size; i++) {
            InventoryItem otherItem = visualItems.get(i);
            if (otherItem == item) continue;

            Rectangle otherBounds = new Rectangle(
                otherItem.getX(),
                otherItem.getY(),
                otherItem.getWidth() * CELL_SIZE,
                otherItem.getHeight() * CELL_SIZE
            );

            if (itemBounds.overlaps(otherBounds)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverlappingAnyItem(InventoryItem item) {
        // Create temporary arrays for item bounds
        Rectangle itemBounds = new Rectangle(
            item.getX(),
            item.getY(),
            item.getWidth() * CELL_SIZE,
            item.getHeight() * CELL_SIZE
        );

        for (int i = 0; i < visualItems.size; i++) {
            InventoryItem otherItem = visualItems.get(i);
            if (otherItem == item) continue;

            Rectangle otherBounds = new Rectangle(
                otherItem.getX(),
                otherItem.getY(),
                otherItem.getWidth() * CELL_SIZE,
                otherItem.getHeight() * CELL_SIZE
            );

            if (itemBounds.overlaps(otherBounds)) {
                return true;
            }
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        if (!isOpen) return;

        batch.end();

        float windowX = player.getCoorX() - WINDOW_WIDTH / 2;
        float windowY = player.getCoorY() - WINDOW_HEIGHT / 2;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw main window
        shapeRenderer.setColor(new Color(0, 0, 0, 1f));
        shapeRenderer.rect(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT);

        // First draw non-selected items
        for (int i = 0; i < visualItems.size; i++) {
            InventoryItem item = visualItems.get(i);
            if (item != selectedItem) {  // Skip the selected item
                shapeRenderer.setColor(item.getColor());
                shapeRenderer.rect(
                    windowX + item.getX(),
                    windowY + item.getY(),
                    item.getWidth() * CELL_SIZE,
                    item.getHeight() * CELL_SIZE
                );
            }
        }

        // Then draw selected item last, so it's always on top
        if (selectedItem != null) {
            float scale = SELECTED_SCALE; // Default scale for selected items

            // If moving and overlapping, use the smaller overlap scale
            if (isMoving && isOverlappingAnyItem(selectedItem)) {
                scale = OVERLAP_SCALE;
            }

            Color itemColor = selectedItem.getColor().cpy().add(0.2f, 0.2f, 0.2f, 0);
            itemColor.a = isMoving ? MOVING_ALPHA : SELECTED_ALPHA; // Apply appropriate transparency

            shapeRenderer.setColor(itemColor);

            // Calculate scaled dimensions and position
            float width = selectedItem.getWidth() * CELL_SIZE * scale;
            float height = selectedItem.getHeight() * CELL_SIZE * scale;
            // Center the scaled item in its cell
            float x = windowX + selectedItem.getX() + (selectedItem.getWidth() * CELL_SIZE * (1 - scale)) / 2;
            float y = windowY + selectedItem.getY() + (selectedItem.getHeight() * CELL_SIZE * (1 - scale)) / 2;

            shapeRenderer.rect(x, y, width, height);
        }

        shapeRenderer.end();
        batch.begin();
    }

    private void selectNextItem() {
        int currentIndex = selectedItem != null ? visualItems.indexOf(selectedItem, true) : -1;
        if (visualItems.size > 0) {
            currentIndex = (currentIndex + 1) % visualItems.size;
            selectedItem = visualItems.get(currentIndex);
        }
    }

    private void selectPreviousItem() {
        int currentIndex = selectedItem != null ? visualItems.indexOf(selectedItem, true) : 0;
        if (visualItems.size > 0) {
            currentIndex = (currentIndex - 1 + visualItems.size) % visualItems.size;
            selectedItem = visualItems.get(currentIndex);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public static Inventory getInstance() {
        return instance;
    }
}
