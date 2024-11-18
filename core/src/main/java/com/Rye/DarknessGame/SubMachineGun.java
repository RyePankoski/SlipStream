package com.Rye.DarknessGame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.ArrayList;

public class SubMachineGun extends Weapon {
    public SubMachineGun(float fireRate, int magazines, int magazineSize, int damage, ArrayList<Sound> sounds,
                         OrthographicCamera camera, Player player,Hud hud,double bulletSpeed,Monster monster,int hitSize,int maxMagazines) {
        super(fireRate, magazines, magazineSize, damage, sounds,camera,player,hud,bulletSpeed,monster,hitSize,maxMagazines);

        hud.updateWeaponStats(ammo,magazines,magazineSize,maxMagazines);
    }
    @Override
    public void fireWeapon(){
        if (ammo == 0 && canAmmoSound){
            outOfAmmoSound.play();
            canAmmoSound = false;
        }
        if (canFire && ammo > 0){
            if (player.getStamina() > 0) {
                player.setStamina(player.getStamina() - 0.3);
            }
        }
        super.fireWeapon();
    }

    @Override
    public Weapon getWeaponType(){
        return this;
    }

}
