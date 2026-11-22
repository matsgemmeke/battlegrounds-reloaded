package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
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
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SelectMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ArenaMapSelector mapSelector;
    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private SelectMapCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute sends error message when given arena id has no corresponding arena")
    void execute_arenaNotFound() {
        Message genericErrorMessage = mock(Message.class);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translateMessage(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(Optional.of(genericErrorMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(logger).severe("Arena 1 is null in remove map command despite prior validation");
        verify(genericErrorMessage).send(player);
    }

    @Test
    @DisplayName("execute sends error message when given arena does not contain a map by the given name")
    void execute_mapNotFound() {
        Message genericErrorMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateMessage(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(Optional.of(genericErrorMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(logger).severe("Map Level 1 in arena 1 is null in remove map command despite prior validation");
        verify(genericErrorMessage).send(player);
    }

    @Test
    @DisplayName("execute creates selection for given map and sends confirmation message")
    void execute_successful() {
        ArenaMap map = mock(ArenaMap.class);
        Message mapSelectedMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(translator.translateMessage(eq(TranslationKey.MAP_SELECTED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(mapSelectedMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<ArenaMapSelection> selectionCaptor = ArgumentCaptor.forClass(ArenaMapSelection.class);
        verify(mapSelector).select(eq(PLAYER_ID), selectionCaptor.capture());

        assertThat(selectionCaptor.getValue()).satisfies(selection -> {
            assertThat(selection.arena()).isEqualTo(arena);
            assertThat(selection.map()).isEqualTo(map);
        });

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.MAP_SELECTED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });

        verify(mapSelectedMessage).send(player);
        verifyNoInteractions(logger);
    }
}
