package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ParticleSettings;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmokeScreenEffect implements ItemEffect {

    private static final long RUNNABLE_DELAY = 0L;
    private static final long RUNNABLE_PERIOD = 1L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    private int currentDuration;
    private int currentSphereSize;
    @Nullable
    private Location previousLocation;
    @NotNull
    private ParticleSettings particleSettings;
    @NotNull
    private SmokeScreenSettings smokeScreenSettings;
    @NotNull
    private TaskRunner taskRunner;

    public SmokeScreenEffect(
            @NotNull SmokeScreenSettings smokeScreenSettings,
            @NotNull ParticleSettings particleSettings,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner
    ) {
        this.smokeScreenSettings = smokeScreenSettings;
        this.particleSettings = particleSettings;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.currentDuration = 0;
    }

    public void activate(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        audioEmitter.playSounds(smokeScreenSettings.ignitionSounds(), source.getLocation());

        task = taskRunner.runTaskTimer(() -> {
            if (++currentDuration >= smokeScreenSettings.duration()) {
                task.cancel();
                return;
            }
            this.createSmokeEffect(source.getLocation(), source.getWorld());
        }, RUNNABLE_DELAY, RUNNABLE_PERIOD);
    }

    private void createSmokeEffect(@NotNull Location location, @NotNull World world) {
        boolean moving = !location.equals(previousLocation);

        if (moving) {
            this.spawnParticle(location, world);
            // If the source moved location the smoke will no longer expand into a sphere, so it resets the size
            currentSphereSize = 0;
            previousLocation = location;
        } else {
            this.createSphere(location, world, ++currentSphereSize);
        }
    }

    private void createSphere(@NotNull Location location, @NotNull World world, int size) {
        for (Location sphereLocation : this.getSphereLocations(location, world, size) ) {
            this.spawnParticle(sphereLocation, world);
        }
    }

    @NotNull
    private List<Location> getSphereLocations(@NotNull Location location, @NotNull World world, int radius) {
        List<Location> locations = new ArrayList<>();

        // Center x, y, z coordinates
        int cx = location.getBlockX();
        int cy = location.getBlockY();
        int cz = location.getBlockZ();
        int radiusSquared = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSquared) {
                        locations.add(new Location(world, cx + x, cy + y, cz + z));
                    }
                }
            }
        }

        return locations;
    }

    private void spawnParticle(@NotNull Location location, @NotNull World world) {
        Particle particle = particleSettings.type();
        int count = particleSettings.count();
        double offsetX = particleSettings.offsetX();
        double offsetY = particleSettings.offsetY();
        double offsetZ = particleSettings.offsetZ();
        double extra = particleSettings.extra();

        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ ,extra);
    }
}
