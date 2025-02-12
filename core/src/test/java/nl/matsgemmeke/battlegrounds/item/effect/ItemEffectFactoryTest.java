package nl.matsgemmeke.battlegrounds.item.effect;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionProperties;
import nl.matsgemmeke.battlegrounds.item.effect.explosion.ExplosionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.flash.FlashEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.sound.SoundNotificationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.spawn.MarkSpawnPointEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemEffectFactoryTest {

    private CombustionEffectFactory combustionEffectFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ItemEffectActivation effectActivation;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        combustionEffectFactory = mock(CombustionEffectFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        effectActivation = mock(ItemEffectActivation.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void createInstanceForCombustionEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        int maxRadius = 3;
        long ticksBetweenSpread = 5L;
        long maxDuration = 600L;
        boolean burnBlocks = true;
        boolean spreadFire = false;

        double longRangeDamage = 10.0;
        double longRangeDistance = 1.5;
        double mediumRangeDamage = 20.0;
        double mediumRangeDistance = 1.0;
        double shortRangeDamage = 30.0;
        double shortRangeDistance = 0.5;

        when(section.getString("type")).thenReturn("COMBUSTION");
        when(section.getInt("max-radius")).thenReturn(maxRadius);
        when(section.getLong("ticks-between-spread")).thenReturn(ticksBetweenSpread);
        when(section.getLong("max-duration")).thenReturn(maxDuration);
        when(section.getBoolean("burn-blocks")).thenReturn(burnBlocks);
        when(section.getBoolean("spread-fire")).thenReturn(spreadFire);

        when(section.getDouble("range.long-range.damage")).thenReturn(longRangeDamage);
        when(section.getDouble("range.long-range.distance")).thenReturn(longRangeDistance);
        when(section.getDouble("range.medium-range.damage")).thenReturn(mediumRangeDamage);
        when(section.getDouble("range.medium-range.distance")).thenReturn(mediumRangeDistance);
        when(section.getDouble("range.short-range.damage")).thenReturn(shortRangeDamage);
        when(section.getDouble("range.short-range.distance")).thenReturn(shortRangeDistance);

        ItemEffect combustionEffect = mock(CombustionEffect.class);
        when(combustionEffectFactory.create(eq(effectActivation), any(CombustionProperties.class), any(RangeProfile.class), eq(audioEmitter), eq(collisionDetector), eq(targetFinder))).thenReturn(combustionEffect);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        ArgumentCaptor<CombustionProperties> combustionPropertiesCaptor = ArgumentCaptor.forClass(CombustionProperties.class);
        ArgumentCaptor<RangeProfile> rangeProfileCaptor = ArgumentCaptor.forClass(RangeProfile.class);

        verify(combustionEffectFactory).create(eq(effectActivation), combustionPropertiesCaptor.capture(), rangeProfileCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(targetFinder));

        CombustionProperties combustionProperties = combustionPropertiesCaptor.getValue();
        assertEquals(maxRadius, combustionProperties.maxRadius());
        assertEquals(ticksBetweenSpread, combustionProperties.ticksBetweenFireSpread());
        assertEquals(maxDuration, combustionProperties.maxDuration());
        assertEquals(burnBlocks, combustionProperties.burnBlocks());
        assertEquals(spreadFire, combustionProperties.spreadFire());

        RangeProfile rangeProfile = rangeProfileCaptor.getValue();
        assertEquals(longRangeDamage, rangeProfile.getLongRangeDamage());
        assertEquals(longRangeDistance, rangeProfile.getLongRangeDistance());
        assertEquals(mediumRangeDamage, rangeProfile.getMediumRangeDamage());
        assertEquals(mediumRangeDistance, rangeProfile.getMediumRangeDistance());
        assertEquals(shortRangeDamage, rangeProfile.getShortRangeDamage());
        assertEquals(shortRangeDistance, rangeProfile.getShortRangeDistance());

        assertInstanceOf(CombustionEffect.class, effect);
    }

    @Test
    public void createInstanceForExplosionEffectType() {
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(ExplosionEffect.class, effect);
    }

    @Test
    public void createInstanceForFlashEffectType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(FlashEffect.class, effect);
    }

    @Test
    public void makeCreatesInstanceOfGunFireSimulationEffect() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        GunInfoProvider gunInfoProvider = mock(GunInfoProvider.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, GunInfoProvider.class)).thenReturn(gunInfoProvider);

        when(section.getString("type")).thenReturn("GUN_FIRE_SIMULATION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(GunFireSimulationEffect.class, effect);
    }

    @Test
    public void makeCreatesInstanceOfMarkSpawnPointEffect() {
        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        when(section.getString("type")).thenReturn("MARK_SPAWN_POINT");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(MarkSpawnPointEffect.class, effect);
    }

    @Test
    public void createInstanceForSmokeScreenEffectType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);

        when(section.getString("particle.type")).thenReturn("CAMPFIRE_COSY_SMOKE");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(SmokeScreenEffect.class, effect);
    }

    @Test
    public void throwExceptionIfParticleTypeCannotBeDefined() {
        when(section.getString("particle.type")).thenReturn(null);
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void throwExceptionIfParticleTypeIsIncorrect() {
        when(section.getString("particle.type")).thenReturn("fail");
        when(section.getString("type")).thenReturn("SMOKE_SCREEN");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void makeCreatesInstanceOfSoundNotificationEffect() {
        when(section.getString("type")).thenReturn("SOUND_NOTIFICATION");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);
        ItemEffect effect = factory.create(section, gameKey, effectActivation);

        assertInstanceOf(SoundNotificationEffect.class, effect);
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }

    @Test
    public void throwExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectFactory factory = new ItemEffectFactory(contextProvider, taskRunner, combustionEffectFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(section, gameKey, effectActivation));
    }
}
