package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemReceiver {

    void setHeldItem(@NotNull ItemStack itemStack);
}
