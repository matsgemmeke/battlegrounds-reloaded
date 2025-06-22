package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerFactory;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ProjectileEffectFactoryTest {

    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ParticleEffectMapper particleEffectMapper;
    private TrailEffectFactory trailEffectFactory;
    private TriggerFactory triggerFactory;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofOpenMode();
        particleEffectMapper = new ParticleEffectMapper();
        trailEffectFactory = mock(TrailEffectFactory.class);
        triggerFactory = mock(TriggerFactory.class);
    }

    @Test
    public void createReturnsInstanceOfBounceEffect() {
        Double horizontalFriction = 2.0;
        Double verticalFriction = 3.0;
        Integer maxActivations = 3;
        TriggerSpec triggerSpec = new TriggerSpec("TIMED", 10L, null, null, null);
        ProjectileEffectSpec spec = new ProjectileEffectSpec("BOUNCE", null, horizontalFriction, verticalFriction, maxActivations, null, List.of(triggerSpec));

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(triggerSpec, gameKey)).thenReturn(trigger);

        MockedConstruction<BounceEffect> bounceEffectConstructor = mockConstruction(BounceEffect.class, (mock, context) -> {
            BounceProperties properties = (BounceProperties) context.arguments().get(0);

            assertThat(properties.amountOfBounces()).isEqualTo(maxActivations);
            assertThat(properties.horizontalFriction()).isEqualTo(horizontalFriction);
            assertThat(properties.verticalFriction()).isEqualTo(verticalFriction);
        });

        ProjectileEffectFactory factory = new ProjectileEffectFactory(contextProvider, particleEffectMapper, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        assertThat(bounceEffectConstructor.constructed()).hasSize(1);
        assertThat(projectileEffect).isInstanceOf(BounceEffect.class);

        bounceEffectConstructor.close();
    }

    @Test
    public void createReturnsInstanceOfSoundEffect() {
        String sounds = "AMBIENT_CAVE-1-1-0";
        TriggerSpec triggerSpec = new TriggerSpec("TIMED", 10L, null, null, null);
        ProjectileEffectSpec spec = new ProjectileEffectSpec("SOUND", sounds, null, null, null, null, List.of(triggerSpec));

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(triggerSpec, gameKey)).thenReturn(trigger);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(contextProvider, particleEffectMapper, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        assertThat(projectileEffect).isInstanceOf(SoundEffect.class);
    }

    @Test
    public void createReturnsInstanceOfStickEffect() {
        String stickSounds = "AMBIENT_CAVE-1-1-0";
        TriggerSpec triggerSpec = new TriggerSpec("FLOOR_HIT", 10L, 1L, null, null);
        ProjectileEffectSpec projectileEffectSpec = new ProjectileEffectSpec("STICK", stickSounds, null, null, null, null, List.of(triggerSpec));

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(triggerSpec, gameKey)).thenReturn(trigger);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(contextProvider, particleEffectMapper, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(projectileEffectSpec, gameKey);

        assertThat(projectileEffect).isInstanceOf(StickEffect.class);
    }

    @Test
    public void createReturnsInstanceOfTrailEffect() {
        TrailEffect trailEffect = mock(TrailEffect.class);
        Integer maxActivations = 2;
        TriggerSpec triggerSpec = new TriggerSpec("FLOOR_HIT", 10L, 1L, null, null);
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);

        ProjectileEffectSpec spec = new ProjectileEffectSpec("TRAIL", null, null, null, maxActivations, particleEffectSpec, List.of(triggerSpec));
        when(trailEffectFactory.create(any(TrailProperties.class))).thenReturn(trailEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(contextProvider, particleEffectMapper, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        ArgumentCaptor<TrailProperties> trailPropertiesCaptor = ArgumentCaptor.forClass(TrailProperties.class);
        verify(trailEffectFactory).create(trailPropertiesCaptor.capture());

        TrailProperties properties = trailPropertiesCaptor.getValue();
        assertThat(properties.maxActivations()).isEqualTo(maxActivations);
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.FLAME);

        assertThat(projectileEffect).isEqualTo(trailEffect);
    }

    @Test
    public void createThrowsProjectileEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ProjectileEffectSpec spec = new ProjectileEffectSpec("BOUNCE", null, null, null, null, null, null);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(contextProvider, particleEffectMapper, trailEffectFactory, triggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(ProjectileEffectCreationException.class)
                .hasMessage("Cannot create BOUNCE because of invalid spec: Required 'maxActivations' value is missing");
    }
}
