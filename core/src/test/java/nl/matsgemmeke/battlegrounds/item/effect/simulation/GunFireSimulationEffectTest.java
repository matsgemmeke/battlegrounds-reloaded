package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GunFireSimulationEffectTest {

    private static final int DURATION = 20;
    private static final int GENERIC_RATE_OF_FIRE = 600;
    private static final List<GameSound> GENERIC_SHOTS_SOUNDS = Collections.emptyList();

    private AudioEmitter audioEmitter;
    private GunFireSimulationProperties properties;
    private GunInfoProvider gunInfoProvider;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new GunFireSimulationProperties(GENERIC_SHOTS_SOUNDS, GENERIC_RATE_OF_FIRE, DURATION);
        gunInfoProvider = mock(GunInfoProvider.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateSimulatesGenericGunFireForHolderWhichCannotHoldGuns() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(audioEmitter, gunInfoProvider, taskRunner, properties);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(2L));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, sourceLocation);
    }

    @Test
    public void activateSimulatesGenericGunFireForHolderWhichDoesNotCarryAnyGuns() {
        GunHolder holder = mock(GunHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(gunInfoProvider.getGunFireSimulationInfo(holder)).thenReturn(null);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(audioEmitter, gunInfoProvider, taskRunner, properties);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(2L));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, sourceLocation);
    }

    @Test
    public void activateStopsSimulatesGunFireOnceEffectSourceNoLongerExists() {
        GunHolder holder = mock(GunHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(false);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 1200;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(gunInfoProvider.getGunFireSimulationInfo(holder)).thenReturn(gunFireSimulationInfo);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(1L))).thenReturn(task);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(audioEmitter, gunInfoProvider, taskRunner, properties);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();

        verifyNoInteractions(audioEmitter);
        verify(task).cancel();
    }

    @Test
    public void activateSimulatesGunFireOnceAndRemovesEffectSourceWhenFinished() {
        GunHolder holder = mock(GunHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 120;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(gunInfoProvider.getGunFireSimulationInfo(holder)).thenReturn(gunFireSimulationInfo);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(10L))).thenReturn(task);

        GunFireSimulationEffect effect = new GunFireSimulationEffect(audioEmitter, gunInfoProvider, taskRunner, properties);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(10L));

        runnableCaptor.getValue().run();
        runnableCaptor.getValue().run();

        verify(audioEmitter, times(2)).playSounds(shotSounds, sourceLocation);
        verify(source).remove();
        verify(task).cancel();
    }
}
