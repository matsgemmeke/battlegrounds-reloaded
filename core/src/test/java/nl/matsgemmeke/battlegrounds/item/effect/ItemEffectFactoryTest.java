package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffectNew;
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

    private ParticleEffectMapper particleEffectMapper;
    private Provider<CombustionEffectNew> combustionEffectProvider;
    private Provider<DamageEffectNew> damageEffectProvider;
    private Provider<ExplosionEffectNew> explosionEffectProvider;
    private Provider<FlashEffectNew> flashEffectProvider;
    private Provider<GunFireSimulationEffectNew> gunFireSimulationEffectProvider;
    private Provider<MarkSpawnPointEffectNew> markSpawnPointEffectProvider;
    private Provider<SmokeScreenEffectNew> smokeScreenEffectProvider;
    private RangeProfileMapper rangeProfileMapper;
    private TriggerExecutorFactory triggerExecutorFactory;
    private TriggerSpec triggerSpec;

    @BeforeEach
    public void setUp() {
        particleEffectMapper = new ParticleEffectMapper();
        combustionEffectProvider = mock();
        damageEffectProvider = mock();
        explosionEffectProvider = mock();
        flashEffectProvider = mock();
        gunFireSimulationEffectProvider = mock();
        markSpawnPointEffectProvider = mock();
        smokeScreenEffectProvider = mock();
        rangeProfileMapper = new RangeProfileMapper();
        triggerSpec = this.createTriggerSpec();

        triggerExecutorFactory = mock(TriggerExecutorFactory.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(mock(TriggerExecutor.class));
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        CombustionEffectNew combustionEffect = mock(CombustionEffectNew.class);
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

        when(combustionEffectProvider.get()).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        verify(combustionEffect).setProperties(propertiesCaptor.capture());

        CombustionProperties properties = propertiesCaptor.getValue();
        assertThat(properties.minSize()).isEqualTo(spec.minSize);
        assertThat(properties.maxSize()).isEqualTo(spec.maxSize);
        assertThat(properties.growth()).isEqualTo(spec.growth);
        assertThat(properties.growthInterval()).isEqualTo(spec.growthInterval);
        assertThat(properties.minDuration()).isEqualTo(spec.minDuration);
        assertThat(properties.maxDuration()).isEqualTo(spec.maxDuration);
        assertThat(properties.burnBlocks()).isEqualTo(spec.damageBlocks);
        assertThat(properties.spreadFire()).isEqualTo(spec.spreadFire);

        RangeProfile rangeProfile = properties.rangeProfile();
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
        DamageEffectNew damageEffect = mock(DamageEffectNew.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "DAMAGE";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.range = rangeProfileSpec;
        spec.damageType = "BULLET_DAMAGE";

        when(damageEffectProvider.get()).thenReturn(damageEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(damageEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        ExplosionEffectNew explosionEffect = mock(ExplosionEffectNew.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "EXPLOSION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.range = rangeProfileSpec;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        when(explosionEffectProvider.get()).thenReturn(explosionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(explosionEffect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        FlashEffectNew flashEffect = mock(FlashEffectNew.class);
        PotionEffectSpec potionEffectSpec = this.createPotionEffectSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "FLASH";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.maxSize = 5.0;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;
        spec.potionEffect = potionEffectSpec;

        when(flashEffectProvider.get()).thenReturn(flashEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(flashEffect);
    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        GunFireSimulationEffectNew gunFireSimulationEffect = mock(GunFireSimulationEffectNew.class);
        ActivationPatternSpec activationPatternSpec = this.createActivationPatternSpec();

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "GUN_FIRE_SIMULATION";
        spec.triggers = Map.of("timed", triggerSpec);
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.activationPattern = activationPatternSpec;

        when(gunFireSimulationEffectProvider.get()).thenReturn(gunFireSimulationEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffect).setProperties(propertiesCaptor.capture());

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
        MarkSpawnPointEffectNew markSpawnPointEffect = mock(MarkSpawnPointEffectNew.class);

        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "MARK_SPAWN_POINT";
        spec.triggers = Map.of("timed", triggerSpec);

        when(markSpawnPointEffectProvider.get()).thenReturn(markSpawnPointEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        assertThat(itemEffect).isEqualTo(markSpawnPointEffect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        SmokeScreenEffectNew smokeScreenEffect = mock(SmokeScreenEffectNew.class);
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

        when(smokeScreenEffectProvider.get()).thenReturn(smokeScreenEffect);

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        ArgumentCaptor<SmokeScreenProperties> propertiesCaptor = ArgumentCaptor.forClass(SmokeScreenProperties.class);
        verify(smokeScreenEffect).setProperties(propertiesCaptor.capture());

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

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);
        ItemEffectNew itemEffect = factory.create(spec);

        assertThat(itemEffect).isInstanceOf(SoundNotificationEffectNew.class);
    }

    @Test
    public void createThrowsItemEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ItemEffectSpec spec = new ItemEffectSpec();
        spec.type = "EXPLOSION";

        ItemEffectFactory factory = new ItemEffectFactory(particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, rangeProfileMapper, triggerExecutorFactory);

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
