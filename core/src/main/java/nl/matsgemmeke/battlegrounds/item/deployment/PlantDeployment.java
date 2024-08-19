package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.jetbrains.annotations.NotNull;

/**
 * Method of item deployment where the item gets planted against a block.
 */
public class PlantDeployment {

    @NotNull
    private Material material;

    public PlantDeployment(@NotNull Material material) {
        this.material = material;
    }

    public void plant(@NotNull Block block, @NotNull BlockFace blockFace) {
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
}
