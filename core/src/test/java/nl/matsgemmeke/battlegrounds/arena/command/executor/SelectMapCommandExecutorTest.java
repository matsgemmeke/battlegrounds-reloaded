package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SelectMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final String GENERIC_ERROR_MESSAGE = "generic error";
    private static final String MAP_SELECTED_MESSAGE = "map selected";
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
    @Captor
    private ArgumentCaptor<Map<String, Object>> textTemplateValuesCaptor;

    @Test
    @DisplayName("execute sends error message when given arena id has no corresponding arena")
    void execute_arenaNotFound() {
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(new TextTemplate(GENERIC_ERROR_MESSAGE));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(player).sendMessage(GENERIC_ERROR_MESSAGE);
        verify(logger).severe("Arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute sends error message when given arena does not contain a map by the given name")
    void execute_mapNotFound() {
        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(new TextTemplate(GENERIC_ERROR_MESSAGE));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(player).sendMessage(GENERIC_ERROR_MESSAGE);
        verify(logger).severe("Map Level 1 in arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute creates selection for given map and sends confirmation message")
    void execute_successful() {
        ArenaMap map = mock(ArenaMap.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        TextTemplate mapSelectedTextTemplate = mock(TextTemplate.class);
        when(mapSelectedTextTemplate.replace(anyMap())).thenReturn(MAP_SELECTED_MESSAGE);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(translator.translate(TranslationKey.MAP_SELECTED.getPath())).thenReturn(mapSelectedTextTemplate);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<ArenaMapSelection> selectionCaptor = ArgumentCaptor.forClass(ArenaMapSelection.class);
        verify(mapSelector).select(eq(PLAYER_ID), selectionCaptor.capture());

        assertThat(selectionCaptor.getValue()).satisfies(selection -> {
            assertThat(selection.arena()).isEqualTo(arena);
            assertThat(selection.map()).isEqualTo(map);
        });

        verify(mapSelectedTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).containsOnly(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(player).sendMessage(MAP_SELECTED_MESSAGE);
        verifyNoInteractions(logger);
    }
}
