package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.EffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GunFireSimulationEffectTest {

    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Location SOURCE_LOCATION = new Location(null, 1, 1, 1);
    private static final long BURST_INTERVAL = 1L;
    private static final long MAX_BURST_DURATION = 10L;
    private static final long MIN_BURST_DURATION = 5L;
    private static final long MAX_DELAY_DURATION = 10L;
    private static final long MIN_DELAY_DURATION = 5L;
    private static final long MAX_TOTAL_DURATION = 30L;
    private static final long MIN_TOTAL_DURATION = 20L;
    private static final List<GameSound> GENERIC_SHOTS_SOUNDS = Collections.emptyList();

    private AudioEmitter audioEmitter;
    private GunFireSimulationProperties properties;
    private GunInfoProvider gunInfoProvider;
    private TaskRunner taskRunner;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new GunFireSimulationProperties(GENERIC_SHOTS_SOUNDS, BURST_INTERVAL, MAX_BURST_DURATION, MIN_BURST_DURATION, MAX_DELAY_DURATION, MIN_DELAY_DURATION, MAX_TOTAL_DURATION, MIN_TOTAL_DURATION);
        gunInfoProvider = mock(GunInfoProvider.class);
        taskRunner = mock(TaskRunner.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void activateSimulatesGenericGunFireWhenGunInfoProviderHasNoInformationForEntity() {
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);

        EffectContext context = new EffectContext(entity, source, INITIATION_LOCATION);

        when(gunInfoProvider.getGunFireSimulationInfo(entityId)).thenReturn(Optional.empty());

        GunFireSimulationEffect effect = new GunFireSimulationEffect(taskRunner, audioEmitter, gunInfoProvider, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, SOURCE_LOCATION);
    }

    @Test
    public void activateStopsSimulatesGunFireOnceEffectSourceNoLongerExists() {
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(false);

        EffectContext context = new EffectContext(entity, source, INITIATION_LOCATION);

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 120;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(gunInfoProvider.getGunFireSimulationInfo(entityId)).thenReturn(Optional.of(gunFireSimulationInfo));

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(1L))).thenReturn(task);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(taskRunner, audioEmitter, gunInfoProvider, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();

        verifyNoInteractions(audioEmitter);
        verify(task).cancel();
    }

    @Test
    public void activateSimulatesGunFireOnceAndRemovesEffectSourceWhenFinished() {
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);

        EffectContext context = new EffectContext(entity, source, INITIATION_LOCATION);

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 1200;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(gunInfoProvider.getGunFireSimulationInfo(entityId)).thenReturn(Optional.of(gunFireSimulationInfo));

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(1L))).thenReturn(task);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(taskRunner, audioEmitter, gunInfoProvider, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        for (int i = 0; i < MAX_TOTAL_DURATION; i++) {
            runnableCaptor.getValue().run();
        }

        // The implementation uses random variables, so check the max and min possible amount of executions
        verify(audioEmitter, atLeast(10)).playSounds(shotSounds, SOURCE_LOCATION);
        verify(audioEmitter, atMost(20)).playSounds(shotSounds, SOURCE_LOCATION);
        verify(source, atLeast(1)).remove();
        verify(source, atMost(10)).remove();
        verify(task, atLeast(1)).cancel();
        verify(task, atMost(10)).cancel();
    }
}
