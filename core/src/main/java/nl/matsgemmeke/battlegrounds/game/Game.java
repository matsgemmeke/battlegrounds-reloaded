package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
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
     * Adds an item behavior handles to the game instance.
     *
     * @param behavior the behavior handler
     */
    void addItemBehavior(@NotNull ItemBehavior behavior);

    /**
     * Gets the item storage for equipment items.
     *
     * @return the equipment storage
     */
    @NotNull
    ItemStorage<Equipment, EquipmentHolder> getEquipmentStorage();

    /**
     * Gets the item storage for gun items.
     *
     * @return the gun storage
     */
    @NotNull
    ItemStorage<Gun, GunHolder> getGunStorage();

    /**
     * Gets the item behaviors registered to the game.
     *
     * @return the game's item behavior instances
     */
    @NotNull
    Collection<ItemBehavior> getItemBehaviors();

    /**
     * Gets the player storage of the game.
     *
     * @return the player storage of the game
     */
    @NotNull
    EntityStorage<GamePlayer> getPlayerStorage();

    /**
     * Gets the spawn point storage of the game.
     *
     * @return the spawn point storage of the game
     */
    @NotNull
    SpawnPointStorage getSpawnPointStorage();
}
