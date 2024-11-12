package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * A deployed object in the form as a placed {@link Block}.
 */
public class PlacedBlock implements EffectSource {

    @NotNull
    private Block block;
    @NotNull
    private Material material;

    public PlacedBlock(@NotNull Block block, @NotNull Material material) {
        this.block = block;
        this.material = material;
    }

    public boolean exists() {
        return block.getType() == material;
    }

    @NotNull
    public Location getLocation() {
        return block.getLocation();
    }

    @NotNull
    public World getWorld() {
        return block.getWorld();
    }

    public boolean isDeployed() {
        return true;
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
