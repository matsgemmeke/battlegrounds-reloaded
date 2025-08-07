package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameContextTest {

    @Test
    public void getOrCreateReturnsResultFromGivenProviderWhenKeyIsNotStored() {
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> provider = mock();
        when(provider.get()).thenReturn(playerRegistry);

        GameContext gameContext = new GameContext();
        PlayerRegistry result = gameContext.getOrCreate(key, provider);

        assertThat(result).isEqualTo(playerRegistry);
    }

    @Test
    public void getOrCreateReturnsStoredInstanceWhenKeyIsAlreadyStored() {
        PlayerRegistry firstPlayerRegistry = mock(PlayerRegistry.class);
        PlayerRegistry secondPlayerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> firstProvider = mock();
        when(firstProvider.get()).thenReturn(firstPlayerRegistry);

        Provider<PlayerRegistry> secondProvider = mock();
        when(secondProvider.get()).thenReturn(secondPlayerRegistry);

        GameContext gameContext = new GameContext();
        gameContext.getOrCreate(key, firstProvider);
        PlayerRegistry result = gameContext.getOrCreate(key, secondProvider);

        assertThat(result).isEqualTo(firstPlayerRegistry);
        assertThat(result).isNotEqualTo(secondPlayerRegistry);
    }
}
