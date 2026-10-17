package nl.matsgemmeke.battlegrounds.sound.api;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Supplier;

public class SoundEmitter {

    private final Scheduler scheduler;

    @Inject
    public SoundEmitter(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public SoundPlayback playSoundTemplate(SoundTemplate soundTemplate, GamePlayer gamePlayer) {
        SoundPlayback playback = new SoundPlayback();
        SoundPlayFunction function = gamePlayer::playSound;
        Supplier<Location> locationSupplier = gamePlayer::getLocation;

        soundTemplate.entries().forEach(entry -> this.playEntry(entry, playback, function, locationSupplier));

        return playback;
    }

    public SoundPlayback playSoundTemplate(SoundTemplate soundTemplate, Location location) {
        return this.playSoundTemplate(soundTemplate, () -> location);
    }

    public SoundPlayback playSoundTemplate(SoundTemplate soundTemplate, Supplier<Location> locationSupplier) {
        SoundPlayback playback = new SoundPlayback();
        Location location = locationSupplier.get();
        World world = location.getWorld();

        if (world == null) {
            return playback;
        }

        SoundPlayFunction soundPlayFunction = (loc, soundCue) -> world.playSound(loc, soundCue.sound(), soundCue.volume(), soundCue.pitch());

        soundTemplate.entries().forEach(entry -> this.playEntry(entry, playback, soundPlayFunction, locationSupplier));

        return playback;
    }

    private void playEntry(SoundTemplateEntry entry, SoundPlayback playback, SoundPlayFunction function, Supplier<Location> locationSupplier) {
        SoundCue soundCue = entry.soundCue();
        int delay = entry.delay();

        // No need to schedule tasks without delay
        if (delay <= 0) {
            function.playSound(locationSupplier.get(), soundCue);
            return;
        }

        Schedule schedule = scheduler.createSingleRunSchedule(delay);
        schedule.addTask(() -> function.playSound(locationSupplier.get(), soundCue));
        schedule.start();

        playback.add(schedule);
    }
}
