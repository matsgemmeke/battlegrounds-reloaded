package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private CombustionEffectFactory combustionEffectFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    private ItemEffectActivation effectActivation;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        combustionEffectFactory = mock(CombustionEffectFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        gunFireSimulationEffectFactory = mock(GunFireSimulationEffectFactory.class);
        effectActivation = mock(ItemEffectActivation.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        int maxRadius = 3;
        long ticksBetweenSpread = 5L;
        long maxDuration = 600L;
        boolean burnBlocks = true;
        boolean spreadFire = false;

        double longRangeDamage = 10.0;
        double longRangeDistance = 1.5;
        double mediumRangeDamage = 20.0;
        double mediumRangeDistance = 1.0;
        double shortRangeDamage = 30.0;
        double shortRangeDistance = 0.5;

        when(section.getString("type")).thenReturn("COMBUSTION");
        when(section.getInt("max-radius")).thenReturn(maxRadius);
        when(section.getLong("ticks-between-spread")).thenReturn(ticksBetweenSpread);
        when(section.getLong("max-duration")).thenReturn(maxDuration);
        when(section.getBoolean("burn-blocks")).thenReturn(burnBlocks);
        when(section.getBoolean("spread-fire")).thenReturn(spreadFire);

        when(section.getDouble("range.long-range.damage")).thenReturn(longRangeDamage);
        when(section.getDouble("range.long-range.distance")).thenReturn(longRangeDistance);
        when(section.getDouble("range.medium-range.damage")).thenReturn(mediumRangeDamage);
        when(section.getDouble("range.medium-range.distance")).thenReturn(mediumRangeDistance);
        when(section.getDouble("range.short-range.damage")).thenReturn(shortRangeDamage);
        when(section.getDouble("range.short-range.distance")).thenReturn(shortRangeDistance);

        ItemEffect combustionEffect = mock(CombustionEffect.class);
        when(combustionEffectFactory.create(eq(effectActivation), any(CombustionProperties.class), any(RangeProfile.class), eq(audioEmitter), eq(collisionDetector), eq(targetFinder))).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        ArgumentCaptor<RangeProfile> rangeProfileCaptor = ArgumentCaptor.forClass(RangeProfile.class);

        verify(combustionEffectFactory).create(eq(effectActivation), propertiesCaptor.capture(), rangeProfileCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(targetFinder));

        CombustionProperties properties = propertiesCaptor.getValue();
        assertEquals(maxRadius, properties.maxRadius());
        assertEquals(ticksBetweenSpread, properties.ticksBetweenFireSpread());
        assertEquals(maxDuration, properties.maxDuration());
        assertEquals(burnBlocks, properties.burnBlocks());
        assertEquals(spreadFire, properties.spreadFire());

        RangeProfile rangeProfile = rangeProfileCaptor.getValue();
        assertEquals(longRangeDamage, rangeProfile.getLongRangeDamage());
        assertEquals(longRangeDistance, rangeProfile.getLongRangeDistance());
        assertEquals(mediumRangeDamage, rangeProfile.getMediumRangeDamage());
        assertEquals(mediumRangeDistance, rangeProfile.getMediumRangeDistance());
        assertEquals(shortRangeDamage, rangeProfile.getShortRangeDamage());
        assertEquals(shortRangeDistance, rangeProfile.getShortRangeDistance());

        assertEquals(combustionEffect, itemEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(ExplosionEffect.class, itemEffect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(FlashEffect.class, itemEffect);
    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        GunInfoProvider gunInfoProvider = mock(GunInfoProvider.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, GunInfoProvider.class)).thenReturn(gunInfoProvider);

        int genericRateOfFire = 600;
        int maxBurstDuration = 100;
        int minBurstDuration = 20;
        int maxDelayBetweenBursts = 60;
        int minDelayBetweenBursts = 60;
        int maxTotalDuration = 300;
        int minTotalDuration = 200;

        when(section.getString("type")).thenReturn("GUN_FIRE_SIMULATION");
        when(section.getInt("generic-rate-of-fire")).thenReturn(genericRateOfFire);
        when(section.getInt("max-burst-duration")).thenReturn(maxBurstDuration);
        when(section.getInt("min-burst-duration")).thenReturn(minBurstDuration);
        when(section.getInt("max-delay-between-bursts")).thenReturn(maxDelayBetweenBursts);
        when(section.getInt("min-delay-between-bursts")).thenReturn(minDelayBetweenBursts);
        when(section.getInt("max-total-duration")).thenReturn(maxTotalDuration);
        when(section.getInt("min-total-duration")).thenReturn(minTotalDuration);

        ItemEffect gunFireSimulationEffect = mock(GunFireSimulationEffect.class);
        when(gunFireSimulationEffectFactory.create(eq(effectActivation), eq(audioEmitter), eq(gunInfoProvider), any(GunFireSimulationProperties.class))).thenReturn(gunFireSimulationEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffectFactory).create(eq(effectActivation), eq(audioEmitter), eq(gunInfoProvider), propertiesCaptor.capture());

        GunFireSimulationProperties properties = propertiesCaptor.getValue();
        assertEquals(genericRateOfFire, properties.genericRateOfFire());
        assertEquals(maxBurstDuration, properties.maxBurstDuration());
        assertEquals(minBurstDuration, properties.minBurstDuration());
        assertEquals(maxDelayBetweenBursts, properties.maxDelayBetweenBursts());
        assertEquals(minDelayBetweenBursts, properties.minDelayBetweenBursts());
        assertEquals(maxTotalDuration, properties.maxTotalDuration());
        assertEquals(minTotalDuration, properties.minTotalDuration());

        assertEquals(gunFireSimulationEffect, itemEffect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        when(section.getString("type")).thenReturn("MARK_SPAWN_POINT");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(MarkSpawnPointEffect.class, itemEffect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);

        when(section.getString("particle.type")).thenReturn("CAMPFIRE_COSY_SMOKE");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(SmokeScreenEffect.class, itemEffect);
    }

    @Test
    public void throwExceptionIfParticleTypeCannotBeDefined() {
        when(section.getString("particle.type")).thenReturn(null);
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void throwExceptionIfParticleTypeIsIncorrect() {
        when(section.getString("particle.type")).thenReturn("fail");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        when(section.getString("type")).thenReturn("SOUND_NOTIFICATION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(SoundNotificationEffect.class, itemEffect);
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory, gunFireSimulationEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }
}
