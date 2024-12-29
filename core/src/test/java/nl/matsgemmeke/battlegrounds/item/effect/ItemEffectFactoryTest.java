package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private GameContext context;
    private ItemEffectActivation effectActivation;
    private MetadataValueCreator metadataValueCreator;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        context = mock(GameContext.class);
        effectActivation = mock(ItemEffectActivation.class);
        metadataValueCreator = mock(MetadataValueCreator.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getCollisionDetector()).thenReturn(collisionDetector);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("COMBUSTION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(CombustionEffect.class, effect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(ExplosionEffect.class, effect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(FlashEffect.class, effect);
    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        GunInfoProvider gunInfoProvider = mock(GunInfoProvider.class);

        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getGunInfoProvider()).thenReturn(gunInfoProvider);

        when(section.getString("type")).thenReturn("GUN_FIRE_SIMULATION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(GunFireSimulationEffect.class, effect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(context.getSpawnPointProvider()).thenReturn(spawnPointProvider);

        when(section.getString("type")).thenReturn("MARK_SPAWN_POINT");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(MarkSpawnPointEffect.class, effect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getCollisionDetector()).thenReturn(collisionDetector);

        when(section.getString("particle.type")).thenReturn("CAMPFIRE_COSY_SMOKE");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(SmokeScreenEffect.class, effect);
    }

    @Test
    public void throwExceptionIfParticleTypeCannotBeDefined() {
        when(section.getString("particle.type")).thenReturn(null);
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(section, context, effectActivation));
    }

    @Test
    public void throwExceptionIfParticleTypeIsIncorrect() {
        when(section.getString("particle.type")).thenReturn("fail");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(section, context, effectActivation));
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        when(section.getString("type")).thenReturn("SOUND_NOTIFICATION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context, effectActivation);

        assertInstanceOf(SoundNotificationEffect.class, effect);
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(section, context, effectActivation));
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(section, context, effectActivation));
    }
}
