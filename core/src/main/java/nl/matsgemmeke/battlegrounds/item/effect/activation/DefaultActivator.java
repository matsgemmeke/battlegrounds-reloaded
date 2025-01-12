package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultActivator implements Activator {

    private boolean ready;
    @Nullable
    private ItemHolder currentHolder;
    @Nullable
    private ItemStack heldItemStack;
    @NotNull
    private ItemTemplate itemTemplate;

    public DefaultActivator(@NotNull ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate.matchesTemplate(itemStack);
    }

    public void prepare(@NotNull ItemHolder holder) {
        // Do not prepare the activator again as it can only be held by one holder
        if (currentHolder != null || heldItemStack != null) {
            return;
        }

        ItemStack itemStack = itemTemplate.createItemStack();
        holder.setHeldItem(itemStack);

        currentHolder = holder;
        heldItemStack = itemStack;
        ready = true;
    }

    public boolean remove() {
        if (currentHolder == null || heldItemStack == null) {
            return false;
        }

        currentHolder.removeItem(heldItemStack);
        currentHolder = null;
        heldItemStack = null;
        return true;
    }
}
