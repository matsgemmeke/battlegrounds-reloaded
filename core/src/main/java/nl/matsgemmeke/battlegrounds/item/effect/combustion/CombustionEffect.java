package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
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

public class CombustionEffect extends BaseItemEffect {

    private static final long RUNNABLE_DELAY = 0L;
    private static final String BURN_BLOCKS_METADATA_KEY = "battlegrounds-burn-blocks";
    private static final String SPREAD_FIRE_METADATA_KEY = "battlegrounds-spread-fire";

    @NotNull
    private AudioEmitter audioEmitter;
    @Nullable
    private BukkitTask task;
    @NotNull
    private CollisionDetector collisionDetector;
    @NotNull
    private CombustionProperties properties;
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
            @NotNull ItemEffectActivation effectActivation,
            @NotNull CombustionProperties properties,
            @NotNull RangeProfile rangeProfile,
            @NotNull AudioEmitter audioEmitter,
            @NotNull CollisionDetector collisionDetector,
            @NotNull MetadataValueCreator metadataValueCreator,
            @NotNull TargetFinder targetFinder,
            @NotNull TaskRunner taskRunner
    ) {
        super(effectActivation);
        this.properties = properties;
        this.rangeProfile = rangeProfile;
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.metadataValueCreator = metadataValueCreator;
        this.targetFinder = targetFinder;
        this.taskRunner = taskRunner;
        this.currentRadius = 0;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void perform(@NotNull ItemEffectContext context) {
        ItemHolder holder = context.getHolder();
        EffectSource source = context.getSource();
        Location location = source.getLocation();
        World world = source.getWorld();

        audioEmitter.playSounds(properties.combustionSounds(), location);

        this.inflictDamage(holder, location);

        int maxRadiusSize = properties.radius();

        task = taskRunner.runTaskTimer(() -> {
            if (++currentRadius > maxRadiusSize) {
                currentRadius = 0;
                task.cancel();
                return;
            }

            for (Block block : this.getBlocksInRadius(location, world, currentRadius)) {
                if (block.getType() == Material.AIR && collisionDetector.hasLineOfSight(block.getLocation(), location)) {
                    this.setOnFire(block);
                }
            }
        }, RUNNABLE_DELAY, properties.ticksBetweenFireSpread());

        source.remove();
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

    private void setOnFire(@NotNull Block block) {
        MetadataValue burnBlocksMetadata = metadataValueCreator.createFixedMetadataValue(properties.burnBlocks());
        MetadataValue spreadFireMetadata = metadataValueCreator.createFixedMetadataValue(properties.spreadFire());

        block.setMetadata(BURN_BLOCKS_METADATA_KEY, burnBlocksMetadata);
        block.setMetadata(SPREAD_FIRE_METADATA_KEY, spreadFireMetadata);
        block.setType(Material.FIRE);
    }
}
