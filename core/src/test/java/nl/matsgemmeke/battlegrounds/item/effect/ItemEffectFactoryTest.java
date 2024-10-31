package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemEffectFactoryTest {

    private GameContext context;
    private MetadataValueCreator metadataValueCreator;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        metadataValueCreator = mock(MetadataValueCreator.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("COMBUSTION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context);

        assertTrue(effect instanceof CombustionEffect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context);

        assertTrue(effect instanceof ExplosionEffect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context);

        assertTrue(effect instanceof FlashEffect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);

        when(section.getString("particle.type")).thenReturn("CAMPFIRE_COSY_SMOKE");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        ItemEffect effect = factory.make(section, context);

        assertTrue(effect instanceof SmokeScreenEffect);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void throwExceptionIfParticleTypeCannotBeDefined() {
        when(section.getString("particle.type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        factory.make(section, context);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void throwExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        factory.make(section, context);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void throwExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(metadataValueCreator, taskRunner);
        factory.make(section, context);
    }
}
