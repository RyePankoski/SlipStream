package com.Rye.DarknessGame.InteractableLibrary;

import com.Rye.DarknessGame.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyPad extends Interactable {
    private final int x;
    private final int y;
    Door door;

    public KeyPad(Player player, Door door) {
        super(player);
        this.door = door;
        x = 8320;
        y = 280;
    }

    public void isPlayerNear() {

        double playerDistance = MathFunctions.distanceFromMe(x, y, player.getCoorX(), player.getCoorY());
        if (playerDistance < 50) {
            PopUpManager.displayPopUp(8340, 250, "E", player);

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                player.setInPopUp(true);
                Gdx.input.setCursorCatched(false);
                interact();
            }
        }
    }

    public void interact() {

        UIManager.getInstance().showKeyPadPopup("CODE:", (input) -> {
            if (input.equals("0723")) {
                player.setInPopUp(false);
                door.setLocked(false);
                SoundEffects.playSound("accessGranted");
                Gdx.input.setCursorCatched(true);
            } else {
                player.setInPopUp(false);
                SoundEffects.playSound("accessDenied");
                Gdx.input.setCursorCatched(true);
            }
        });
    }
}
