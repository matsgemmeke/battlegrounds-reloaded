package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExistentLobbyConditionTest {

    private static final int ARENA_ID = 1;
    private static final String LOBBY_NOT_EXISTS_TEXT = "lobby not exists";

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private Translator translator;
    @InjectMocks
    private ExistentLobbyCondition condition;

    @Test
    @DisplayName("validateCondition does nothing when given arena id is not registered")
    void validateCondition_arenaIdNotRegistered() {
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID));
    }

    @Test
    @DisplayName("validateCondition does nothing when given arena id matches with an arena with a lobby location")
    void validationCondition_arenaWithLobby() {
        Location lobbyLocation = new Location(null, 1.1, 2.2, 3.3, 90.0f, 0.0f);

        Arena arena = mock(Arena.class);
        when(arena.getLobbyLocation()).thenReturn(Optional.of(lobbyLocation));

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID));
    }

    @ParameterizedTest
    @ValueSource(strings = { LOBBY_NOT_EXISTS_TEXT })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when given arena id matches with an arena without a lobby location")
    void validationCondition_arenaWithoutLobby(String errorMessage) {
        Arena arena = mock(Arena.class);
        when(arena.getLobbyLocation()).thenReturn(Optional.empty());

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateLegacyText(eq(TranslationKey.LOBBY_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.LOBBY_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });
    }
}
