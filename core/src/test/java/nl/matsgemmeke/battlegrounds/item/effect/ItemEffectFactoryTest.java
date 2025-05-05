package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
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
import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private CombustionEffectFactory combustionEffectFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    private ParticleEffectMapper particleEffectMapper;
    private SmokeScreenEffectFactory smokeScreenEffectFactory;
    private TriggerFactory triggerFactory;
    private TriggerSpec triggerSpec;

    @BeforeEach
    public void setUp() {
        combustionEffectFactory = mock(CombustionEffectFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        gunFireSimulationEffectFactory = mock(GunFireSimulationEffectFactory.class);
        particleEffectMapper = new ParticleEffectMapper();
        smokeScreenEffectFactory = mock(SmokeScreenEffectFactory.class);
        triggerSpec = new TriggerSpec("TIMED", null, null, 20L);

        triggerFactory = mock(TriggerFactory.class);
        when(triggerFactory.create(triggerSpec, gameKey, null)).thenReturn(mock(Trigger.class));
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(30.0, 0.5, 20.0, 1.0, 10.0, 1.5);
        ItemEffectSpec effectSpec = new ItemEffectSpec("COMBUSTION", List.of(triggerSpec), rangeProfileSpec, 5.0, 2.5, null, 0.5, 5L, 450L, 350L, null, null, true, false, null, null, null);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        ItemEffect combustionEffect = mock(CombustionEffect.class);
        when(combustionEffectFactory.create(any(CombustionProperties.class), any(RangeProfile.class), eq(audioEmitter), eq(collisionDetector), eq(targetFinder))).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        ArgumentCaptor<RangeProfile> rangeProfileCaptor = ArgumentCaptor.forClass(RangeProfile.class);

        verify(combustionEffectFactory).create(propertiesCaptor.capture(), rangeProfileCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(targetFinder));

        CombustionProperties properties = propertiesCaptor.getValue();
        assertThat(properties.maxSize()).isEqualTo(effectSpec.maxSize());
        assertThat(properties.growthInterval()).isEqualTo(effectSpec.growthInterval());
        assertThat(properties.maxDuration()).isEqualTo(effectSpec.maxDuration());
        assertThat(properties.burnBlocks()).isEqualTo(effectSpec.damageBlocks());
        assertThat(properties.spreadFire()).isEqualTo(effectSpec.spreadFire());

        RangeProfile rangeProfile = rangeProfileCaptor.getValue();
        assertThat(rangeProfile.getLongRangeDamage()).isEqualTo(rangeProfileSpec.longRangeDamage());
        assertThat(rangeProfile.getLongRangeDistance()).isEqualTo(rangeProfileSpec.longRangeDistance());
        assertThat(rangeProfile.getMediumRangeDamage()).isEqualTo(rangeProfileSpec.mediumRangeDamage());
        assertThat(rangeProfile.getMediumRangeDistance()).isEqualTo(rangeProfileSpec.mediumRangeDistance());
        assertThat(rangeProfile.getShortRangeDamage()).isEqualTo(rangeProfileSpec.shortRangeDamage());
        assertThat(rangeProfile.getShortRangeDistance()).isEqualTo(rangeProfileSpec.shortRangeDistance());

        assertThat(itemEffect).isEqualTo(combustionEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(30.0, 0.5, 20.0, 1.0, 10.0, 1.5);
        ItemEffectSpec effectSpec = new ItemEffectSpec("EXPLOSION", List.of(triggerSpec), rangeProfileSpec, null, null, null, null, null, null, null, null, 2.0f, true, false, null, null, null);

        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        assertThat(itemEffect).isInstanceOf(ExplosionEffect.class);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        PotionEffectSpec potionEffectSpec = new PotionEffectSpec("BLINDNESS", 100, 1, true, false, true);
        ItemEffectSpec effectSpec = new ItemEffectSpec("FLASH", List.of(triggerSpec), null, null, 5.0, null, null, null, null, null, null, 2.0f, true, false, null, potionEffectSpec, null);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        assertThat(itemEffect).isInstanceOf(FlashEffect.class);

    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        ActivationPatternSpec activationPatternSpec = new ActivationPatternSpec(2L, 200L, 100L, 20L, 10L);
        ItemEffectSpec effectSpec = new ItemEffectSpec("GUN_FIRE_SIMULATION", List.of(triggerSpec), null, null, null, null, null, null, 200L, 100L, null, null, null, null, null, null, activationPatternSpec);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        GunInfoProvider gunInfoProvider = mock(GunInfoProvider.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, GunInfoProvider.class)).thenReturn(gunInfoProvider);

        ItemEffect gunFireSimulationEffect = mock(GunFireSimulationEffect.class);
        when(gunFireSimulationEffectFactory.create(eq(audioEmitter), eq(gunInfoProvider), any(GunFireSimulationProperties.class))).thenReturn(gunFireSimulationEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffectFactory).create(eq(audioEmitter), eq(gunInfoProvider), propertiesCaptor.capture());

        GunFireSimulationProperties properties = propertiesCaptor.getValue();
        assertThat(properties.burstInterval()).isEqualTo(activationPatternSpec.burstInterval());
        assertThat(properties.maxBurstDuration()).isEqualTo(activationPatternSpec.maxBurstDuration());
        assertThat(properties.minBurstDuration()).isEqualTo(activationPatternSpec.minBurstDuration());
        assertThat(properties.maxDelayDuration()).isEqualTo(activationPatternSpec.maxDelayDuration());
        assertThat(properties.minDelayDuration()).isEqualTo(activationPatternSpec.minDelayDuration());
        assertThat(properties.maxTotalDuration()).isEqualTo(effectSpec.maxDuration());
        assertThat(properties.minTotalDuration()).isEqualTo(effectSpec.minDuration());

        assertThat(itemEffect).isEqualTo(gunFireSimulationEffect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(triggerSpec), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        assertThat(itemEffect).isInstanceOf(MarkSpawnPointEffect.class);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("CAMPFIRE_COSY_SMOKE", 1, 0.1, 0.1, 0.1, 0.0, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("SMOKE_SCREEN", List.of(triggerSpec), null, 2.5, 5.0, 5.0, 0.5, 5L, 100L, 200L, null, null, null, null, particleEffectSpec, null, null);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);

        ItemEffect smokeScreenEffect = mock(SmokeScreenEffect.class);
        when(smokeScreenEffectFactory.create(any(SmokeScreenProperties.class), eq(audioEmitter), eq(collisionDetector))).thenReturn(smokeScreenEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        ArgumentCaptor<SmokeScreenProperties> propertiesCaptor = ArgumentCaptor.forClass(SmokeScreenProperties.class);
        verify(smokeScreenEffectFactory).create(propertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector));

        SmokeScreenProperties properties = propertiesCaptor.getValue();
        assertThat(properties.minDuration()).isEqualTo(effectSpec.minDuration());
        assertThat(properties.maxDuration()).isEqualTo(effectSpec.maxDuration());
        assertThat(properties.density()).isEqualTo(effectSpec.density());
        assertThat(properties.minSize()).isEqualTo(effectSpec.minSize());
        assertThat(properties.maxSize()).isEqualTo(effectSpec.maxSize());
        assertThat(properties.growth()).isEqualTo(effectSpec.growth());
        assertThat(properties.growthInterval()).isEqualTo(effectSpec.growthInterval());
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.CAMPFIRE_COSY_SMOKE);
        assertThat(properties.particleEffect().count()).isEqualTo(particleEffectSpec.count());
        assertThat(properties.particleEffect().offsetX()).isEqualTo(particleEffectSpec.offsetX());
        assertThat(properties.particleEffect().offsetY()).isEqualTo(particleEffectSpec.offsetY());
        assertThat(properties.particleEffect().offsetZ()).isEqualTo(particleEffectSpec.offsetZ());
        assertThat(properties.particleEffect().extra()).isEqualTo(particleEffectSpec.extra());

        assertThat(itemEffect).isEqualTo(smokeScreenEffect);
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        ItemEffectSpec effectSpec = new ItemEffectSpec("SOUND_NOTIFICATION", List.of(triggerSpec), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);
        ItemEffect itemEffect = factory.create(effectSpec, gameKey, null);

        assertThat(itemEffect).isInstanceOf(SoundNotificationEffect.class);
    }

    @Test
    public void createThrowsItemEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        TriggerSpec triggerSpec = new TriggerSpec("ENEMY_PROXIMITY", 5.0, 10L, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("EXPLOSION", List.of(triggerSpec), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, combustionEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, smokeScreenEffectFactory, triggerFactory);

        assertThatThrownBy(() -> factory.create(effectSpec, gameKey, null))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Cannot create EXPLOSION because of invalid spec: Required 'rangeProfile' value is missing");
    }
}
