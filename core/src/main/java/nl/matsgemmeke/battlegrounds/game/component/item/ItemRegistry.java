package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.Item;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code ItemRegistry} interface provides methods for registering items to a game. It allows for registering an
 * item either directly or with an associated holder.
 *
 * @param <T> the type of item
 * @param <S> the type of item holder
 */
public interface ItemRegistry<T extends Item, S extends ItemHolder> {

    @NotNull
    List <T> findAll();

    /**
     * Gets all registered items that are assigned to a given holder.
     *
     * @param holder the item holder
     * @return a list of items assigned to the holder
     */
    @NotNull
    List<T> getAssignedItems(@NotNull S holder);

    /**
     * Registers an item to the game.
     *
     * @param item the item to be registered
     */
    void registerItem(@NotNull T item);

    /**
     * Registers an item to the game with an associated holder.
     *
     * @param item the item to be registered
     * @param holder the holder associated with the item
     */
    void registerItem(@NotNull T item, @NotNull S holder);

    /**
     * Removes the assigned holder from the given item and registers it as an unassigned item. This method will do
     * nothing if the given item has no assigned holder.
     *
     * @param item the item whose holder is to be unregistered
     */
    void unassignItem(@NotNull T item);
}
