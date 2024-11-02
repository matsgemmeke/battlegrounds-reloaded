package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DefaultActivator implements Activator {

    private boolean ready;
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
        ItemStack itemStack = itemTemplate.createItemStack();
        holder.setHeldItem(itemStack);

        ready = true;
    }
}
