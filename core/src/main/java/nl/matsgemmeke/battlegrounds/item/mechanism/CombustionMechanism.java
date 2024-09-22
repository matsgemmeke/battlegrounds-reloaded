package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.MetadataValueCreator;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombustionMechanism implements ItemMechanism {

    private static final long RUNNABLE_DELAY = 0L;
    private static final String BURN_BLOCKS_METADATA_KEY = "battlegrounds-burn-blocks";
    private static final String SPREAD_FIRE_METADATA_KEY = "battlegrounds-spread-fire";

    private boolean burnBlocks;
    private boolean spreadFire;
    @Nullable
    private BukkitTask task;
    private int currentRadius;
    private int radius;
    private long ticksBetweenSpread;
    @NotNull
    private MetadataValueCreator metadataValueCreator;
    @NotNull
    private TaskRunner taskRunner;

    public CombustionMechanism(
            @NotNull MetadataValueCreator metadataValueCreator,
            @NotNull TaskRunner taskRunner,
            int radius,
            long ticksBetweenSpread,
            boolean burnBlocks,
            boolean spreadFire
    ) {
        this.metadataValueCreator = metadataValueCreator;
        this.taskRunner = taskRunner;
        this.radius = radius;
        this.ticksBetweenSpread = ticksBetweenSpread;
        this.burnBlocks = burnBlocks;
        this.spreadFire = spreadFire;
        this.currentRadius = 0;
    }

    public void activate(@NotNull ItemHolder holder) {
        this.activate(holder.getLocation(), holder.getWorld());
    }

    public void activate(@NotNull ItemHolder holder, @NotNull Deployable object) {
        object.remove();

        this.activate(object.getLocation(), object.getWorld());
    }

    private void activate(@NotNull Location location, @NotNull World world) {
        int maxRadiusSize = radius;

        task = taskRunner.runTaskTimer(() -> {
            if (++currentRadius > maxRadiusSize) {
                currentRadius = 0;
                task.cancel();
                return;
            }

            for (Block block : getBlocksInRadius(location, world, currentRadius)) {
                if (block.getType() == Material.AIR && hasLineOfSight(world, block.getLocation(), location)) {
                    this.setOnFire(block);
                }
            }
        }, RUNNABLE_DELAY, ticksBetweenSpread);
    }

    @NotNull
    private List<Block> getBlocksInRadius(@NotNull Location location, @NotNull World world, int radius) {
        List<Block> blocks = new ArrayList<>();

        // Loop through a square bounding box around the location
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSquared = x * x + z * z;
                // Check if the block is within the circle's radius
                if (distanceSquared <= radius * radius) {
                    blocks.add(world.getBlockAt(location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z));
                }
            }
        }

        return blocks;
    }

    private boolean hasLineOfSight(@NotNull World world, @NotNull Location from, @NotNull Location to) {
        // Trace the blocks from the starting location to the destination location
        double distance = from.distance(to);

        for (int i = 0; i < distance; i++) {
            double t = i / distance;

            int x = (int) (from.getX() + t * (to.getX() - from.getX()));
            int y = (int) (from.getY() + t * (to.getY() - from.getY()));
            int z = (int) (from.getZ() + t * (to.getZ() - from.getZ()));

            Block block = world.getBlockAt(x, y, z);

            if (!block.isPassable()) {
                return false;
            }
        }
        return true;
    }

    private void setOnFire(@NotNull Block block) {
        MetadataValue burnBlocksMetadata = metadataValueCreator.createFixedMetadataValue(burnBlocks);
        MetadataValue spreadFireMetadata = metadataValueCreator.createFixedMetadataValue(spreadFire);

        block.setMetadata(BURN_BLOCKS_METADATA_KEY, burnBlocksMetadata);
        block.setMetadata(SPREAD_FIRE_METADATA_KEY, spreadFireMetadata);
        block.setType(Material.FIRE);
    }
}
