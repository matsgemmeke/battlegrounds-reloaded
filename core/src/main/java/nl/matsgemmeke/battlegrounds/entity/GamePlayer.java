package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A player who participates in a {@link Game}.
 */
public interface GamePlayer extends GameEntity, EquipmentHolder, GunHolder {

    /**
     * Gets the {@link Player} entity of the object.
     *
     * @return the player entity
     */
    @NotNull
    Player getEntity();
}
