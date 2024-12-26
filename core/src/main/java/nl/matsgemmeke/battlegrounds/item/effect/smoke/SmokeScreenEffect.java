package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SmokeScreenEffect implements ItemEffect {

    private static final long RUNNABLE_DELAY = 0L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    @NotNull
    private CollisionDetector collisionDetector;
    private double currentRadius;
    private int currentDuration;
    private Location currentLocation;
    @NotNull
    private Random random;
    @NotNull
    private SmokeScreenProperties properties;
    @NotNull
    private TaskRunner taskRunner;

    public SmokeScreenEffect(
            @NotNull SmokeScreenProperties properties,
            @NotNull AudioEmitter audioEmitter,
            @NotNull CollisionDetector collisionDetector,
            @NotNull TaskRunner taskRunner
    ) {
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.collisionDetector = collisionDetector;
        this.taskRunner = taskRunner;
        this.currentDuration = 0;
        this.random = new Random();
    }

    public void activate(@NotNull ItemEffectContext context) {
        audioEmitter.playSounds(properties.ignitionSounds(), context.getSource().getLocation());

        currentDuration = 0;
        currentLocation = context.getSource().getLocation();
        currentRadius = properties.radiusStartingSize();

        EffectSource source = context.getSource();

        task = taskRunner.runTaskTimer(() -> {
            if (!source.exists()) {
                task.cancel();
                return;
            }

            if (++currentDuration >= properties.duration()) {
                source.remove();
                task.cancel();
                return;
            }

            this.createSmokeEffect(source.getLocation(), source.getWorld());
        }, RUNNABLE_DELAY, properties.growthPeriod());
    }

    private void createSmokeEffect(@NotNull Location location, @NotNull World world) {
        boolean moving = this.hasMovedFromCurrentLocation(location);

        if (moving) {
            this.spawnParticle(location, world);
            // If the source moved location the smoke will no longer expand into a sphere, so it resets the size
            currentRadius = properties.radiusStartingSize();
            currentLocation = location;
        } else {
            currentRadius += properties.growthIncrease();
            double radius = Math.min(currentRadius, properties.radiusMaxSize());

            this.createSphere(location, world, radius);
        }
    }

    private boolean hasMovedFromCurrentLocation(@NotNull Location location) {
        return location.getX() != currentLocation.getX()
                || location.getY() != currentLocation.getY()
                || location.getZ() != currentLocation.getZ();
    }

    private void spawnParticle(@NotNull Location location, @NotNull World world) {
        Particle particle = properties.particleEffect().type();
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
            if (!collisionDetector.producesBlockCollisionAt(particleLocation)
                    && collisionDetector.hasLineOfSight(particleLocation, location)) {
                Particle particle = properties.particleEffect().type();
                double extra = properties.particleEffect().extra();

                world.spawnParticle(particle, particleLocation, 0, offsetX, offsetY, offsetZ, extra);
            }
        }
    }
}
