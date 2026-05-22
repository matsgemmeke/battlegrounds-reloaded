package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.bukkit.Location;

/**
 * A player who participates in a {@link Game}.
 */
public interface GamePlayer extends GameEntity, EquipmentUser, GunUser, MeleeWeaponUser, PotionEffectReceiver {

    /**
     * Gets whether the player is passive and cannot inflict or take damage from the plugin's custom items.
     *
     * @return whether the player is passive
     */
    boolean isPassive();

    /**
     * Sets whether the player is passive and cannot inflict or take damage from the plugin's custom items.
     *
     * @param passive whether the player is passive
     */
    void setPassive(boolean passive);

    /**
     * Plays a given sound to the player at a given location.
     *
     * @param location the location to play the sound
     * @param sound    the sound to play
     */
    void playSound(Location location, GameSound sound);
}
