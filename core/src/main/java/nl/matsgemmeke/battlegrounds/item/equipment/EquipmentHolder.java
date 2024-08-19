package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.holder.ItemThrower;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EquipmentHolder extends ItemHolder, ItemThrower {

    /**
     * Gets the last two blocks in holder's line of sight.
     *
     * @param maxDistance the distance to scan
     * @return the last two target blocks of the holder
     */
    @NotNull
    List<Block> getLastTwoTargetBlocks(int maxDistance);

    /**
     * Gets the direction of where the holder would throw an equipment item.
     *
     * @return the throwing direction
     */
    @NotNull
    Location getThrowingDirection();
}
