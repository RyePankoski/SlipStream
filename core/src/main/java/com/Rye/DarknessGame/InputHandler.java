package com.Rye.DarknessGame;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashSet;
import java.util.Set;

public class InputHandler extends InputAdapter {
    Set<Integer> pressedKeysHash;
    Set<Integer> pressedMouseHash;
    public InputHandler(){
        pressedKeysHash = new HashSet<>();
        pressedMouseHash = new HashSet<>();
    }
    @Override
    public boolean keyDown(int keycode) {
        pressedKeysHash.add(keycode);
        return true;
    }
    @Override
    public boolean keyUp(int keycode) {
        pressedKeysHash.remove(keycode);
        return true;
    }
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pressedMouseHash.add(button);
        return true;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pressedMouseHash.remove(button);
        return true;
    }

    public Set<Integer> getPressedKeysHash() {
        return pressedKeysHash;
    }

    public Set<Integer> getPressedMouseHash() {
        return pressedMouseHash;
    }
}
