package com.Rye.DarknessGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PDA {

    Player player;

    Sprite PDA;

    int x;
    int y;

    boolean pickedUp = false;
    SpriteBatch spriteBatch;


    public PDA(Player player) {
        this.player = player;

        x = 9450;
        y = 50;

        PDA = new Sprite();
        spriteBatch = new SpriteBatch();

        Texture pdaTexture = new Texture(Gdx.files.internal("TexSprites/PDA.png"));
        PDA = new Sprite(pdaTexture);

    }


    public void updatePDA() {
        isPlayerNear();
        if (!pickedUp) {
            drawMyself();
        }
    }

    public void isPlayerNear(){
        double playerDistance = MathFunctions.distanceFromMe(x,y,player.getCoorX(),player.getCoorY());

        if (playerDistance < 50 && !pickedUp){
            PopUpManager.displayPopUp(x-20, y, "E",player);
            if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                pickedUp = true;
                SoundEffects.playMusic("pdaFound");
            }
        }
    }

    public void drawMyself() {
        spriteBatch.setProjectionMatrix(player.getCamera().combined);
        spriteBatch.begin();
        PDA.setPosition(x - PDA.getWidth() / 2, y - PDA.getHeight() / 2);
        PDA.setRotation(52);
        PDA.draw(spriteBatch);
        spriteBatch.end();
    }


}
