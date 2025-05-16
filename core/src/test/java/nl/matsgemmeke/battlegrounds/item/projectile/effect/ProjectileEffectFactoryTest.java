package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ProjectileEffectFactoryTest {

    private BounceEffectFactory bounceEffectFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ParticleEffectMapper particleEffectMapper;
    private SoundEffectFactory soundEffectFactory;
    private StickEffectFactory stickEffectFactory;
    private TrailEffectFactory trailEffectFactory;
    private TriggerFactory triggerFactory;

    @BeforeEach
    public void setUp() {
        bounceEffectFactory = mock(BounceEffectFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        particleEffectMapper = new ParticleEffectMapper();
        soundEffectFactory = mock(SoundEffectFactory.class);
        stickEffectFactory = mock(StickEffectFactory.class);
        trailEffectFactory = mock(TrailEffectFactory.class);
        triggerFactory = mock(TriggerFactory.class);
    }

    @Test
    public void createReturnsInstanceOfBounceEffect() {
        BounceEffect bounceEffect = mock(BounceEffect.class);
        Double horizontalFriction = 2.0;
        Double verticalFriction = 3.0;
        Integer maxActivations = 3;
        TriggerSpec triggerSpec = new TriggerSpec("TIMED", null, null, null);

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(triggerSpec, gameKey)).thenReturn(trigger);

        ProjectileEffectSpec spec = new ProjectileEffectSpec("BOUNCE", null, null, null, horizontalFriction, verticalFriction, maxActivations, null, List.of(triggerSpec));
        when(bounceEffectFactory.create(any(BounceProperties.class), argThat(triggers -> triggers.contains(trigger)))).thenReturn(bounceEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(bounceEffectFactory, contextProvider, particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        ArgumentCaptor<BounceProperties> bouncePropertiesCaptor = ArgumentCaptor.forClass(BounceProperties.class);
        verify(bounceEffectFactory).create(bouncePropertiesCaptor.capture(), argThat(triggers -> triggers.contains(trigger)));

        BounceProperties properties = bouncePropertiesCaptor.getValue();
        assertThat(properties.amountOfBounces()).isEqualTo(maxActivations);
        assertThat(properties.horizontalFriction()).isEqualTo(horizontalFriction);
        assertThat(properties.verticalFriction()).isEqualTo(verticalFriction);

        assertThat(projectileEffect).isEqualTo(bounceEffect);
    }

    @Test
    public void createReturnsInstanceOfSoundEffect() {
        SoundEffect soundEffect = mock(SoundEffect.class);
        Long delay = 1L;
        List<Long> intervals = List.of(5L, 10L);
        String sounds = "AMBIENT_CAVE-1-1-0";

        ProjectileEffectSpec spec = new ProjectileEffectSpec("SOUND", delay, intervals, sounds, null, null, null, null, null);
        when(soundEffectFactory.create(any(SoundProperties.class))).thenReturn(soundEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(bounceEffectFactory, contextProvider, particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        ArgumentCaptor<SoundProperties> soundPropertiesCaptor = ArgumentCaptor.forClass(SoundProperties.class);
        verify(soundEffectFactory).create(soundPropertiesCaptor.capture());

        SoundProperties properties = soundPropertiesCaptor.getValue();
        assertThat(properties.delay()).isEqualTo(delay);
        assertThat(properties.intervals()).isEqualTo(intervals);
        assertThat(properties.sounds()).hasSize(1);

        assertThat(projectileEffect).isEqualTo(soundEffect);
    }

    @Test
    public void createReturnsInstanceOfStickEffect() {
        StickEffect stickEffect = mock(StickEffect.class);
        String stickSounds = "AMBIENT_CAVE-1-1-0";
        TriggerSpec triggerSpec = new TriggerSpec("FLOOR_HIT", null, 1L, null);

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(triggerSpec, gameKey)).thenReturn(trigger);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ProjectileEffectSpec spec = new ProjectileEffectSpec("STICK", null, null, stickSounds, null, null, null, null, List.of(triggerSpec));
        when(stickEffectFactory.create(any(StickProperties.class), eq(audioEmitter))).thenReturn(stickEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(bounceEffectFactory, contextProvider, particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        ArgumentCaptor<StickProperties> stickPropertiesCaptor = ArgumentCaptor.forClass(StickProperties.class);
        verify(stickEffectFactory).create(stickPropertiesCaptor.capture(), eq(audioEmitter));

        StickProperties properties = stickPropertiesCaptor.getValue();
        assertThat(properties.stickSounds()).hasSize(1);

        assertThat(projectileEffect).isEqualTo(stickEffect);
    }

    @Test
    public void createReturnsInstanceOfTrailEffect() {
        TrailEffect trailEffect = mock(TrailEffect.class);
        Long delay = 1L;
        List<Long> intervals = List.of(5L, 10L);
        Integer maxActivations = 2;
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null);

        ProjectileEffectSpec spec = new ProjectileEffectSpec("TRAIL", delay, intervals, null, null, null, maxActivations, particleEffectSpec, null);
        when(trailEffectFactory.create(any(TrailProperties.class))).thenReturn(trailEffect);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(bounceEffectFactory, contextProvider, particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerFactory);
        ProjectileEffect projectileEffect = factory.create(spec, gameKey);

        ArgumentCaptor<TrailProperties> trailPropertiesCaptor = ArgumentCaptor.forClass(TrailProperties.class);
        verify(trailEffectFactory).create(trailPropertiesCaptor.capture());

        TrailProperties properties = trailPropertiesCaptor.getValue();
        assertThat(properties.delay()).isEqualTo(delay);
        assertThat(properties.intervals()).isEqualTo(intervals);
        assertThat(properties.maxActivations()).isEqualTo(maxActivations);
        assertThat(properties.particleEffect().particle()).isEqualTo(Particle.FLAME);

        assertThat(projectileEffect).isEqualTo(trailEffect);
    }

    @Test
    public void createThrowsProjectileEffectCreationExceptionWhenRequiredSpecValueIsNull() {
        ProjectileEffectSpec spec = new ProjectileEffectSpec("BOUNCE", null, null, null, null, null, null, null, null);

        ProjectileEffectFactory factory = new ProjectileEffectFactory(bounceEffectFactory, contextProvider, particleEffectMapper, soundEffectFactory, stickEffectFactory, trailEffectFactory, triggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(ProjectileEffectCreationException.class)
                .hasMessage("Cannot create BOUNCE because of invalid spec: Required 'maxActivations' value is missing");
    }
}
