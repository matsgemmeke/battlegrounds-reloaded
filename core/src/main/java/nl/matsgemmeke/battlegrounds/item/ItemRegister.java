package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores and handles states of a specific subtype of item.
 */
public class ItemRegister<T extends Item, U extends ItemHolder> {

    @NotNull
    private ConcurrentMap<U, Set<T>> assignedItems;
    @NotNull
    private Set<T> unassignedItems;

    public ItemRegister() {
        this.assignedItems = new ConcurrentHashMap<>();
        this.unassignedItems = new HashSet<>();
    }

    /**
     * Adds an item to the register that already has an assigned holder.
     *
     * @param item the item
     * @param holder the holder
     */
    public void addAssignedItem(@NotNull T item, @NotNull U holder) {
        assignedItems.putIfAbsent(holder, new HashSet<>());
        assignedItems.get(holder).add(item);
    }

    /**
     * Adds an unassigned item to the register.
     *
     * @param item the item
     */
    public void addUnassignedItem(@NotNull T item) {
        unassignedItems.add(item);
    }

    /**
     * Attempts to find an item in the register by its {@link ItemStack}, and is assigned to a specific holder. Returns
     * null if the register does not have an item with the corresponding {@link ItemStack} and holder.
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
     * Attempts to find an item in the register by its {@link ItemStack}. Returns null if the register does not contain
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
     * Removes an assigned item from the register.
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
     * Removes an unassigned item from the register.
     *
     * @param item the item to remove
     * @return whether the item was removed
     */
    public boolean removeUnassignedItem(@NotNull T item) {
        return unassignedItems.remove(item);
    }
}
