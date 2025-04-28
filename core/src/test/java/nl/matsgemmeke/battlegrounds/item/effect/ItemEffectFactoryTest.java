package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
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
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private CombustionEffectFactory combustionEffectFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    private Section section;
    private SmokeScreenEffectFactory smokeScreenEffectFactory;

    @BeforeEach
    public void setUp() {
        combustionEffectFactory = mock(CombustionEffectFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        gunFireSimulationEffectFactory = mock(GunFireSimulationEffectFactory.class);
        section = mock(Section.class);
        smokeScreenEffectFactory = mock(SmokeScreenEffectFactory.class);
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
        when(combustionEffectFactory.create(any(CombustionProperties.class), any(RangeProfile.class), eq(audioEmitter), eq(collisionDetector), eq(targetFinder))).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        ArgumentCaptor<RangeProfile> rangeProfileCaptor = ArgumentCaptor.forClass(RangeProfile.class);

        verify(combustionEffectFactory).create(propertiesCaptor.capture(), rangeProfileCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(targetFinder));

        CombustionProperties properties = propertiesCaptor.getValue();
        assertThat(properties.maxRadius()).isEqualTo(maxRadius);
        assertThat(properties.ticksBetweenFireSpread()).isEqualTo(ticksBetweenSpread);
        assertThat(properties.maxDuration()).isEqualTo(maxDuration);
        assertThat(properties.burnBlocks()).isEqualTo(burnBlocks);
        assertThat(properties.spreadFire()).isEqualTo(spreadFire);

        RangeProfile rangeProfile = rangeProfileCaptor.getValue();
        assertThat(rangeProfile.getLongRangeDamage()).isEqualTo(longRangeDamage);
        assertThat(rangeProfile.getLongRangeDistance()).isEqualTo(longRangeDistance);
        assertThat(rangeProfile.getMediumRangeDamage()).isEqualTo(mediumRangeDamage);
        assertThat(rangeProfile.getMediumRangeDistance()).isEqualTo(mediumRangeDistance);
        assertThat(rangeProfile.getShortRangeDamage()).isEqualTo(shortRangeDamage);
        assertThat(rangeProfile.getShortRangeDistance()).isEqualTo(shortRangeDistance);

        assertThat(itemEffect).isEqualTo(combustionEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        assertThat(itemEffect).isInstanceOf(ExplosionEffect.class);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        assertThat(itemEffect).isInstanceOf(FlashEffect.class);
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
        when(gunFireSimulationEffectFactory.create(eq(audioEmitter), eq(gunInfoProvider), any(GunFireSimulationProperties.class))).thenReturn(gunFireSimulationEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffectFactory).create(eq(audioEmitter), eq(gunInfoProvider), propertiesCaptor.capture());

        GunFireSimulationProperties properties = propertiesCaptor.getValue();
        assertThat(properties.genericRateOfFire()).isEqualTo(genericRateOfFire);
        assertThat(properties.maxBurstDuration()).isEqualTo(maxBurstDuration);
        assertThat(properties.minBurstDuration()).isEqualTo(minBurstDuration);
        assertThat(properties.maxDelayBetweenBursts()).isEqualTo(maxDelayBetweenBursts);
        assertThat(properties.minDelayBetweenBursts()).isEqualTo(minDelayBetweenBursts);
        assertThat(properties.maxTotalDuration()).isEqualTo(maxTotalDuration);
        assertThat(properties.minTotalDuration()).isEqualTo(minTotalDuration);

        assertThat(itemEffect).isEqualTo(gunFireSimulationEffect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        when(section.getString("type")).thenReturn("MARK_SPAWN_POINT");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        assertThat(itemEffect).isInstanceOf(MarkSpawnPointEffect.class);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);

        int duration = 400;
        double density = 5.0;
        double radiusMaxSize = 3.0;
        double radiusStartingSize = 0.2;
        double growthIncrease = 0.025;
        long growthPeriod = 1L;

        int count = 1;
        double offsetX = 0.1;
        double offsetY = 0.2;
        double offsetZ = 0.3;
        double extra = 0.0;

        when(section.getInt("duration")).thenReturn(duration);
        when(section.getDouble("density")).thenReturn(density);
        when(section.getDouble("radius.max-size")).thenReturn(radiusMaxSize);
        when(section.getDouble("radius.starting-size")).thenReturn(radiusStartingSize);
        when(section.getDouble("growth-increase")).thenReturn(growthIncrease);
        when(section.getLong("growth-period")).thenReturn(growthPeriod);

        when(section.getString("type")).thenReturn("SMOKE_SCREEN");
        when(section.getString("particle.type")).thenReturn("CAMPFIRE_COSY_SMOKE");
        when(section.getInt("particle.count")).thenReturn(count);
        when(section.getDouble("particle.offset-x")).thenReturn(offsetX);
        when(section.getDouble("particle.offset-y")).thenReturn(offsetY);
        when(section.getDouble("particle.offset-z")).thenReturn(offsetZ);
        when(section.getDouble("particle.extra")).thenReturn(extra);

        ItemEffect smokeScreenEffect = mock(SmokeScreenEffect.class);
        when(smokeScreenEffectFactory.create(any(SmokeScreenProperties.class), eq(audioEmitter), eq(collisionDetector))).thenReturn(smokeScreenEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        ArgumentCaptor<SmokeScreenProperties> propertiesCaptor = ArgumentCaptor.forClass(SmokeScreenProperties.class);
        verify(smokeScreenEffectFactory).create(propertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector));

        SmokeScreenProperties properties = propertiesCaptor.getValue();
        assertThat(properties.duration()).isEqualTo(duration);
        assertThat(properties.density()).isEqualTo(density);
        assertThat(properties.radiusMaxSize()).isEqualTo(radiusMaxSize);
        assertThat(properties.radiusStartingSize()).isEqualTo(radiusStartingSize);
        assertThat(properties.growthIncrease()).isEqualTo(growthIncrease);
        assertThat(properties.growthPeriod()).isEqualTo(growthPeriod);
        assertThat(properties.particleEffect().type()).isEqualTo(Particle.CAMPFIRE_COSY_SMOKE);
        assertThat(properties.particleEffect().count()).isEqualTo(count);
        assertThat(properties.particleEffect().offsetX()).isEqualTo(offsetX);
        assertThat(properties.particleEffect().offsetY()).isEqualTo(offsetY);
        assertThat(properties.particleEffect().offsetZ()).isEqualTo(offsetZ);
        assertThat(properties.particleEffect().extra()).isEqualTo(extra);

        assertThat(itemEffect).isEqualTo(smokeScreenEffect);
    }

    @Test
    public void throwExceptionIfParticleTypeCannotBeDefined() {
        when(section.getString("particle.type")).thenReturn(null);
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);

        assertThatThrownBy(() -> factory.create(section, gameKey)).isInstanceOf(InvalidItemConfigurationException.class);
    }

    @Test
    public void throwExceptionIfParticleTypeIsIncorrect() {
        when(section.getString("particle.type")).thenReturn("fail");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);

        assertThatThrownBy(() -> factory.create(section, gameKey)).isInstanceOf(InvalidItemConfigurationException.class);
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        when(section.getString("type")).thenReturn("SOUND_NOTIFICATION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);
        ItemEffect itemEffect = factory.create(section, gameKey);

        assertThat(itemEffect).isInstanceOf(SoundNotificationEffect.class);
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);

        assertThatThrownBy(() -> factory.create(section, gameKey)).isInstanceOf(InvalidItemConfigurationException.class);
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, smokeScreenEffectFactory);

        assertThatThrownBy(() -> factory.create(section, gameKey)).isInstanceOf(InvalidItemConfigurationException.class);
    }
}
