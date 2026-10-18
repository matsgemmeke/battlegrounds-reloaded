package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.BounceEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.SoundEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.StickEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.TrailEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.FloorHitTriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;
import org.bukkit.Particle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectileEffectFactoryTest {

    private static final String SOUND_SEQUENCE_ID = "equipment-activation-2";
    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);

    @Spy
    private ParticleEffectMapper particleEffectMapper;
    @Mock
    private SoundEffectFactory soundEffectFactory;
    @Mock
    private SoundService soundService;
    @Mock
    private StickEffectFactory stickEffectFactory;
    @Mock
    private TrailEffectFactory trailEffectFactory;
    @Mock
    private TriggerExecutorFactory triggerExecutorFactory;
    @InjectMocks
    private ProjectileEffectFactory projectileEffectFactory;

    @Test
    @DisplayName("create returns BounceEffect instance")
    void create_bounceEffect() {
        Double horizontalFriction = 2.0;
        Double verticalFriction = 3.0;
        Integer maxActivations = 3;
        TriggerSpec triggerSpec = this.createTriggerSpec();

        BounceEffectSpec spec = new BounceEffectSpec();
        spec.type = "BOUNCE";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.horizontalFriction = 2.0;
        spec.verticalFriction = 3.0;
        spec.maxActivations = 3;

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(triggerExecutor);

        MockedConstruction<BounceEffect> bounceEffectConstructor = mockConstruction(BounceEffect.class, (mock, context) -> {
            BounceProperties properties = (BounceProperties) context.arguments().get(0);

            assertThat(properties.amountOfBounces()).isEqualTo(maxActivations);
            assertThat(properties.horizontalFriction()).isEqualTo(horizontalFriction);
            assertThat(properties.verticalFriction()).isEqualTo(verticalFriction);
        });

        ProjectileEffect projectileEffect = projectileEffectFactory.create(spec);

        assertThat(bounceEffectConstructor.constructed()).hasSize(1);
        assertThat(projectileEffect).isInstanceOf(BounceEffect.class);

        bounceEffectConstructor.close();
    }

    @Test
    @DisplayName("create throws ProjectileEffectCreationException when creating sound effect whose sound sequence id is invalid")
    void create_soundEffectWithInvalidSoundSequenceId() {
        SoundEffectSpec spec = new SoundEffectSpec();
        spec.type = "SOUND";
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectileEffectFactory.create(spec))
                .isInstanceOf(ProjectileEffectCreationException.class)
                .hasMessage("Sound sequence 'equipment-activation-2' does not exist");
    }

    @Test
    @DisplayName("create returns SoundEffect instance")
    void create_soundEffect() {
        SoundEffect soundEffect = mock(SoundEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();

        SoundEffectSpec spec = new SoundEffectSpec();
        spec.type = "SOUND";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.soundSequence = SOUND_SEQUENCE_ID;

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(triggerExecutor);

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(soundEffectFactory.create(SOUND_SEQUENCE)).thenReturn(soundEffect);

        ProjectileEffect projectileEffect = projectileEffectFactory.create(spec);

        assertThat(projectileEffect).isEqualTo(soundEffect);
    }

    @Test
    @DisplayName("create throws ProjectileEffectCreationException when creating stick effect whose sound sequence id is invalid")
    void create_stickEffectWithInvalidSoundSequenceId() {
        TriggerSpec triggerSpec = this.createTriggerSpec();

        StickEffectSpec spec = new StickEffectSpec();
        spec.type = "STICK";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectileEffectFactory.create(spec))
                .isInstanceOf(ProjectileEffectCreationException.class)
                .hasMessage("Sound sequence 'equipment-activation-2' does not exist");
    }

    @Test
    @DisplayName("create returns StickEffect instance")
    void create_stickEffect() {
        StickEffect stickEffect = mock(StickEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();

        StickEffectSpec spec = new StickEffectSpec();
        spec.type = "STICK";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.soundSequence = SOUND_SEQUENCE_ID;

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(triggerExecutor);

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(stickEffectFactory.create(SOUND_SEQUENCE)).thenReturn(stickEffect);

        ProjectileEffect projectileEffect = projectileEffectFactory.create(spec);

        assertThat(projectileEffect).isEqualTo(stickEffect);
    }

    @Test
    @DisplayName("create returns TrailEffect instance")
    void create_trailEffect() {
        TrailEffect trailEffect = mock(TrailEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec();

        TrailEffectSpec spec = new TrailEffectSpec();
        spec.type = "TRAIL";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.maxActivations = 2;
        spec.particleEffect = particleEffectSpec;

        when(trailEffectFactory.create(any(TrailProperties.class))).thenReturn(trailEffect);

        ProjectileEffect projectileEffect = projectileEffectFactory.create(spec);

        ArgumentCaptor<TrailProperties> trailPropertiesCaptor = ArgumentCaptor.forClass(TrailProperties.class);
        verify(trailEffectFactory).create(trailPropertiesCaptor.capture());

        TrailProperties properties = trailPropertiesCaptor.getValue();
        assertThat(properties.maxActivations()).isEqualTo(spec.maxActivations);
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.FLAME);

        assertThat(projectileEffect).isEqualTo(trailEffect);
    }

    private ParticleEffectSpec createParticleEffectSpec() {
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec();
        particleEffectSpec.particle = "FLAME";
        particleEffectSpec.count = 1;
        particleEffectSpec.offsetX = 0.0;
        particleEffectSpec.offsetY = 0.0;
        particleEffectSpec.offsetZ = 0.0;
        particleEffectSpec.extra = 0.0;
        return particleEffectSpec;
    }

    private TriggerSpec createTriggerSpec() {
        FloorHitTriggerSpec triggerSpec = new FloorHitTriggerSpec();
        triggerSpec.type = "FLOOR_HIT";
        triggerSpec.delay = 10L;
        triggerSpec.interval = 1L;
        return triggerSpec;
    }
}
