package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Adds an item entity to the game and creates a {@link GameItem} instance.
     *
     * @param item the item to add
     * @return the created {@link GamePlayer} instance
     */
    @NotNull
    GameItem addItem(@NotNull Item item);

    /**
     * Adds a player to the context and creates a {@link GamePlayer} instance.
     *
     * @param player the player to add
     * @return the created {@link GamePlayer} instance
     */
    @NotNull
    GamePlayer addPlayer(@NotNull Player player);

    /**
     * Calculates the amount of damage produced in an event where an entity directly damages another entity. This can
     * be, for example, a player who attack another player, or an item entity explosion inflicting damage on another
     * entity.
     *
     * @param damager the entity who inflicted damage
     * @param entity the entity who got damaged
     * @param damage the original damage amount
     * @return the amount of produced damage
     */
    double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, double damage);

    /**
     * Gets the context of the game instance which holds the instances to component interfaces.
     *
     * @return the game context
     */
    @NotNull
    GameContext getContext();

    /**
     * Gets the item storage for equipment items.
     *
     * @return the equipment storage
     */
    @NotNull
    ItemStorage<Equipment, EquipmentHolder> getEquipmentStorage();

    /**
     * Finds the {@link GamePlayer} instance for a player entity. Returns null if there is no entry for the player.
     *
     * @param player the player entity
     * @return the {@link GamePlayer} entry or null if there is none for the player
     */
    @Nullable
    GamePlayer getGamePlayer(@NotNull Player player);

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
     * Gets the item entity storages of the game. Not to be confused with storage for held items, these have their own
     * specific storages. See for example {@link #getGunStorage()}.
     *
     * @return the item entity storage of the game
     */
    @NotNull
    EntityStorage<GameItem> getItemStorage();

    /**
     * Gets whether a specific entity is present in the game instance.
     *
     * @param entity the entity
     * @return whether the entity is present in the game
     */
    boolean hasEntity(@NotNull Entity entity);

    /**
     * Gets whether a player is present in the context.
     *
     * @param player the player
     * @return whether the context has the player
     */
    boolean hasPlayer(@NotNull Player player);
}
