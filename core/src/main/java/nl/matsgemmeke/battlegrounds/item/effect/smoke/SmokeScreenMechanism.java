package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.effect.ParticleSettings;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SmokeScreenMechanism implements ItemMechanism {

    private static final long RUNNABLE_DELAY = 0L;
    private static final long RUNNABLE_PERIOD = 1L;

    @NotNull
    private AudioEmitter audioEmitter;
    private BukkitTask task;
    private int currentSize;
    @NotNull
    private ParticleSettings particleSettings;
    @NotNull
    private SmokeScreenSettings smokeScreenSettings;
    @NotNull
    private TaskRunner taskRunner;

    public SmokeScreenMechanism(
            @NotNull SmokeScreenSettings smokeScreenSettings,
            @NotNull ParticleSettings particleSettings,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner
    ) {
        this.smokeScreenSettings = smokeScreenSettings;
        this.particleSettings = particleSettings;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.currentSize = 0;
    }

    public void activate(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        holder.removeItem(itemStack);

        this.activate(holder.getLocation(), holder.getWorld());
    }

    public void activate(@NotNull ItemHolder holder, @NotNull Deployable object) {
        object.remove();

        this.activate(object.getLocation(), object.getWorld());
    }

    private void activate(@NotNull Location location, @NotNull World world) {
        audioEmitter.playSounds(smokeScreenSettings.ignitionSounds(), location);

        task = taskRunner.runTaskTimer(() -> {
            if (++currentSize > smokeScreenSettings.size()) {
                task.cancel();
                return;
            }
            this.createSphere(location, world, currentSize);
        }, RUNNABLE_DELAY, RUNNABLE_PERIOD);
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
