package nl.matsgemmeke.battlegrounds.sound;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundEmitterTest {

    private static final String SEQUENCE_ID = "equipment-reload-1";
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 1.5f;
    private static final int DELAY = 5;

    @Mock
    private Scheduler scheduler;
    @InjectMocks
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("playSoundSequence returns empty sound playback when given location has no world")
    void playSoundSequence_locationWithoutWorld() {
        SoundCue soundCue = new SoundCue(null, VOLUME, PITCH);
        SoundSequenceEntry entry = new SoundSequenceEntry(soundCue, DELAY);
        SoundSequence soundSequence = new SoundSequence(SEQUENCE_ID, List.of(entry));

        Location location = new Location(null, 1, 2, 3);

        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, location);

        assertThat(soundPlayback).isNotNull();

        verifyNoInteractions(scheduler);
    }

    @Test
    @DisplayName("playSoundSequence plays given sound sequence without delay and returns empty sound playback")
    void playSoundSequence_noDelay() {
        SoundCue soundCue = new SoundCue(null, VOLUME, PITCH);
        SoundSequenceEntry entry = new SoundSequenceEntry(soundCue, 0);
        SoundSequence soundSequence = new SoundSequence(SEQUENCE_ID, List.of(entry));

        World world = mock(World.class);
        Location location = new Location(world, 1, 2, 3);

        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, location);

        assertThat(soundPlayback).isNotNull();

        verify(world).playSound(location, (Sound) null, VOLUME, PITCH);
        verifyNoInteractions(scheduler);
    }

    @Test
    @DisplayName("playSoundSequence plays given sound sequence with delay")
    void playSoundSequence_delay() {
        SoundCue soundCue = new SoundCue(null, VOLUME, PITCH);
        SoundSequenceEntry entry = new SoundSequenceEntry(soundCue, DELAY);
        SoundSequence soundSequence = new SoundSequence(SEQUENCE_ID, List.of(entry));

        World world = mock(World.class);
        Location location = new Location(world, 1, 2, 3);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(DELAY)).thenReturn(schedule);

        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, location);
        soundPlayback.cancel();

        assertThat(soundPlayback).isNotNull();

        verify(world).playSound(location, (Sound) null, VOLUME, PITCH);
        verify(schedule).stop();
    }

    @Test
    @DisplayName("playSoundSequence plays given sound sequnce without delay and returns empty sound playback")
    void playSoundSequence_singleReceiverNoDelay() {
        SoundCue soundCue = new SoundCue(null, VOLUME, PITCH);
        SoundSequenceEntry entry = new SoundSequenceEntry(soundCue, 0);
        SoundSequence soundSequence = new SoundSequence(SEQUENCE_ID, List.of(entry));
        SoundReceiver soundReceiver = mock(SoundReceiver.class);
        Location location = new Location(null, 1, 2, 3);

        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, soundReceiver, () -> location);

        assertThat(soundPlayback).isNotNull();

        verify(soundReceiver).playSound(location, soundCue);
        verifyNoInteractions(scheduler);
    }

    @Test
    @DisplayName("playSoundSequence plays given sound sequence with delay")
    void playSoundSequence_singleReceiverDelay() {
        SoundCue soundCue = new SoundCue(null, VOLUME, PITCH);
        SoundSequenceEntry entry = new SoundSequenceEntry(soundCue, DELAY);
        SoundSequence soundSequence = new SoundSequence(SEQUENCE_ID, List.of(entry));
        SoundReceiver soundReceiver = mock(SoundReceiver.class);
        Location location = new Location(null, 1, 2, 3);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(DELAY)).thenReturn(schedule);

        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, soundReceiver, () -> location);
        soundPlayback.cancel();

        assertThat(soundPlayback).isNotNull();

        verify(soundReceiver).playSound(location, soundCue);
        verify(schedule).stop();
    }
}
