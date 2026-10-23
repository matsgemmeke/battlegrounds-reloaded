package nl.matsgemmeke.battlegrounds.item.effect;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.*;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.*;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageEffect;
import nl.matsgemmeke.battlegrounds.item.effect.damage.DamageProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenProperties;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationProperties;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.HitboxDamageProfileMapper;
import nl.matsgemmeke.battlegrounds.item.mapper.RangeProfileMapper;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.SoundService;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemEffectFactoryTest {

    private static final String SOUND_SEQUENCE_ID = "equipment-activation-1";
    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);

    @Spy
    private HitboxDamageProfileMapper hitboxDamageProfileMapper;
    @Spy
    private ParticleEffectMapper particleEffectMapper;
    @Mock
    private Provider<CombustionEffect> combustionEffectProvider;
    @Mock
    private Provider<DamageEffect> damageEffectProvider;
    @Mock
    private Provider<ExplosionEffect> explosionEffectProvider;
    @Mock
    private Provider<FlashEffect> flashEffectProvider;
    @Mock
    private Provider<GunFireSimulationEffect> gunFireSimulationEffectProvider;
    @Mock
    private Provider<MarkSpawnPointEffect> markSpawnPointEffectProvider;
    @Mock
    private Provider<SmokeScreenEffect> smokeScreenEffectProvider;
    @Mock
    private Provider<SoundNotificationEffect> soundNotificationEffectProvider;
    @Spy
    private RangeProfileMapper rangeProfileMapper;
    @Mock
    private SoundService soundService;

    private ItemEffectFactory itemEffectFactory;

    @BeforeEach
    void setUp() {
        itemEffectFactory = new ItemEffectFactory(hitboxDamageProfileMapper, particleEffectMapper, combustionEffectProvider, damageEffectProvider, explosionEffectProvider, flashEffectProvider, gunFireSimulationEffectProvider, markSpawnPointEffectProvider, smokeScreenEffectProvider, soundNotificationEffectProvider, rangeProfileMapper, soundService);
    }

    @Test
    @DisplayName("create throws ItemEffectCreationException when sound sequence id for combustion effect is invalid")
    void create_invalidCombustionSoundSequence() {
        CombustionEffectSpec spec = new CombustionEffectSpec();
        spec.type = "COMBUSTION";
        spec.activationSoundSequence = SOUND_SEQUENCE_ID;
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 350L;
        spec.maxDuration = 450L;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEffectFactory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Sound sequence 'equipment-activation-1' does not exist");
    }

    @Test
    @DisplayName("create returns instance for CombustionEffect")
    void create_combustionEffect() {
        CombustionEffect combustionEffect = mock(CombustionEffect.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        CombustionEffectSpec spec = new CombustionEffectSpec();
        spec.type = "COMBUSTION";
        spec.activationSoundSequence = SOUND_SEQUENCE_ID;
        spec.range = rangeProfileSpec;
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 350L;
        spec.maxDuration = 450L;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(combustionEffectProvider.get()).thenReturn(combustionEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        ArgumentCaptor<CombustionProperties> propertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        verify(combustionEffect).setProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.soundSequence()).isEqualTo(SOUND_SEQUENCE);
            assertThat(properties.rangeProfile()).satisfies(rangeProfile -> {
                assertThat(rangeProfile.shortRangeDamage()).isEqualTo(rangeProfileSpec.shortRange.damage);
                assertThat(rangeProfile.shortRangeDistance()).isEqualTo(rangeProfileSpec.shortRange.distance);
                assertThat(rangeProfile.mediumRangeDamage()).isEqualTo(rangeProfileSpec.mediumRange.damage);
                assertThat(rangeProfile.mediumRangeDistance()).isEqualTo(rangeProfileSpec.mediumRange.distance);
                assertThat(rangeProfile.longRangeDamage()).isEqualTo(rangeProfileSpec.longRange.damage);
                assertThat(rangeProfile.longRangeDistance()).isEqualTo(rangeProfileSpec.longRange.distance);
            });
            assertThat(properties.minSize()).isEqualTo(spec.minSize);
            assertThat(properties.maxSize()).isEqualTo(spec.maxSize);
            assertThat(properties.growth()).isEqualTo(spec.growth);
            assertThat(properties.growthInterval()).isEqualTo(spec.growthInterval);
            assertThat(properties.minDuration()).isEqualTo(spec.minDuration);
            assertThat(properties.maxDuration()).isEqualTo(spec.maxDuration);
            assertThat(properties.burnBlocks()).isEqualTo(spec.damageBlocks);
            assertThat(properties.spreadFire()).isEqualTo(spec.spreadFire);
        });

        assertThat(itemEffect).isEqualTo(combustionEffect);
    }

    @Test
    void createInstanceForDamageEffectType() {
        DamageEffect damageEffect = mock(DamageEffect.class);
        HitboxDamageProfileSpec hitboxDamageProfileSpec = this.createHitboxDamageProfileSpec();
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        DamageEffectSpec spec = new DamageEffectSpec();
        spec.type = "DAMAGE";
        spec.range = rangeProfileSpec;
        spec.hitboxDamageProfile = hitboxDamageProfileSpec;
        spec.damageType = "BULLET_DAMAGE";

        when(damageEffectProvider.get()).thenReturn(damageEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        ArgumentCaptor<DamageProperties> propertiesCaptor = ArgumentCaptor.forClass(DamageProperties.class);
        verify(damageEffect).setProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.damageType()).isEqualTo(DamageType.BULLET_DAMAGE);
            assertThat(properties.rangeProfile()).satisfies(rangeProfile -> {
               assertThat(rangeProfile.shortRangeDamage()).isEqualTo(30.0);
               assertThat(rangeProfile.shortRangeDistance()).isEqualTo(0.5);
               assertThat(rangeProfile.mediumRangeDamage()).isEqualTo(20.0);
               assertThat(rangeProfile.mediumRangeDistance()).isEqualTo(1.0);
               assertThat(rangeProfile.longRangeDamage()).isEqualTo(10.0);
               assertThat(rangeProfile.longRangeDistance()).isEqualTo(1.5);
            });
            assertThat(properties.hitboxDamageProfile()).satisfies(hitboxDamageProfile -> {
                assertThat(hitboxDamageProfile.headDamageModifier()).isEqualTo(1.5);
                assertThat(hitboxDamageProfile.torsoDamageModifier()).isEqualTo(1.0);
                assertThat(hitboxDamageProfile.limbsDamageMultiplier()).isEqualTo(0.5);
            });
        });

        assertThat(itemEffect).isEqualTo(damageEffect);
    }

    @Test
    void createInstanceForExplosionEffectType() {
        ExplosionEffect explosionEffect = mock(ExplosionEffect.class);
        RangeProfileSpec rangeProfileSpec = this.createRangeProfileSpec();

        ExplosionEffectSpec spec = new ExplosionEffectSpec();
        spec.type = "EXPLOSION";
        spec.range = rangeProfileSpec;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;

        when(explosionEffectProvider.get()).thenReturn(explosionEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        assertThat(itemEffect).isEqualTo(explosionEffect);
    }

    @Test
    void createInstanceForFlashEffectType() {
        FlashEffect flashEffect = mock(FlashEffect.class);
        PotionEffectSpec potionEffectSpec = this.createPotionEffectSpec();

        FlashEffectSpec spec = new FlashEffectSpec();
        spec.type = "FLASH";
        spec.range = 5.0;
        spec.power = 2.0f;
        spec.damageBlocks = true;
        spec.spreadFire = false;
        spec.potionEffect = potionEffectSpec;

        when(flashEffectProvider.get()).thenReturn(flashEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        assertThat(itemEffect).isEqualTo(flashEffect);
    }

    @Test
    @DisplayName("create throws ItemEffectCreationException when sound sequence id for gun fire simulation effect is invalid")
    void create_invalidGunFireSimulationSoundSequence() {
        GunFireSimulationEffectSpec spec = new GunFireSimulationEffectSpec();
        spec.type = "GUN_FIRE_SIMULATION";
        spec.defaultSoundSequence = SOUND_SEQUENCE_ID;
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.burstInterval = 2L;
        spec.minBurstDuration = 100L;
        spec.maxBurstDuration = 200L;
        spec.minDelayDuration = 10L;
        spec.maxDelayDuration = 20L;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEffectFactory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Default sound sequence 'equipment-activation-1' does not exist");
    }

    @Test
    @DisplayName("create return instance of GunFireSimulationEffect")
    void create_gunFireSimulationEffect() {
        GunFireSimulationEffect gunFireSimulationEffect = mock(GunFireSimulationEffect.class);

        GunFireSimulationEffectSpec spec = new GunFireSimulationEffectSpec();
        spec.type = "GUN_FIRE_SIMULATION";
        spec.defaultSoundSequence = SOUND_SEQUENCE_ID;
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.burstInterval = 2L;
        spec.minBurstDuration = 100L;
        spec.maxBurstDuration = 200L;
        spec.minDelayDuration = 10L;
        spec.maxDelayDuration = 20L;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(gunFireSimulationEffectProvider.get()).thenReturn(gunFireSimulationEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        ArgumentCaptor<GunFireSimulationProperties> propertiesCaptor = ArgumentCaptor.forClass(GunFireSimulationProperties.class);
        verify(gunFireSimulationEffect).setProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.defaultSoundSequence()).isEqualTo(SOUND_SEQUENCE);
            assertThat(properties.burstInterval()).isEqualTo(spec.burstInterval);
            assertThat(properties.maxBurstDuration()).isEqualTo(spec.maxBurstDuration);
            assertThat(properties.minBurstDuration()).isEqualTo(spec.minBurstDuration);
            assertThat(properties.maxDelayDuration()).isEqualTo(spec.maxDelayDuration);
            assertThat(properties.minDelayDuration()).isEqualTo(spec.minDelayDuration);
            assertThat(properties.maxTotalDuration()).isEqualTo(spec.maxDuration);
            assertThat(properties.minTotalDuration()).isEqualTo(spec.minDuration);
        });

        assertThat(itemEffect).isEqualTo(gunFireSimulationEffect);
    }

    @Test
    void makeCreatesInstanceOfMarkSpawnPointEffect() {
        MarkSpawnPointEffect markSpawnPointEffect = mock(MarkSpawnPointEffect.class);

        MarkSpawnPointEffectSpec spec = new MarkSpawnPointEffectSpec();
        spec.type = "MARK_SPAWN_POINT";

        when(markSpawnPointEffectProvider.get()).thenReturn(markSpawnPointEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        assertThat(itemEffect).isEqualTo(markSpawnPointEffect);
    }

    @Test
    @DisplayName("create throws ItemEffectCreationException when sound sequence id for sound screen effect is invalid")
    void create_invalidSmokeScreenSoundSequence() {
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec();

        SmokeScreenEffectSpec spec = new SmokeScreenEffectSpec();
        spec.type = "SMOKE_SCREEN";
        spec.activationSoundSequence = SOUND_SEQUENCE_ID;
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.density = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.particleEffect = particleEffectSpec;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEffectFactory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Activation sound sequence 'equipment-activation-1' does not exist");
    }

    @Test
    @DisplayName("create returns instance of SmokeScreenEffect")
    void create_smokeScreenEffect() {
        SmokeScreenEffect smokeScreenEffect = mock(SmokeScreenEffect.class);
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec();

        SmokeScreenEffectSpec spec = new SmokeScreenEffectSpec();
        spec.type = "SMOKE_SCREEN";
        spec.activationSoundSequence = SOUND_SEQUENCE_ID;
        spec.minSize = 2.5;
        spec.maxSize = 5.0;
        spec.density = 5.0;
        spec.growth = 0.5;
        spec.growthInterval = 5L;
        spec.minDuration = 100L;
        spec.maxDuration = 200L;
        spec.particleEffect = particleEffectSpec;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(smokeScreenEffectProvider.get()).thenReturn(smokeScreenEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        ArgumentCaptor<SmokeScreenProperties> propertiesCaptor = ArgumentCaptor.forClass(SmokeScreenProperties.class);
        verify(smokeScreenEffect).setProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.particleEffect()).satisfies(particleEffect -> {
                assertThat(particleEffect.particle()).isEqualTo(Particle.CAMPFIRE_COSY_SMOKE);
                assertThat(particleEffect.count()).isEqualTo(particleEffectSpec.count);
                assertThat(particleEffect.offsetX()).isEqualTo(particleEffectSpec.offsetX);
                assertThat(particleEffect.offsetY()).isEqualTo(particleEffectSpec.offsetY);
                assertThat(particleEffect.offsetZ()).isEqualTo(particleEffectSpec.offsetZ);
                assertThat(particleEffect.extra()).isEqualTo(particleEffectSpec.extra);
            });
            assertThat(properties.activationSoundSequence()).isEqualTo(SOUND_SEQUENCE);
            assertThat(properties.minDuration()).isEqualTo(spec.minDuration);
            assertThat(properties.maxDuration()).isEqualTo(spec.maxDuration);
            assertThat(properties.density()).isEqualTo(spec.density);
            assertThat(properties.minSize()).isEqualTo(spec.minSize);
            assertThat(properties.maxSize()).isEqualTo(spec.maxSize);
            assertThat(properties.growth()).isEqualTo(spec.growth);
            assertThat(properties.growthInterval()).isEqualTo(spec.growthInterval);
        });

        assertThat(itemEffect).isEqualTo(smokeScreenEffect);
    }

    @Test
    @DisplayName("make throws ItemEffectCreationException when given specification has an invalid sequence id")
    void make_soundNotificationEffectInvalidSpec() {
        SoundNotificationEffectSpec spec = new SoundNotificationEffectSpec();
        spec.type = "SOUND_NOTIFICATION";
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEffectFactory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Sound sequence 'equipment-activation-1' does not exist");
    }

    @Test
    @DisplayName("make creates instance of SoundNotificationEffect")
    void make_soundNotificationEffectSuccessful() {
        SoundNotificationEffect soundNotificationEffect = mock(SoundNotificationEffect.class);

        SoundNotificationEffectSpec spec = new SoundNotificationEffectSpec();
        spec.type = "SOUND_NOTIFICATION";
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(soundNotificationEffectProvider.get()).thenReturn(soundNotificationEffect);

        ItemEffect itemEffect = itemEffectFactory.create(spec);

        ArgumentCaptor<SoundNotificationProperties> propertiesCaptor = ArgumentCaptor.forClass(SoundNotificationProperties.class);
        verify(soundNotificationEffect).setProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.soundSequence()).isEqualTo(SOUND_SEQUENCE);
        });

        assertThat(itemEffect).isInstanceOf(SoundNotificationEffect.class);
    }

    @Test
    void createThrowsItemEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ExplosionEffectSpec spec = new ExplosionEffectSpec();
        spec.type = "EXPLOSION";

        assertThatThrownBy(() -> itemEffectFactory.create(spec))
                .isInstanceOf(ItemEffectCreationException.class)
                .hasMessage("Cannot create EXPLOSION because of invalid spec: Required 'rangeProfile' value is missing");
    }

    private HitboxDamageProfileSpec createHitboxDamageProfileSpec() {
        HitboxDamageProfileSpec spec = new HitboxDamageProfileSpec();
        spec.head = 1.5;
        spec.torso = 1.0;
        spec.limbs = 0.5;
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
}
