package nl.matsgemmeke.battlegrounds.item.equipment;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Activator {

    @Nullable
    ItemStack getItemStack();

    boolean isReady();

    void prepare(@NotNull Map<String, Object> values);
}
