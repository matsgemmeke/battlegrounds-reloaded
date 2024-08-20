package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.jetbrains.annotations.NotNull;

public class PlantableObject implements DeployableObject {

    @NotNull
    private Block block;
    @NotNull
    private BlockFace blockFace;
    @NotNull
    private Material material;

    public PlantableObject(@NotNull Block block, @NotNull BlockFace blockFace, @NotNull Material material) {
        this.block = block;
        this.blockFace = blockFace;
        this.material = material;
    }

    @NotNull
    public Location getLocation() {
        return block.getLocation();
    }

    @NotNull
    public World getWorld() {
        return block.getWorld();
    }

    public void plant() {
        block.setType(material);

        AttachedFace attachedFace;

        switch (blockFace) {
            case UP -> {
                attachedFace = AttachedFace.FLOOR;
            }
            case DOWN -> {
                attachedFace = AttachedFace.CEILING;
            }
            default -> {
                attachedFace = AttachedFace.WALL;
            }
        }

        BlockState plantBlockState = block.getState();

        FaceAttachable faceAttachable = (FaceAttachable) block.getBlockData();
        faceAttachable.setAttachedFace(attachedFace);

        plantBlockState.setBlockData(faceAttachable);

        if (attachedFace == AttachedFace.WALL) {
            Directional directional = (Directional) block.getBlockData();
            directional.setFacing(blockFace);

            plantBlockState.setBlockData(directional);
        }

        plantBlockState.update(true, true);
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
