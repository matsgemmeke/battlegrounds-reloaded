package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileEffectSpec;
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
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ProjectileEffectFactoryTest {

    private ParticleEffectMapper particleEffectMapper;
    private SoundEffectFactory soundEffectFactory;
    private StickEffectFactory stickEffectFactory;
    private TrailEffectFactory trailEffectFactory;
    private TriggerExecutorFactory triggerExecutorFactory;

    @BeforeEach
    public void setUp() {
        particleEffectMapper = new ParticleEffectMapper();
        soundEffectFactory = mock(SoundEffectFactory.class);
        stickEffectFactory = mock(StickEffectFactory.class);
        trailEffectFactory = mock(TrailEffectFactory.class);
        triggerExecutorFactory = mock(TriggerExecutorFactory.class);
    }

    @Test
    public void createReturnsInstanceOfBounceEffect() {
        Double horizontalFriction = 2.0;
        Double verticalFriction = 3.0;
        Integer maxActivations = 3;
        TriggerSpec triggerSpec = this.createTriggerSpec();

        ProjectileEffectSpec spec = new ProjectileEffectSpec();
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

        ProjectileEffectFactory factory = new ProjectileEffectFactory(particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerExecutorFactory);
        ProjectileEffect projectileEffect = factory.create(spec);

        assertThat(bounceEffectConstructor.constructed()).hasSize(1);
        assertThat(projectileEffect).isInstanceOf(BounceEffect.class);

        bounceEffectConstructor.close();
    }

    @Test
    public void createReturnsInstanceOfSoundEffect() {
        SoundEffect soundEffect = mock(SoundEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();

        ProjectileEffectSpec spec = new ProjectileEffectSpec();
        spec.type = "SOUND";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.sounds = "AMBIENT_CAVE-1-1-0";

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(triggerExecutor);

        when(soundEffectFactory.create(anyList())).thenReturn(soundEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerExecutorFactory);
        ProjectileEffect projectileEffect = factory.create(spec);

        assertThat(projectileEffect).isEqualTo(soundEffect);
    }

    @Test
    public void createReturnsInstanceOfStickEffect() {
        StickEffect stickEffect = mock(StickEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();

        ProjectileEffectSpec spec = new ProjectileEffectSpec();
        spec.type = "STICK";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.sounds = "AMBIENT_CAVE-1-1-0";

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutorFactory.create(triggerSpec)).thenReturn(triggerExecutor);

        when(stickEffectFactory.create(anyList())).thenReturn(stickEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerExecutorFactory);
        ProjectileEffect projectileEffect = factory.create(spec);

        assertThat(projectileEffect).isEqualTo(stickEffect);
    }

    @Test
    public void createReturnsInstanceOfTrailEffect() {
        TrailEffect trailEffect = mock(TrailEffect.class);
        TriggerSpec triggerSpec = this.createTriggerSpec();
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec();

        ProjectileEffectSpec spec = new ProjectileEffectSpec();
        spec.type = "TRAIL";
        spec.triggers = Map.of("floor-hit", triggerSpec);
        spec.maxActivations = 2;
        spec.particleEffect = particleEffectSpec;

        when(trailEffectFactory.create(any(TrailProperties.class))).thenReturn(trailEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerExecutorFactory);
        ProjectileEffect projectileEffect = factory.create(spec);

        ArgumentCaptor<TrailProperties> trailPropertiesCaptor = ArgumentCaptor.forClass(TrailProperties.class);
        verify(trailEffectFactory).create(trailPropertiesCaptor.capture());

        TrailProperties properties = trailPropertiesCaptor.getValue();
        assertThat(properties.maxActivations()).isEqualTo(spec.maxActivations);
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.FLAME);

        assertThat(projectileEffect).isEqualTo(trailEffect);
    }

    @Test
    public void createThrowsProjectileEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ProjectileEffectSpec spec = new ProjectileEffectSpec();
        spec.type = "BOUNCE";

        ProjectileEffectFactory factory = new ProjectileEffectFactory(particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerExecutorFactory);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(ProjectileEffectCreationException.class)
                .hasMessage("Cannot create BOUNCE because of invalid spec: Required 'maxActivations' value is missing");
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
        TriggerSpec triggerSpec = new TriggerSpec();
        triggerSpec.type = "FLOOR_HIT";
        triggerSpec.delay = 10L;
        triggerSpec.interval = 1L;
        return triggerSpec;
    }
}
