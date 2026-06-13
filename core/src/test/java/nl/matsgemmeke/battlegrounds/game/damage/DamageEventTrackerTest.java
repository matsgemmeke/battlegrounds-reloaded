package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.fixture.DamageEventFixture;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DamageEventTrackerTest {

    private final DamageEventTracker damageEventTracker = new DamageEventTracker();

    @Test
    @DisplayName("getTrackedDamageEvents returns list of damage events that were tracked")
    void getTrackedDamageEvents() {
        DamageEvent damageEvent = DamageEventFixture.createDefault();

        damageEventTracker.add(damageEvent);
        List<DamageEvent> savedEvents = damageEventTracker.getTrackedDamageEvents();

        assertThat(savedEvents).containsExactly(damageEvent);
    }
}
