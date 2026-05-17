package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DamageEventTrackerTest {

    private final DamageEventTracker damageEventTracker = new DamageEventTracker();

    @Test
    @DisplayName("saveAll returns list of damage events that were saved")
    void saveAll() {
        DamageEvent damageEvent = new DamageEvent(null, null, null, null, 0, null, 0, false, false, null);

        damageEventTracker.add(damageEvent);
        List<DamageEvent> savedEvents = damageEventTracker.saveAll();

        assertThat(savedEvents).containsExactly(damageEvent);
    }
}
