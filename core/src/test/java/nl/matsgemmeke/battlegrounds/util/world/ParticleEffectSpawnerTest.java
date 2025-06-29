package nl.matsgemmeke.battlegrounds.util.world;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class ParticleEffectSpawnerTest {

    private static final Particle PARTICLE = Particle.FLAME;
    private static final int COUNT = 1;
    private static final double OFFSET_X = 0.1;
    private static final double OFFSET_Y = 0.2;
    private static final double OFFSET_Z = 0.3;
    private static final double EXTRA = 0.0;

    @Test
    public void spawnParticleEffectSpawnsParticleEffectWithBlockData() {
        BlockData blockData = mock(BlockData.class);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
        bukkit.when(() -> Bukkit.createBlockData(Material.STONE)).thenReturn(blockData);

        Particle particle = Particle.BLOCK_CRACK;
        Material blockDataMaterial = Material.STONE;
        ParticleEffect particleEffect = new ParticleEffect(particle, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, blockDataMaterial, null);

        ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
        particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);

        bukkit.close();

        verify(world).spawnParticle(particle, location, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, blockData);
    }

    @Test
    public void spawnParticleEffectSpawnsParticleEffectWithDustOptions() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        DustOptions dustOptions = new DustOptions(Color.RED, 1);
        ParticleEffect particleEffect = new ParticleEffect(PARTICLE, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, null, dustOptions);

        ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
        particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);

        verify(world).spawnParticle(PARTICLE, location, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, dustOptions);
    }

    @Test
    public void spawnParticleEffectSpawnsParticleEffectWithoutAdditionalData() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        ParticleEffect particleEffect = new ParticleEffect(PARTICLE, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, null, null);

        ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
        particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);

        verify(world).spawnParticle(PARTICLE, location, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA);
    }
}
