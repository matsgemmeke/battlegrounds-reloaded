package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.item.Item;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Stores and handles states of a specific subtype of item.
 */
public class ItemContainer<T extends Item, U extends ItemHolder> {

    @NotNull
    private ConcurrentMap<U, List<T>> assignedItems;
    @NotNull
    private List<T> unassignedItems;

    public ItemContainer() {
        this.assignedItems = new ConcurrentHashMap<>();
        this.unassignedItems = new ArrayList<>();
    }

    /**
     * Adds an item to the storage that already has an assigned holder.
     *
     * @param item the item
     * @param holder the holder
     */
    public void addAssignedItem(@NotNull T item, @NotNull U holder) {
        assignedItems.putIfAbsent(holder, new ArrayList<>());
        assignedItems.get(holder).add(item);
    }

    /**
     * Adds an unassigned item to the storage.
     *
     * @param item the item
     */
    public void addUnassignedItem(@NotNull T item) {
        unassignedItems.add(item);
    }

    /**
     * Gathers all item instance in the storage and returns the result in a list. The list contain both assigned and
     * unassigned items.
     *
     * @return a list containing all item instances
     */
    @NotNull
    public List<T> getAllItems() {
        Stream<T> assignedItemsStream = assignedItems.values().stream().flatMap(List::stream);
        Stream<T> unassignedItemsStream = unassignedItems.stream();

        return Stream.concat(assignedItemsStream, unassignedItemsStream).toList();
    }

    /**
     * Attempts to find an item in the storage by its {@link ItemStack}, and is assigned to a specific holder. Returns
     * null if the storage does not have an item with the corresponding {@link ItemStack} and holder.
     *
     * @param holder the item holder
     * @param itemStack the item stack
     * @return the corresponding item or null if there are no matches
     */
    @Nullable
    public T getAssignedItem(@NotNull U holder, @NotNull ItemStack itemStack) {
        if (!assignedItems.containsKey(holder)) {
            return null;
        }

        return this.getItemFromCollection(assignedItems.get(holder), itemStack);
    }

    /**
     * Gets all items currently assigned to a given holder. Returns an empty list if the holder has no assigned items.
     *
     * @param holder the holder
     * @return a list containing items assigned to the holder
     */
    @NotNull
    public List<T> getAssignedItems(@NotNull U holder) {
        return assignedItems.getOrDefault(holder, Collections.emptyList());
    }

    /**
     * Attempts to find an item in the storage by its {@link ItemStack}. Returns null if the storage does not contain
     * an item with the given {@link ItemStack}.
     *
     * @param itemStack the item stack
     * @return the corresponding item or null if there are no matches
     */
    @Nullable
    public T getUnassignedItem(@NotNull ItemStack itemStack) {
        return this.getItemFromCollection(unassignedItems, itemStack);
    }

    @Nullable
    private T getItemFromCollection(@NotNull Collection<T> items, @NotNull ItemStack itemStack) {
        for (T item : items) {
            if (item.isMatching(itemStack)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Removes an assigned item from the storage.
     *
     * @param item the item to remove
     * @param holder the item's holder
     * @return whether the item was removed
     */
    public boolean removeAssignedItem(@NotNull T item, @NotNull U holder) {
        if (!assignedItems.containsKey(holder)) {
            return false;
        }

        return assignedItems.get(holder).remove(item);
    }

    /**
     * Removes an unassigned item from the storage.
     *
     * @param item the item to remove
     * @return whether the item was removed
     */
    public boolean removeUnassignedItem(@NotNull T item) {
        return unassignedItems.remove(item);
    }
}
