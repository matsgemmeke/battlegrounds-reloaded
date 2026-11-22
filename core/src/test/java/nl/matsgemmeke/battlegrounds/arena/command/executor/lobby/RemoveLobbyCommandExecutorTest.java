package nl.matsgemmeke.battlegrounds.arena.command.executor.lobby;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveLobbyCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String PLAYER_NAME = "TestPlayer";

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private RemoveLobbyCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute logs warning message when given arena id somehow doesn't exist")
    void execute_arenaIdNotExists() {
        Message lobbyRemoveFailedMessage = mock(Message.class);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(translator.translateMessage(TranslationKey.LOBBY_REMOVE_FAILED.getPath())).thenReturn(Optional.of(lobbyRemoveFailedMessage));

        commandExecutor.execute(player, ARENA_ID);

        verify(logger).warning("Player TestPlayer attempted to remove the lobby for arena 1, however no arena was found for this validated arena id");
        verify(lobbyRemoveFailedMessage).send(player);
    }

    @Test
    @DisplayName("execute removes the arena waiting lobby")
    void execute_successful() {
        Arena arena = mock(Arena.class);
        ArenaSetupConfiguration arenaSetupConfiguration = mock(ArenaSetupConfiguration.class);
        Message lobbyRemoveSuccessfulMessage = mock(Message.class);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(arenaSetupConfiguration);
        when(translator.translateMessage(eq(TranslationKey.LOBBY_REMOVE_SUCCESSFUL.getPath()), any(Replacement[].class))).thenReturn(Optional.of(lobbyRemoveSuccessfulMessage));

        commandExecutor.execute(player, ARENA_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.LOBBY_REMOVE_SUCCESSFUL.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });

        verify(arena).setLobbyLocation(null);
        verify(arenaSetupConfiguration).removeLobby();
        verify(lobbyRemoveSuccessfulMessage).send(player);
    }
}
