package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents an activity which groups multiple players together to play under the rules of the plugin.
 */
public interface Game {

    /**
     * Adds an action executor to the game instance.
     *
     * @param actionExecutor the action executor
     */
    void addActionExecutor(@NotNull ActionExecutor actionExecutor);

    /**
     * Gets the item container which keeps equipment items.
     *
     * @return the equipment container
     */
    @NotNull
    ItemContainer<Equipment, EquipmentHolder> getEquipmentContainer();

    /**
     * Gets the item container which keeps gun items.
     *
     * @return the gun container
     */
    @NotNull
    ItemContainer<Gun, GunHolder> getGunContainer();

    /**
     * Gets the item behaviors registered to the game.
     *
     * @return the game's item behavior instances
     */
    @NotNull
    Collection<ActionExecutor> getActionExecutors();

    /**
     * Gets the entity container which keeps {@link GamePlayer} instances.
     *
     * @return the player container
     */
    @NotNull
    EntityContainer<GamePlayer> getPlayerContainer();

    /**
     * Gets the spawn point container.
     *
     * @return the spawn point container of the game
     */
    @NotNull
    SpawnPointContainer getSpawnPointContainer();
}
