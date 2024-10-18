package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DefaultActivator implements Activator {

    private boolean ready;
    @Nullable
    private ItemStack itemStack;
    @NotNull
    private ItemTemplate itemTemplate;

    public DefaultActivator(@NotNull ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
        this.ready = false;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isReady() {
        return ready;
    }

    public void prepare(@NotNull Map<String, Object> values) {
        itemStack = itemTemplate.createItemStack(values);
        ready = true;
    }
}
