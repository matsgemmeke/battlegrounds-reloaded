package nl.matsgemmeke.battlegrounds.util.world;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class ParticleEffectSpawner {

    /**
     * Spawns a particle in the given world at the given location based on the given particle effect. Is it required
     * that the given location has a world reference.
     *
     * @param particleEffect the particle effect
     * @param location the location to spawn the particle at
     * @throws IllegalArgumentException when the given location's world is null
     */
    public void spawnParticleEffect(@NotNull ParticleEffect particleEffect, @NotNull Location location) {
        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Cannot display particle at location %s, its world is null".formatted(location));
        }

        Particle particle = particleEffect.particle();
        int count = particleEffect.count();
        double offsetX = particleEffect.offsetX();
        double offsetY = particleEffect.offsetY();
        double offsetZ = particleEffect.offsetZ();
        double extra = particleEffect.extra();

        Material material = particleEffect.blockDataMaterial();
        DustOptions dustOptions = particleEffect.dustOptions();

        if (material != null) {
            BlockData blockData = material.createBlockData();
            world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, blockData);
        } else if (dustOptions != null) {
            world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, dustOptions);
        } else {
            world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
        }
    }
}
