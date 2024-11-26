package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.function.Consumer;

public class UIManager {

    private static UIManager instance;
    private Stage uiStage;
    private Skin skin;
    private InputProcessor originalInputProcessor;

    // Private constructor to prevent direct instantiation
    private UIManager() {
        // Initialize the stage
        uiStage = new Stage();

        // Load UI skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }

    // Method to show a keypad popup
    public void showKeyPadPopup(String title, Consumer<String> onSubmit) {
        // Clear any existing popups
        uiStage.clear();

        // Create popup window
        Window popup = new Window(title, skin);
        popup.setSize(300, 200);
        popup.setPosition(
            Gdx.graphics.getWidth() / 2f - popup.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - popup.getHeight() / 2f
        );
        popup.setTouchable(Touchable.enabled);

        // Create text field
        TextField textField = new TextField("", skin);
        textField.setTouchable(Touchable.enabled);

        // Submit button
        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String userInput = textField.getText();
                onSubmit.accept(userInput);
                hidePopup();
            }
        });

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hidePopup();
            }
        });

        // Layout the popup
        popup.add(textField).width(200).pad(10);
        popup.row();
        popup.add(submitButton).pad(10);
        popup.add(cancelButton).pad(10);

        // Add popup to stage
        uiStage.addActor(popup);

        // Store and replace input processor
        originalInputProcessor = Gdx.input.getInputProcessor();
        System.out.println(originalInputProcessor);
        Gdx.input.setInputProcessor(uiStage);

        // Set focus
        uiStage.setKeyboardFocus(textField);
    }

    public void hidePopup() {
        uiStage.clear();
        if (originalInputProcessor != null) {
            System.out.println(originalInputProcessor);
            Gdx.input.setInputProcessor(originalInputProcessor);
        }
    }
    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }
}
