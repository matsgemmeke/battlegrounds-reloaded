package nl.matsgemmeke.battlegrounds.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlockCollisionChecker {

    private static final Map<Material,Double> blockHeights = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            if (material.name().endsWith("_WALL")) {
                blockHeights.put(material, 1.5);
            }
            if (material.name().endsWith("_FENCE_GATE") || material.name().endsWith("_FENCE")) {
                blockHeights.put(material, 1.5);
            }
            if (material.name().endsWith("_BED")) {
                blockHeights.put(material, 0.5);
            }
            if (material.name().endsWith("_SLAB")|| material.name().endsWith("_FENCE")) {
                blockHeights.put(material, 0.5);
            }
            if (material.name().endsWith("DAYLIGHT_DETECTOR")) {
                blockHeights.put(material, 0.4);
            }
            if (material.name().endsWith("CARPET")) {
                blockHeights.put(material, 0.1);
            }
            if (material.name().endsWith("TRAPDOOR")) {
                blockHeights.put(material, 0.2);
            }
        }
    }

    public double getHeight(@NotNull Block block) {
        Material type = block.getType();

        Tag tag = Tag.SLABS;

        if (Tag.SLABS.isTagged(type)) {
            // If the slab is the bottom one
            return ((Slab) block.getBlockData()).getType() == Type.TOP ? 1.0 : 0.5;
        }

        if (blockHeights.containsKey(type)) {
            return blockHeights.get(type);
        }

        return type.isSolid() ? 1.0 : 0.0;
    }

    public boolean isSolid(@NotNull Block block, @NotNull Location location) {
        Material type = block.getType();

        if (Tag.SNOW.isTagged(type)) {
            return false;
        }
        if (Tag.SIGNS.isTagged(type)) {
            return false;
        }
        if (Tag.CARPETS.isTagged(type)) {
            return false;
        }
        if (type == Material.WATER) {
            return true;
        }
        if (Tag.SLABS.isTagged(type)) {
            Type slabType = ((Slab) block.getBlockData()).getType();
            return location.getY() - location.getBlockY() > 0.5 && slabType == Type.TOP || location.getY() - location.getBlockY() <= 0.5 && slabType == Type.BOTTOM;
        }
        if (Tag.BEDS.isTagged(type)) {
            return location.getY() - location.getBlockY() > 0.5;
        }
        if (Tag.DOORS.isTagged(type)) {
            return true;
        }
        if (type.name().contains("GLASS")) {
            return true;
        }
        if (Tag.STAIRS.isTagged(type)) {
            Stairs stairs = (Stairs) block.getBlockData();
            BlockFace blockFace = stairs.getFacing();
            Half half = stairs.getHalf();

            if (half == Half.BOTTOM && (location.getY() - location.getBlockY() < 0.5)) {
                return true;
            }
            if (half == Half.TOP && (location.getY() - location.getBlockY() > 0.5)) {
                return true;
            }
            if (blockFace == BlockFace.NORTH) {
                return location.getZ() - location.getBlockZ() < 0.5;
            }
            if (blockFace == BlockFace.EAST) {
                return location.getX() - location.getBlockX() > 0.5;
            }
            if (blockFace == BlockFace.SOUTH) {
                return location.getZ() - location.getBlockZ() > 0.5;
            }
            if (blockFace == BlockFace.WEST) {
                return location.getX() - location.getBlockX() < 0.5;
            }
        }

        return type.isOccluding();
    }
}
