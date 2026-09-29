package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.damage.FreeplayEventDamageAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventDamageAdapterProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<EventDamageAdapter>> providers = new HashMap<>();
    @Spy
    private TypeLiteral<EventDamageAdapter> typeLiteral = TypeLiteral.get(EventDamageAdapter.class);
    @InjectMocks
    private EventDamageAdapterProvider eventDamageAdapterProvider;

    @Test
    void get() {
        FreeplayEventDamageAdapter eventDamageAdapter = mock(FreeplayEventDamageAdapter.class);

        Provider<EventDamageAdapter> freeplayEventDamageAdapterProvider = mock();
        when(freeplayEventDamageAdapterProvider.get()).thenReturn(eventDamageAdapter);

        providers.put(GameContextType.FREEPLAY_MODE, freeplayEventDamageAdapterProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        EventDamageAdapter result = eventDamageAdapterProvider.get();

        assertThat(result).isEqualTo(eventDamageAdapter);
    }
}
