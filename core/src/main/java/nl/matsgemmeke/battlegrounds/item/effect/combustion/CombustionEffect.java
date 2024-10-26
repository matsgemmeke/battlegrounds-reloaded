package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemMechanism;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombustionEffect implements ItemMechanism {

    private static final long RUNNABLE_DELAY = 0L;
    private static final String BURN_BLOCKS_METADATA_KEY = "battlegrounds-burn-blocks";
    private static final String SPREAD_FIRE_METADATA_KEY = "battlegrounds-spread-fire";

    @NotNull
    private AudioEmitter audioEmitter;
    @Nullable
    private BukkitTask task;
    @NotNull
    private CombustionSettings settings;
    private int currentRadius;
    @NotNull
    private MetadataValueCreator metadataValueCreator;
    @NotNull
    private RangeProfile rangeProfile;
    @NotNull
    private TargetFinder targetFinder;
    @NotNull
    private TaskRunner taskRunner;

    public CombustionEffect(
            @NotNull CombustionSettings settings,
            @NotNull RangeProfile rangeProfile,
            @NotNull AudioEmitter audioEmitter,
            @NotNull MetadataValueCreator metadataValueCreator,
            @NotNull TargetFinder targetFinder,
            @NotNull TaskRunner taskRunner
    ) {
        this.settings = settings;
        this.rangeProfile = rangeProfile;
        this.audioEmitter = audioEmitter;
        this.metadataValueCreator = metadataValueCreator;
        this.targetFinder = targetFinder;
        this.taskRunner = taskRunner;
        this.currentRadius = 0;
    }

    public void activate(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        holder.removeItem(itemStack);

        this.activate(holder, holder.getLocation(), holder.getWorld());
    }

    public void activate(@NotNull ItemHolder holder, @NotNull Deployable object) {
        object.remove();

        this.activate(holder, object.getLocation(), object.getWorld());
    }

    private void activate(@NotNull ItemHolder holder, @NotNull Location location, @NotNull World world) {
        audioEmitter.playSounds(settings.sounds(), location);

        this.inflictDamage(holder, location);

        int maxRadiusSize = settings.radius();

        task = taskRunner.runTaskTimer(() -> {
            if (++currentRadius > maxRadiusSize) {
                currentRadius = 0;
                task.cancel();
                return;
            }

            for (Block block : this.getBlocksInRadius(location, world, currentRadius)) {
                if (block.getType() == Material.AIR && hasLineOfSight(world, block.getLocation(), location)) {
                    this.setOnFire(block);
                }
            }
        }, RUNNABLE_DELAY, settings.ticksBetweenFireSpread());
    }

    private void inflictDamage(@NotNull ItemHolder holder, @NotNull Location location) {
        for (GameEntity target : targetFinder.findTargets(holder, location, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getLocation();

            double distance = location.distance(targetLocation);
            double damage = rangeProfile.getDamageByDistance(distance);

            target.damage(damage);
        }
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
        MetadataValue burnBlocksMetadata = metadataValueCreator.createFixedMetadataValue(settings.burnBlocks());
        MetadataValue spreadFireMetadata = metadataValueCreator.createFixedMetadataValue(settings.spreadFire());

        block.setMetadata(BURN_BLOCKS_METADATA_KEY, burnBlocksMetadata);
        block.setMetadata(SPREAD_FIRE_METADATA_KEY, spreadFireMetadata);
        block.setType(Material.FIRE);
    }
}
