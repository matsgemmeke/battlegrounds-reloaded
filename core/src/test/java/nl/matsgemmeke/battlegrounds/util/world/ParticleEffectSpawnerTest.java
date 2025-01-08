package nl.matsgemmeke.battlegrounds.util.world;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class ParticleEffectSpawnerTest {

    @Test
    public void spawnParticleEffectSpawnsParticleWithBlockData() {
        BlockData blockData = mock(BlockData.class);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.createBlockData(Material.STONE)).thenReturn(blockData);

        Particle particle = Particle.BLOCK_CRACK;
        int count = 1;
        double offsetX = 0.1;
        double offsetY = 0.2;
        double offsetZ = 0.3;
        double extra = 0.0;
        Material blockDataMaterial = Material.STONE;
        ParticleEffect particleEffect = new ParticleEffect(particle, count, offsetX, offsetY, offsetZ, extra, blockDataMaterial);

        ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
        particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);

        bukkit.close();

        verify(world).spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, blockData);
    }

    @Test
    public void spawnParticleEffectSpawnsParticleWithoutBlockData() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        Particle particle = Particle.FLAME;
        int count = 1;
        double offsetX = 0.1;
        double offsetY = 0.2;
        double offsetZ = 0.3;
        double extra = 0.0;
        ParticleEffect particleEffect = new ParticleEffect(particle, count, offsetX, offsetY, offsetZ, extra, null);

        ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
        particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);

        verify(world).spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }
}
