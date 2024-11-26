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


    private UIManager() {
        uiStage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }

    public void voltageRegulator(String title, Consumer<String> onSubmit) {
        uiStage.clear();


        Window popup = new Window(title, skin);
        popup.setSize(300, 200);
        popup.setPosition(
            Gdx.graphics.getWidth() / 2f - popup.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - popup.getHeight() / 2f
        );
        popup.setTouchable(Touchable.enabled);

        TextField voltage1 = new TextField("", skin);
        voltage1.setTouchable(Touchable.enabled);

        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String userInput = voltage1.getText();
                onSubmit.accept(userInput);
                hidePopup();
            }
        });


        popup.add(voltage1).width(200).pad(10);
        popup.row();
        popup.add(submitButton).pad(10);

        uiStage.addActor(popup);
        Gdx.input.setInputProcessor(uiStage);
        uiStage.setKeyboardFocus(voltage1);
    }

    public void showKeyPadPopup(String title, Consumer<String> onSubmit) {
        uiStage.clear();

        Window popup = new Window(title, skin);
        popup.setSize(300, 200);
        popup.setPosition(
            Gdx.graphics.getWidth() / 2f - popup.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - popup.getHeight() / 2f
        );
        popup.setTouchable(Touchable.enabled);

        TextField textField = new TextField("", skin);
        textField.setTouchable(Touchable.enabled);

        TextButton submitButton = new TextButton("ATTEMPT", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String userInput = textField.getText();
                onSubmit.accept(userInput);
                hidePopup();
            }
        });


        popup.add(textField).width(200).pad(10);
        popup.row();
        popup.add(submitButton).pad(10);

        uiStage.addActor(popup);
        Gdx.input.setInputProcessor(uiStage);
        uiStage.setKeyboardFocus(textField);
    }

    public void hidePopup() {
        uiStage.clear();
    }
    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }
}
