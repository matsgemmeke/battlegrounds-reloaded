package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockTriggerTarget implements TriggerTarget {

    private static final double BLOCK_CENTER_OFFSET = 0.5;
    private static final Vector ZERO = new Vector();

    private final Block block;
    private final Material material;

    public BlockTriggerTarget(Block block, Material material) {
        this.block = block;
        this.material = material;
    }

    @Override
    public boolean exists() {
        return block.getType() == material;
    }

    @Override
    public Location getLocation() {
        return block.getLocation().add(BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET);
    }

    @Override
    public Vector getVelocity() {
        // A block trigger target never moves
        return ZERO;
    }

    @Override
    public World getWorld() {
        return block.getWorld();
    }
}
