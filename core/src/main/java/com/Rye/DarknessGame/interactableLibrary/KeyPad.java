package com.Rye.DarknessGame.interactableLibrary;

import com.Rye.DarknessGame.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class KeyPad extends Interactable {
    private Stage stage;
    private Skin skin;
    private int x;
    private int y;
    private boolean popUpOpen;

    Door door;

    public KeyPad(Player player, Door door) {
        super(player);
        this.door = door;
        x = 8320;
        y = 280;
    }

    public void isPlayerNear(){
        double playerDistance = MathFunctions.distanceFromMe(x, y, player.getCoorX(), player.getCoorY());
        if(playerDistance < 50 && !popUpOpen){

            PopUpManager.displayPopUp(8340,250,"E",player);

            if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                player.setInPopUp(true);
                popUpOpen = true;
                Gdx.input.setCursorCatched(false);
                interact();
            }
        } else if (playerDistance > 70 && popUpOpen){
            popUpOpen = false;
            Gdx.input.setCursorCatched(true);
        }
    }

    public void interact() {
        UIManager.getInstance().showKeyPadPopup("Enter Code", (input) -> {
            if (input.equals("0723")) {
                player.setInPopUp(false);
                door.setLocked(false);
                SoundEffects.playSound("accessGranted");
                System.out.println("Correct code entered!");
            } else {
                player.setInPopUp(false);
                SoundEffects.playSound("accessDenied");
                System.out.println("Incorrect code");
            }
        });
    }
}
