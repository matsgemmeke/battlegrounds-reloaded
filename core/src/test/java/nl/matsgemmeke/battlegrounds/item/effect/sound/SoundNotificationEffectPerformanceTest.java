package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundNotificationEffectPerformanceTest {

    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Sound SOUND = Sound.AMBIENT_CAVE;
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 2.0f;

    private GameSound sound;
    private SoundNotificationEffectPerformance performance;

    @BeforeEach
    void setUp() {
        sound = mock(GameSound.class);
        performance = new SoundNotificationEffectPerformance(List.of(sound));
    }

    @Test
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performPlaysNoSoundsWhenEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(zombie, source, INITIATION_LOCATION);

        performance.perform(context);

        verifyNoInteractions(sound);
    }

    @Test
    void performPlaysSoundsOnlyForPlayerEntity() {
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location playerLocation = new Location(null, 0, 0, 0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);

        ItemEffectContext context = new ItemEffectContext(player, source, INITIATION_LOCATION);

        when(sound.getSound()).thenReturn(SOUND);
        when(sound.getVolume()).thenReturn(VOLUME);
        when(sound.getPitch()).thenReturn(PITCH);

        performance.perform(context);

        verify(player).playSound(playerLocation, SOUND, VOLUME, PITCH);
    }

    @Test
    void cancelCancelsAllTriggerRuns() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        performance.addTriggerRun(triggerRun);
        performance.cancel();

        verify(triggerRun).cancel();
    }
}
