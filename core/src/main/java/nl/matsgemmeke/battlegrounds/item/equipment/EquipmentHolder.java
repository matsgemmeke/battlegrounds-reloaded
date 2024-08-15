package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.holder.ItemThrower;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface EquipmentHolder extends ItemHolder, ItemThrower {

    /**
     * Gets the direction of where the holder would throw an equipment item.
     *
     * @return the throwing direction
     */
    @NotNull
    Location getThrowingDirection();
}
