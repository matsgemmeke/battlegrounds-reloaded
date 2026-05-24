package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class CombustionEffectPerformance extends BaseItemEffectPerformance {

    private static final long SCHEDULE_DELAY = 0L;
    private static final String BURN_BLOCKS_METADATA_KEY = "burn-blocks";
    private static final String SPREAD_FIRE_METADATA_KEY = "spread-fire";

    private final AudioEmitter audioEmitter;
    private final CollisionDetector collisionDetector;
    private final CombustionProperties properties;
    private final DamageProcessor damageProcessor;
    private final MetadataValueEditor metadataValueEditor;
    private final Random random;
    private final Scheduler scheduler;
    private final Set<Block> affectedBlocks;
    private final TargetFinder targetFinder;
    private double currentRadius;
    private Schedule schedule;

    @Inject
    public CombustionEffectPerformance(
            AudioEmitter audioEmitter,
            CollisionDetector collisionDetector,
            DamageProcessor damageProcessor,
            MetadataValueEditor metadataValueEditor,
            Scheduler scheduler,
            TargetFinder targetFinder,
            @Assisted CombustionProperties properties
    ) {
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.damageProcessor = damageProcessor;
        this.metadataValueEditor = metadataValueEditor;
        this.scheduler = scheduler;
        this.targetFinder = targetFinder;
        this.properties = properties;
        this.random = new Random();
        this.affectedBlocks = new HashSet<>();
    }

    @Override
    public boolean isPerforming() {
        return schedule != null && schedule.isRunning();
    }

    @Override
    public void start() {
        DamageSource damageSource = currentContext.getDamageSource();
        Actor actor = currentContext.getActor();
        Location actorLocation = actor.getLocation();
        World world = actor.getWorld();

        audioEmitter.playSounds(properties.combustionSounds(), actorLocation);

        currentRadius = properties.minSize();

        this.inflictDamage(damageSource, actorLocation);

        schedule = scheduler.createRepeatingSchedule(SCHEDULE_DELAY, properties.growthInterval());
        schedule.addTask(() -> this.increaseFireCircleRadius(actorLocation, world));
        schedule.start();

        long duration = this.getRandomDuration(properties.minDuration(), properties.maxDuration());

        Schedule cancelSchedule = scheduler.createSingleRunSchedule(duration);
        cancelSchedule.addTask(this::rollback);
        cancelSchedule.start();

        if (actor instanceof Removable removableActor) {
            removableActor.remove();
        }
    }

    private void inflictDamage(DamageSource damageSource, Location actorLocation) {
        UUID damageSourceId = damageSource.getUniqueId();
        RangeProfile rangeProfile = properties.rangeProfile();
        double damageRange = rangeProfile.longRangeDistance();

        for (GameEntity target : targetFinder.findTargets(damageSourceId, actorLocation, damageRange)) {
            Location targetLocation = target.getLocation();

            double distance = actorLocation.distance(targetLocation);
            double damageAmount = rangeProfile.getDamageByDistance(distance);
            Damage damage = new Damage(damageAmount, DamageType.FIRE_DAMAGE, HitboxComponentType.TORSO);

            DamageContext damageContext = new DamageContext(damageSource, target, damage, distance);

            damageProcessor.processDamage(damageContext);
        }
    }

    private void increaseFireCircleRadius(Location location, World world) {
        currentRadius += properties.growth();

        if (currentRadius > properties.maxSize()) {
            currentRadius = 0;
            schedule.stop();
            return;
        }

        for (Block block : this.getBlocksInRadius(location, world, (int) currentRadius)) {
            if (block.getType() == Material.AIR && collisionDetector.hasLineOfSight(block.getLocation(), location)) {
                this.setOnFire(block);
                affectedBlocks.add(block);
            }
        }
    }

    private List<Block> getBlocksInRadius(Location location, World world, int radius) {
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

    private void setOnFire(Block block) {
        metadataValueEditor.addFixedMetadataValue(block, BURN_BLOCKS_METADATA_KEY, properties.burnBlocks());
        metadataValueEditor.addFixedMetadataValue(block, SPREAD_FIRE_METADATA_KEY, properties.spreadFire());

        block.setType(Material.FIRE);
    }

    private long getRandomDuration(long minDuration, long maxDuration) {
        if (minDuration >= maxDuration) {
            return minDuration;
        } else {
            return random.nextLong(minDuration, maxDuration);
        }
    }

    @Override
    public void rollback() {
        for (Block block : affectedBlocks) {
            if (block.getType() == Material.FIRE) {
                block.setType(Material.AIR);
            }

            metadataValueEditor.removeMetadata(block, BURN_BLOCKS_METADATA_KEY);
            metadataValueEditor.removeMetadata(block, SPREAD_FIRE_METADATA_KEY);
        }

        schedule.stop();
    }
}
