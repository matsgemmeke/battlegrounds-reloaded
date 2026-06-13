package nl.matsgemmeke.battlegrounds.storage.stats;

import nl.matsgemmeke.battlegrounds.fixture.DamageEventFixture;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatsStorageTest {

    @Mock
    private DamageEventRepository damageEventRepository;
    @InjectMocks
    private StatsStorage statsStorage;

    @Test
    @DisplayName("saveDamageEvent saves damage event to repository")
    void saveDamageEvent() {
        DamageEvent damageEvent = DamageEventFixture.createDefault();
        List<DamageEvent> damageEvents = List.of(damageEvent);

        statsStorage.saveDamageEvents(damageEvents);

        verify(damageEventRepository).save(damageEvents);
    }
}
