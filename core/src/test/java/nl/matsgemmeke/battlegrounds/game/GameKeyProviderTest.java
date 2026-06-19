package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.OutOfScopeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameKeyProviderTest {

    @Mock
    private GameScope gameScope;
    @InjectMocks
    private GameKeyProvider provider;

    @Test
    @DisplayName("get throws OutOfScopeException when scope has not entered any game context")
    void get_withoutEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("No game context active");
    }

    @Test
    @DisplayName("get returns GameKey of entered game context")
    void get_successful() {
        GameKey gameKey = GameKey.ofFreeplay();
        GameContext gameContext = new GameContext(gameKey, GameContextType.OPEN_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        GameKey result = provider.get();

        assertThat(result).isEqualTo(gameKey);
    }
}
