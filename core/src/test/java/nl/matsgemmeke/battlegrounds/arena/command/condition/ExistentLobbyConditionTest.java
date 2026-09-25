package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistentLobbyConditionTest {

    private static final int ARENA_ID = 1;
    private static final String LOBBY_NOT_EXISTS_TEXT = LanguageFixture.getTranslation(TranslationKey.LOBBY_NOT_EXISTS.getPath());

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

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when given arena id matches with an arena without a lobby location")
    void validationCondition_arenaWithoutLobby() {
        Arena arena = mock(Arena.class);
        when(arena.getLobbyLocation()).thenReturn(Optional.empty());

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.LOBBY_NOT_EXISTS.getPath())).thenReturn(new TextTemplate(LOBBY_NOT_EXISTS_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("&cArena 1 has no waiting lobby set up.");
    }
}
