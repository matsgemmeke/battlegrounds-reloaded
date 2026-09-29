package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaFactory;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
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

    @Mock
    private ArenaFactory arenaFactory;
    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private CreateArenaCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute creates arena and sends success message")
    void execute() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);

        Arena arena = mock(Arena.class);
        when(arenaFactory.create(eq(ARENA_ID), any(ArenaSettings.class), eq(PLAYER_ID))).thenReturn(arena);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(translator.translate(eq(TranslationKey.ARENA_CREATED.getPath()))).thenReturn(new TextTemplate(SUCCESS_MESSAGE));

        commandExecutor.execute(player, ARENA_ID);

        verify(arenaRegistry).addArena(gameKey, arena);
        verify(player).sendMessage(SUCCESS_MESSAGE);
    }
}
