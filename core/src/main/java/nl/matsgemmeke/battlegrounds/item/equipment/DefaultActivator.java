package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    public void prepare(@NotNull ItemHolder holder, @NotNull Map<String, Object> values) {
        ItemStack itemStack = itemTemplate.createItemStack(values);
        holder.setHeldItem(itemStack);

        ready = true;
    }
}
