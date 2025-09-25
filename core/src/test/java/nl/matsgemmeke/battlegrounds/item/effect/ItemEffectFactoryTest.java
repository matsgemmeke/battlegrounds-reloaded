package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffect;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashProperties;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.RangeProfileMapper;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private CombustionEffectFactory combustionEffectFactory;
    private DamageEffectFactory damageEffectFactory;
    private ExplosionEffectFactory explosionEffectFactory;
    private FlashEffectFactory flashEffectFactory;
    private GunFireSimulationEffectFactory gunFireSimulationEffectFactory;
    private ParticleEffectMapper particleEffectMapper;
    private Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider;
    private RangeProfileMapper rangeProfileMapper;
    private SmokeScreenEffectFactory smokeScreenEffectFactory;
    private TriggerExecutorFactory triggerExecutorFactory;
    private TriggerSpec triggerSpec;

    @BeforeEach
    public void setUp() {
        combustionEffectFactory = mock(CombustionEffectFactory.class);
        damageEffectFactory = mock(DamageEffectFactory.class);
        explosionEffectFactory = mock(ExplosionEffectFactory.class);
        flashEffectFactory = mock(FlashEffectFactory.class);
        gunFireSimulationEffectFactory = mock(GunFireSimulationEffectFactory.class);
        particleEffectMapper = new ParticleEffectMapper();
        markSpawnPointEffectProvider = mock();
        rangeProfileMapper = new RangeProfileMapper();
        smokeScreenEffectFactory = mock(SmokeScreenEffectFactory.class);
        triggerSpec = this.createTriggerSpec();

        triggerExecutorFactory = mock(TriggerExecutorFactory.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(mock(TriggerExecutor.class));
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "COMBUSTION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.range = rangeProfileSpec;
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 350L;
        spec.maxDuration = 450L;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        ItemEffect combustionEffect = mock(CombustionEffect.class);
        when(combustionEffectFactory.create(any(CombustionProperties.class), any(RangeProfile.class))).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        ArgumentCaptor<RangeProfile> rangeProfileCaptor = ArgumentCaptor.forClass(RangeProfile.class);

        verify(combustionEffectFactory).create(propertiesCaptor.capture(), rangeProfileCaptor.capture());

        CombustionProperties properties = propertiesCaptor.getValue();
        assertThat(properties.minSize()).isEqualTo(spec.minSize);
        assertThat(properties.maxSize()).isEqualTo(spec.maxSize);
        assertThat(properties.growth()).isEqualTo(spec.growth);
        assertThat(properties.growthInterval()).isEqualTo(spec.growthInterval);
        assertThat(properties.minDuration()).isEqualTo(spec.minDuration);
        assertThat(properties.maxDuration()).isEqualTo(spec.maxDuration);
        assertThat(properties.burnBlocks()).isEqualTo(spec.damageBlocks);
        assertThat(properties.spreadFire()).isEqualTo(spec.spreadFire);

        RangeProfile rangeProfile = rangeProfileCaptor.getValue();
        assertThat(rangeProfile.shortRangeDamage()).isEqualTo(rangeProfileSpec.shortRange.damage);
        assertThat(rangeProfile.shortRangeDistance()).isEqualTo(rangeProfileSpec.shortRange.distance);
        assertThat(rangeProfile.mediumRangeDamage()).isEqualTo(rangeProfileSpec.mediumRange.damage);
        assertThat(rangeProfile.mediumRangeDistance()).isEqualTo(rangeProfileSpec.mediumRange.distance);
        assertThat(rangeProfile.longRangeDamage()).isEqualTo(rangeProfileSpec.longRange.damage);
        assertThat(rangeProfile.longRangeDistance()).isEqualTo(rangeProfileSpec.longRange.distance);

        assertThat(itemEffect).isEqualTo(combustionEffect);
    }

    @Test
    public void createInstanceForDamageEffectType() {
        DamageEffect damageEffect = mock(DamageEffect.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "DAMAGE";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.range = rangeProfileSpec;
        spec.damageType = "BULLET_DAMAGE";

        when(damageEffectFactory.create(any(DamageProperties.class))).thenReturn(damageEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(damageEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        ExplosionEffect explosionEffect = mock(ExplosionEffect.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "EXPLOSION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.range = rangeProfileSpec;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        when(explosionEffectFactory.create(any(ExplosionProperties.class), any(RangeProfile.class))).thenReturn(explosionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(explosionEffect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        FlashEffect flashEffect = mock(FlashEffect.class);
        PotionEffectSpec potionEffectSpec = this.createPotionEffectSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "FLASH";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.maxSize = 5.0;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;
        spec.potionEffect = potionEffectSpec;

        when(flashEffectFactory.create(any(FlashProperties.class))).thenReturn(flashEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(flashEffect);
    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        ActivationPatternSpec activationPatternSpec = this.createActivationPatternSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "GUN_FIRE_SIMULATION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.activationPattern = activationPatternSpec;

        ItemEffect gunFireSimulationEffect = mock(GunFireSimulationEffect.class);
        when(gunFireSimulationEffectFactory.create(any(GunFireSimulationProperties.class))).thenReturn(gunFireSimulationEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffectFactory).create(propertiesCaptor.capture());

        GunFireSimulationProperties properties = propertiesCaptor.getValue();
        assertThat(properties.burstInterval()).isEqualTo(activationPatternSpec.burstInterval);
        assertThat(properties.maxBurstDuration()).isEqualTo(activationPatternSpec.maxBurstDuration);
        assertThat(properties.minBurstDuration()).isEqualTo(activationPatternSpec.minBurstDuration);
        assertThat(properties.maxDelayDuration()).isEqualTo(activationPatternSpec.maxDelayDuration);
        assertThat(properties.minDelayDuration()).isEqualTo(activationPatternSpec.minDelayDuration);
        assertThat(properties.maxTotalDuration()).isEqualTo(spec.maxDuration);
        assertThat(properties.minTotalDuration()).isEqualTo(spec.minDuration);

        assertThat(itemEffect).isEqualTo(gunFireSimulationEffect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "MARK_SPAWN_POINT";
        spec.triggers = Map.of("timed", triggerSpec);

        MarkSpawnPointEffect markSpawnPointEffect = mock(MarkSpawnPointEffect.class);

        when(markSpawnPointEffectProvider.get()).thenReturn(markSpawnPointEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(markSpawnPointEffect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "SMOKE_SCREEN";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.density = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.particleEffect = particleEffectSpec;

        ItemEffect smokeScreenEffect = mock(SmokeScreenEffect.class);
        when(smokeScreenEffectFactory.create(any(SmokeScreenProperties.class))).thenReturn(smokeScreenEffect);

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        ArgumentCaptor<SmokeScreenProperties> propertiesCaptor = ArgumentCaptor.forClass(SmokeScreenProperties.class);
        verify(smokeScreenEffectFactory).create(propertiesCaptor.capture());

        SmokeScreenProperties properties = propertiesCaptor.getValue();
        assertThat(properties.minDuration()).isEqualTo(spec.minDuration);
        assertThat(properties.maxDuration()).isEqualTo(spec.maxDuration);
        assertThat(properties.density()).isEqualTo(spec.density);
        assertThat(properties.minSize()).isEqualTo(spec.minSize);
        assertThat(properties.maxSize()).isEqualTo(spec.maxSize);
        assertThat(properties.growth()).isEqualTo(spec.growth);
        assertThat(properties.growthInterval()).isEqualTo(spec.growthInterval);
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.CAMPFIRE_COSY_SMOKE);
        assertThat(properties.particleEffect().count()).isEqualTo(particleEffectSpec.count);
        assertThat(properties.particleEffect().offsetX()).isEqualTo(particleEffectSpec.offsetX);
        assertThat(properties.particleEffect().offsetY()).isEqualTo(particleEffectSpec.offsetY);
        assertThat(properties.particleEffect().offsetZ()).isEqualTo(particleEffectSpec.offsetZ);
        assertThat(properties.particleEffect().extra()).isEqualTo(particleEffectSpec.extra);

        assertThat(itemEffect).isEqualTo(smokeScreenEffect);
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "SOUND_NOTIFICATION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.activationSounds = "AMBIENT_CAVE-1-1-0";

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);
        ItemEffect itemEffect = factory.create(spec);

        assertThat(itemEffect).isInstanceOf(SoundNotificationEffect.class);
    }

    @Test
    public void createThrowsItemEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "EXPLOSION";

        ItemEffectFactory factory = new ItemEffectFactory(combustionEffectFactory, damageEffectFactory, explosionEffectFactory, flashEffectFactory, gunFireSimulationEffectFactory, particleEffectMapper, markSpawnPointEffectProvider, rangeProfileMapper, smokeScreenEffectFactory, triggerExecutorFactory);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Cannot create EXPLOSION because of invalid spec: Required 'rangeProfile' value is missing");
    }

    private ActivationPatternSpec createActivationPatternSpec() {
        ActivationPatternSpec spec = new ActivationPatternSpec();
        spec.burstInterval = 2L;
        spec.minBurstDuration = 100L;
        spec.maxBurstDuration = 200L;
        spec.minDelayDuration = 10L;
        spec.maxDelayDuration = 20L;
        return spec;
    }

    private ParticleEffectSpec createParticleEffectSpec() {
        ParticleEffectSpec spec = new ParticleEffectSpec();
        spec.particle = "CAMPFIRE_COSY_SMOKE";
        spec.count = 1;
        spec.offsetX = 0.1;
        spec.offsetY = 0.1;
        spec.offsetZ = 0.1;
        spec.extra = 0.0;
        return spec;
    }

    private PotionEffectSpec createPotionEffectSpec() {
        PotionEffectSpec spec = new PotionEffectSpec();
        spec.type = "BLINDNESS";
        spec.duration = 100;
        spec.amplifier = 1;
        spec.ambient = true;
        spec.particles = false;
        spec.icon = true;
        return spec;
    }

    private RangeProfileSpec createRangeProfileSpec() {
        DamageDistanceSpec shortRangeSpec = new DamageDistanceSpec();
        shortRangeSpec.damage = 30.0;
        shortRangeSpec.distance = 0.5;

        DamageDistanceSpec mediumRangeSpec = new DamageDistanceSpec();
        mediumRangeSpec.damage = 20.0;
        mediumRangeSpec.distance = 1.0;

        DamageDistanceSpec longRangeSpec = new DamageDistanceSpec();
        longRangeSpec.damage = 10.0;
        longRangeSpec.distance = 1.5;

        RangeProfileSpec spec = new RangeProfileSpec();
        spec.shortRange = shortRangeSpec;
        spec.mediumRange = mediumRangeSpec;
        spec.longRange = longRangeSpec;
        return spec;
    }

    private TriggerSpec createTriggerSpec() {
        TriggerSpec spec = new TriggerSpec();
        spec.type = "TIMED";
        spec.delay = 20L;
        return spec;
    }
}
