package com.Rye.DarknessGame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;

public class Rifle extends Weapon {

    public Rifle(float fireRate, int magazines, int magazineSize, int damage, ArrayList<Sound> sounds,
                 OrthographicCamera camera, Player player, Hud hud, double bulletSpeed, Monster monster,int hitSize,int maxMagazines) {
        super(fireRate, magazines, magazineSize, damage, sounds, camera, player, hud, bulletSpeed, monster, hitSize,maxMagazines );
        hud.updateWeaponStats(ammo, magazines, magazineSize,maxMagazines);


        width = 40;
        height = 120;
    }

    @Override
    public void fireWeapon() {

        if (canFire && ammo > 0) {
            if (player.getStamina() > 0) {
                player.setStamina(player.getStamina() - 10);
            }
        }
        super.fireWeapon();
    }

    @Override
    public Weapon getWeaponType() {
        return this;
    }


}
