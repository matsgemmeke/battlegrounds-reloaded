package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.item.Item;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code ItemRegistry} interface provides methods for registering items to a game. It allows for registering an
 * item either directly or with an associated holder.
 *
 * @param <T> the type of item
 * @param <S> the type of item holder
 */
public interface ItemRegistry<T extends Item, S extends ItemHolder> {

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
}
