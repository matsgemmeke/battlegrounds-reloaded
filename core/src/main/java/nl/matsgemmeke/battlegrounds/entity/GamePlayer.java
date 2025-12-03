package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A player who participates in a {@link Game}.
 */
public interface GamePlayer extends GameEntity, EquipmentHolder, GunHolder, MeleeWeaponHolder, PotionEffectReceiver {

    /**
     * Gets the {@link Player} entity of the object.
     *
     * @return the player entity
     */
    @NotNull
    Player getEntity();

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
}
