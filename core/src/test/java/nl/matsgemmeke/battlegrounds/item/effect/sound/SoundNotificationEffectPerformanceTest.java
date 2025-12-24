package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    @Mock
    private DamageSource damageSource;
    @Mock
    private GameSound sound;
    @Mock
    private ItemEffectSource effectSource;

    private SoundNotificationEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new SoundNotificationEffectPerformance(List.of(sound));
    }

    @Test
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void cancelCancelsAllTriggerRuns() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        performance.addTriggerRun(triggerRun);
        performance.cancel();

        verify(triggerRun).cancel();
    }
}
