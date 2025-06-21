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
     * Spawns a particle in the given world at the given location based on the given particle effect.
     *
     * @param particleEffect the particle effect
     * @param world the world to spawn the particle in
     * @param location the location to spawn the particle at
     */
    public void spawnParticleEffect(@NotNull ParticleEffect particleEffect, @NotNull World world, @NotNull Location location) {
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
