package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SmokeScreenEffectPerformance extends BaseItemEffectPerformance {

    private static final long SCHEDULER_DELAY = 0L;

    private final AudioEmitter audioEmitter;
    private final CollisionDetector collisionDetector;
    private final Random random;
    private final Scheduler scheduler;
    private final SmokeScreenProperties properties;
    private double currentRadius;
    private Location currentLocation;
    private Schedule repeatingSchedule;

    @Inject
    public SmokeScreenEffectPerformance(AudioEmitter audioEmitter, CollisionDetector collisionDetector, Scheduler scheduler, @Assisted SmokeScreenProperties properties) {
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.scheduler = scheduler;
        this.properties = properties;
        this.random = new Random();
    }

    @Override
    public boolean isPerforming() {
        return repeatingSchedule != null && repeatingSchedule.isRunning();
    }

    @Override
    public void perform(@NotNull ItemEffectContext context) {
        audioEmitter.playSounds(properties.activationSounds(), context.getSource().getLocation());

        currentLocation = context.getSource().getLocation();
        currentRadius = properties.minSize();

        long totalDuration = this.getTotalDuration(properties.minDuration(), properties.maxDuration());

        repeatingSchedule = scheduler.createRepeatingSchedule(SCHEDULER_DELAY, properties.growthInterval());
        repeatingSchedule.addTask(() -> this.handleGrowth(context));
        repeatingSchedule.start();

        Schedule cancelSchedule = scheduler.createSingleRunSchedule(totalDuration);
        cancelSchedule.addTask(() -> this.expire(context));
        cancelSchedule.start();
    }

    private void handleGrowth(ItemEffectContext context) {
        ItemEffectSource source = context.getSource();

        if (!source.exists()) {
            repeatingSchedule.stop();
            return;
        }

        this.createSmokeEffect(source.getLocation(), source.getWorld());
    }

    private long getTotalDuration(long minDuration, long maxDuration) {
        if (minDuration >= maxDuration) {
            return minDuration;
        } else {
            return random.nextLong(properties.minDuration(), properties.maxDuration());
        }
    }

    private void createSmokeEffect(@NotNull Location location, @NotNull World world) {
        boolean moving = this.hasMovedFromCurrentLocation(location);

        if (moving) {
            this.spawnParticle(location, world);
            // If the source moved location the smoke will no longer expand into a sphere, so it resets the size
            currentRadius = properties.minSize();
            currentLocation = location;
        } else {
            currentRadius += properties.growth();
            double radius = Math.min(currentRadius, properties.maxSize());

            this.createSphere(location, world, radius);
        }
    }

    private boolean hasMovedFromCurrentLocation(@NotNull Location location) {
        return location.getX() != currentLocation.getX()
                || location.getY() != currentLocation.getY()
                || location.getZ() != currentLocation.getZ();
    }

    private void spawnParticle(@NotNull Location location, @NotNull World world) {
        Particle particle = properties.particleEffect().particle();
        int count = properties.particleEffect().count();
        double offsetX = properties.particleEffect().offsetX();
        double offsetY = properties.particleEffect().offsetY();
        double offsetZ = properties.particleEffect().offsetZ();
        double extra = properties.particleEffect().extra();

        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }

    private void createSphere(@NotNull Location location, @NotNull World world, double radius) {
        // Increase the amount of particles based on the radius of the sphere
        int particleAmount = (int) (radius * properties.density());

        for (int i = 0; i < particleAmount; i++) {
            // Generate a random distance within the current radius
            double distance = radius * Math.cbrt(random.nextDouble());

            // Random angles for spherical coordinates
            double theta = 2 * Math.PI * random.nextDouble();
            double phi = Math.acos(2 * random.nextDouble() - 1);

            // Convert spherical coordinates to Cartesian coordinates
            double x = distance * Math.sin(phi) * Math.cos(theta);
            double y = distance * Math.cos(phi);
            double z = distance * Math.sin(phi) * Math.sin(theta);

            Location particleLocation = location.clone().add(x, y, z);
            double offsetX = (particleLocation.getX() - location.getX()) * random.nextDouble();
            double offsetY = (particleLocation.getY() - location.getY()) * random.nextDouble();
            double offsetZ = (particleLocation.getZ() - location.getZ()) * random.nextDouble();

            // Spawn particle at calculated location
            if (!collisionDetector.producesBlockCollisionAt(particleLocation) && collisionDetector.hasLineOfSight(particleLocation, location)) {
                Particle particle = properties.particleEffect().particle();
                double extra = properties.particleEffect().extra();
                Material data = properties.particleEffect().blockDataMaterial();

                world.spawnParticle(particle, particleLocation, 0, offsetX, offsetY, offsetZ, extra, data, true);
            }
        }
    }

    private void expire(ItemEffectContext context) {
        this.rollback();

        if (context.getSource() instanceof Removable removableSource) {
            removableSource.remove();
        }
    }

    @Override
    public void rollback() {
        if (!this.isPerforming()) {
            return;
        }

        repeatingSchedule.stop();
    }
}
