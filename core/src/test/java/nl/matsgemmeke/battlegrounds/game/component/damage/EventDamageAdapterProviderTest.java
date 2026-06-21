package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.damage.FreeplayEventDamageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventDamageAdapterProviderTest {

    private GameScope gameScope;
    private TypeLiteral<EventDamageAdapter> typeLiteral;

    @BeforeEach
    void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(EventDamageAdapter.class);
    }

    @Test
    void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.FREEPLAY_MODE);
        FreeplayEventDamageAdapter eventDamageAdapter = mock(FreeplayEventDamageAdapter.class);

        Provider<EventDamageAdapter> freeplayEventDamageAdapterProvider = mock();
        when(freeplayEventDamageAdapterProvider.get()).thenReturn(eventDamageAdapter);

        Map<GameContextType, Provider<EventDamageAdapter>> implementations = Map.of(GameContextType.FREEPLAY_MODE, freeplayEventDamageAdapterProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        EventDamageAdapterProvider provider = new EventDamageAdapterProvider(gameScope, implementations, typeLiteral);
        EventDamageAdapter result = provider.get();

        assertThat(result).isEqualTo(eventDamageAdapter);
    }
}
