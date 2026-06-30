package nl.matsgemmeke.battlegrounds.command.arena;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.arena.ArenaFactory;
import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateArenaCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String SUCCESS_MESSAGE = "success";
    private static final String FAILED_MESSAGE = "fail";

    @Mock
    private ArenaFactory arenaFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private CreateArenaCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute creates arena and sends success message")
    void execute_successful() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);

        Arena arena = mock(Arena.class);
        when(arenaFactory.create(eq(ARENA_ID), any(ArenaSettings.class), eq(PLAYER_ID))).thenReturn(arena);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.addArena(gameKey, arena)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.ARENA_CREATED.getPath()))).thenReturn(new TextTemplate(SUCCESS_MESSAGE));

        commandExecutor.execute(player, ARENA_ID);

        verify(player).sendMessage(SUCCESS_MESSAGE);
    }

    @Test
    @DisplayName("execute does not add arena and sends failed message")
    void execute_failed() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);

        Arena arena = mock(Arena.class);
        when(arenaFactory.create(eq(ARENA_ID), any(ArenaSettings.class), eq(PLAYER_ID))).thenReturn(arena);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.addArena(gameKey, arena)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.ARENA_CREATION_FAILED.getPath()))).thenReturn(new TextTemplate(FAILED_MESSAGE));

        commandExecutor.execute(player, ARENA_ID);

        verify(player).sendMessage(FAILED_MESSAGE);
    }
}
