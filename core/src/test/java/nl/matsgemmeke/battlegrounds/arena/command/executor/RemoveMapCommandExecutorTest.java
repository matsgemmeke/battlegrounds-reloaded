package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
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
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final String MAP_REMOVAL_FAILED_MESSAGE = "map removal failed";
    private static final String MAP_CONFIRM_REMOVAL_MESSAGE = "map confirm removal";
    private static final String MAP_REMOVED_MESSAGE = "map removed";

    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Translator translator;
    @InjectMocks
    private RemoveMapCommandExecutor commandExecutor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> textTemplateValuesCaptor;

    @Test
    @DisplayName("execute sends error message when given arena id has no corresponding arena")
    void execute_arenaNotFound() {
        TextTemplate mapRemovalFailedTextTemplate = mock(TextTemplate.class);
        when(mapRemovalFailedTextTemplate.replace(anyMap())).thenReturn(MAP_REMOVAL_FAILED_MESSAGE);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.MAP_REMOVAL_FAILED.getPath())).thenReturn(mapRemovalFailedTextTemplate);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(mapRemovalFailedTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).containsOnly(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(player).sendMessage(MAP_REMOVAL_FAILED_MESSAGE);
        verify(logger).severe("Arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute sends error message when given arena does not contain a map by the given name")
    void execute_mapNotFound() {
        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        TextTemplate mapRemovalFailedTextTemplate = mock(TextTemplate.class);
        when(mapRemovalFailedTextTemplate.replace(anyMap())).thenReturn(MAP_REMOVAL_FAILED_MESSAGE);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.MAP_REMOVAL_FAILED.getPath())).thenReturn(mapRemovalFailedTextTemplate);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(mapRemovalFailedTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).containsOnly(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(player).sendMessage(MAP_REMOVAL_FAILED_MESSAGE);
        verify(logger).severe("Map Level 1 in arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute sends confirm removal message again after it ran out")
    void execute_confirmRemovalRunsOut() {
        ArenaMap map = mock(ArenaMap.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        TextTemplate mapConfirmRemovalTextTemplate = mock(TextTemplate.class);
        when(mapConfirmRemovalTextTemplate.replace(anyMap())).thenReturn(MAP_CONFIRM_REMOVAL_MESSAGE);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.MAP_CONFIRM_REMOVAL.getPath())).thenReturn(mapConfirmRemovalTextTemplate);
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);
        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(mapConfirmRemovalTextTemplate, times(2)).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).containsOnly(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(player, times(2)).sendMessage(MAP_CONFIRM_REMOVAL_MESSAGE);
        verify(schedule, times(2)).start();
    }

    @Test
    @DisplayName("execute removes map from arena when confirming removal")
    void execute_successful() {
        ArenaMap map = mock(ArenaMap.class);
        Schedule schedule = mock(Schedule.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        TextTemplate mapConfirmRemovalTextTemplate = mock(TextTemplate.class);
        when(mapConfirmRemovalTextTemplate.replace(anyMap())).thenReturn(MAP_CONFIRM_REMOVAL_MESSAGE);

        TextTemplate mapRemovedTextTemplate = mock(TextTemplate.class);
        when(mapRemovedTextTemplate.replace(anyMap())).thenReturn(MAP_REMOVED_MESSAGE);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.MAP_CONFIRM_REMOVAL.getPath())).thenReturn(mapConfirmRemovalTextTemplate);
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);
        when(translator.translate(TranslationKey.MAP_REMOVED.getPath())).thenReturn(mapRemovedTextTemplate);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);
        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        verify(mapConfirmRemovalTextTemplate).replace(textTemplateValuesCaptor.capture());

        assertThat(textTemplateValuesCaptor.getValue()).containsOnly(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );

        verify(player).sendMessage(MAP_CONFIRM_REMOVAL_MESSAGE);
        verify(schedule).start();
        verify(arena).removeMap(map);
        verify(setupConfiguration).removeMap(MAP_NAME);
        verify(player).sendMessage(MAP_REMOVED_MESSAGE);
    }
}
