package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
     * Gets the item register for equipment.
     *
     * @return the equipment register
     */
    @NotNull
    ItemRegister<Equipment, EquipmentHolder> getEquipmentRegister();

    /**
     * Finds the {@link GamePlayer} instance for a player entity. Returns null if there is no entry for the player.
     *
     * @param player the player entity
     * @return the {@link GamePlayer} entry or null if there is none for the player
     */
    @Nullable
    GamePlayer getGamePlayer(@NotNull Player player);

    /**
     * Gets the item register for guns.
     *
     * @return the gun register
     */
    @NotNull
    ItemRegister<Gun, GunHolder> getGunRegister();

    /**
     * Gets the item behaviors registered to the game.
     *
     * @return the game's item behavior instances
     */
    @NotNull
    Collection<ItemBehavior> getItemBehaviors();

    /**
     * Executes logic that handles item swaps by players.
     *
     * @param gamePlayer the player
     * @param swapFrom the item that the player swaps from
     * @param swapTo the item that the player swaps to
     * @return whether the action should be performed
     */
    boolean handleItemSwap(@NotNull GamePlayer gamePlayer, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo);

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
