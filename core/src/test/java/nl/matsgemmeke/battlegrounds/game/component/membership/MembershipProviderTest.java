package nl.matsgemmeke.battlegrounds.game.component.membership;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import org.junit.jupiter.api.DisplayName;
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
class MembershipProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<MembershipService>> providers = new HashMap<>();
    @Mock
    private MembershipService membershipService;
    @InjectMocks
    private MembershipProvider membershipProvider;

    @Test
    @DisplayName("get returns MembershipService instance belonging to active game context")
    void get() {
        Provider<MembershipService> freeplayMembershipServiceProvider = mock();
        when(freeplayMembershipServiceProvider.get()).thenReturn(membershipService);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        providers.put(GameContextType.FREEPLAY_MODE, freeplayMembershipServiceProvider);

        MembershipService result = membershipProvider.get();

        assertThat(result).isEqualTo(membershipService);
    }
}
