package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    private double currentRadius;
    @NotNull
    private List<Block> changedBlocks;
    private long duration;
    @NotNull
    private MetadataValueEditor metadataValueEditor;
    @NotNull
    private Random random;
    @NotNull
    private RangeProfile rangeProfile;
    @NotNull
    private TargetFinder targetFinder;
    @NotNull
    private TaskRunner taskRunner;

    @Inject
    public CombustionEffect(
            @NotNull MetadataValueEditor metadataValueEditor,
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull CombustionProperties properties,
            @Assisted @NotNull RangeProfile rangeProfile,
            @Assisted @NotNull AudioEmitter audioEmitter,
            @Assisted @NotNull CollisionDetector collisionDetector,
            @Assisted @NotNull TargetFinder targetFinder
    ) {
        this.properties = properties;
        this.rangeProfile = rangeProfile;
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.metadataValueEditor = metadataValueEditor;
        this.targetFinder = targetFinder;
        this.taskRunner = taskRunner;
        this.changedBlocks = new ArrayList<>();
        this.currentRadius = 0;
        this.random = new Random();
    }

    public void perform(@NotNull ItemEffectContext context) {
        ItemEffectSource source = context.getSource();
        Location location = source.getLocation();
        World world = source.getWorld();

        audioEmitter.playSounds(properties.combustionSounds(), location);

        currentRadius = properties.minSize();
        duration = this.getRandomDuration(properties.minDuration(), properties.maxDuration());

        this.inflictDamage(context.getEntity().getUniqueId(), location);

        task = taskRunner.runTaskTimer(() -> {
            currentRadius += properties.growth();

            if (currentRadius > properties.maxSize()) {
                currentRadius = 0;
                task.cancel();
                return;
            }

            for (Block block : this.getBlocksInRadius(location, world, (int) currentRadius)) {
                if (block.getType() == Material.AIR && collisionDetector.hasLineOfSight(block.getLocation(), location)) {
                    this.setOnFire(block);
                }
            }
        }, RUNNABLE_DELAY, properties.growthInterval());

        taskRunner.runTaskLater(this::reset, duration);

        source.remove();
    }

    private long getRandomDuration(long minDuration, long maxDuration) {
        if (minDuration >= maxDuration) {
            return minDuration;
        } else {
            return random.nextLong(minDuration, maxDuration);
        }
    }

    private void inflictDamage(@NotNull UUID entityId, @NotNull Location location) {
        for (GameEntity target : targetFinder.findTargets(entityId, location, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getLocation();

            double distance = location.distance(targetLocation);
            double damageAmount = rangeProfile.getDamageByDistance(distance);
            Damage damage = new Damage(damageAmount, DamageType.FIRE_DAMAGE);

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
        metadataValueEditor.addFixedMetadataValue(block, BURN_BLOCKS_METADATA_KEY, properties.burnBlocks());
        metadataValueEditor.addFixedMetadataValue(block, SPREAD_FIRE_METADATA_KEY, properties.spreadFire());

        block.setType(Material.FIRE);

        changedBlocks.add(block);
    }

    public void reset() {
        for (Block block : changedBlocks) {
            if (block.getType() == Material.FIRE) {
                block.setType(Material.AIR);
            }

            metadataValueEditor.removeMetadata(block, BURN_BLOCKS_METADATA_KEY);
            metadataValueEditor.removeMetadata(block, SPREAD_FIRE_METADATA_KEY);
        }
    }
}
