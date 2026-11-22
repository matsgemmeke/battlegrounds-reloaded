package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.i18n.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveMapCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";

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
        Message mapRemovalFailedMessage = mock(Message.class);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translateMessage(eq(TranslationKey.MAP_REMOVAL_FAILED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(mapRemovalFailedMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.MAP_REMOVAL_FAILED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });

        verify(mapRemovalFailedMessage).send(player);
        verify(logger).severe("Arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute sends error message when given arena does not contain a map by the given name")
    void execute_mapNotFound() {
        Message mapRemovalFailedMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateMessage(eq(TranslationKey.MAP_REMOVAL_FAILED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(mapRemovalFailedMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.MAP_REMOVAL_FAILED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });

        verify(mapRemovalFailedMessage).send(player);
        verify(logger).severe("Map Level 1 in arena 1 is null in remove map command despite prior validation");
    }

    @Test
    @DisplayName("execute sends confirm removal message again after it ran out")
    void execute_confirmRemovalRunsOut() {
        ArenaMap map = mock(ArenaMap.class);
        Message mapConfirmRemovalMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateMessage(eq(TranslationKey.MAP_CONFIRM_REMOVAL.getPath()), any(Replacement[].class))).thenReturn(Optional.of(mapConfirmRemovalMessage));
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);
        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator, times(2)).translateMessage(eq(TranslationKey.MAP_CONFIRM_REMOVAL.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });

        verify(mapConfirmRemovalMessage, times(2)).send(player);
        verify(schedule, times(2)).start();
    }

    @Test
    @DisplayName("execute removes map from arena when confirming removal")
    void execute_successful() {
        ArenaMap map = mock(ArenaMap.class);
        Schedule schedule = mock(Schedule.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);
        Message mapRemovedMessage = mock(Message.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateMessage(eq(TranslationKey.MAP_CONFIRM_REMOVAL.getPath()), any(Replacement[].class))).thenReturn(Optional.empty());
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);
        when(translator.translateMessage(eq(TranslationKey.MAP_REMOVED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(mapRemovedMessage));

        commandExecutor.execute(player, ARENA_ID, MAP_NAME);
        commandExecutor.execute(player, ARENA_ID, MAP_NAME);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.MAP_REMOVED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });

        verify(schedule).start();
        verify(arena).removeMap(map);
        verify(setupConfiguration).removeMap(MAP_NAME);
        verify(mapRemovedMessage).send(player);
    }
}
