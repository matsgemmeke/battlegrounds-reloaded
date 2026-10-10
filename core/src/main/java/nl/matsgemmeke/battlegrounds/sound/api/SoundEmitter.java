package nl.matsgemmeke.battlegrounds.sound.api;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.function.Supplier;

public class SoundEmitter {

    private final Scheduler scheduler;

    @Inject
    public SoundEmitter(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void playSoundTemplate(SoundTemplate template, Location location) {
        this.playSoundTemplate(template, () -> location);
    }

    public void playSoundTemplate(SoundTemplate template, Supplier<Location> locationSupplier) {
        Location location = locationSupplier.get();
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        template.cues().forEach(cue -> this.playCue(cue, world, location));
    }

    private void playCue(SoundCue cue, World world, Location location) {
        Sound sound = cue.sound();
        float volume = cue.volume();
        float pitch = cue.pitch();
        int delay = cue.delay();

        // No need to schedule tasks without delay
        if (delay <= 0) {
            world.playSound(location, sound, volume, pitch);
            return;
        }

        Schedule schedule = scheduler.createSingleRunSchedule(cue.delay());
        schedule.addTask(() -> world.playSound(location, sound, volume, pitch));
        schedule.start();
    }
}
