package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

import java.util.Optional;
import java.util.Set;

public final class HitboxUtil {

    public static Hitbox createHitbox(StaticBoundingBox boundingBox) {
        HitboxComponent component = new HitboxComponent(HitboxComponentType.TORSO, boundingBox.widthX(), boundingBox.height(), boundingBox.widthZ(), 0, 0, 0);
        RelativeHitbox relativeHitbox = new RelativeHitbox(Set.of(component));

        return new Hitbox(boundingBox.baseLocation(), relativeHitbox);
    }

    public static Optional<Location> getBedBaseLocation(Location baseLocation) {
        BlockData blockData = baseLocation.getBlock().getBlockData();

        if (!(blockData instanceof Bed bed)) {
            return Optional.empty();
        }

        BlockFace blockFace = bed.getFacing().getOppositeFace();
        float yaw = convertBedBlockFaceToYaw(blockFace);

        return Optional.of(new Location(baseLocation.getWorld(), baseLocation.getX(), baseLocation.getY(), baseLocation.getZ(), yaw, baseLocation.getPitch()));
    }

    private static float convertBedBlockFaceToYaw(BlockFace face) {
        return switch (face) {
            case NORTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
        };
    }
}
