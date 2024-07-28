package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface EquipmentHolder extends ItemHolder {

    /**
     * Gets the direction of where the holder would throw an equipment item.
     *
     * @return the throwing direction
     */
    @NotNull
    Location getThrowingDirection();
}
