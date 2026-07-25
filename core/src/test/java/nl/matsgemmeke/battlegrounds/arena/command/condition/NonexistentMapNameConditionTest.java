package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NonexistentMapNameConditionTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final String GENERIC_ERROR_MESSAGE = "generic error";
    private static final String ARENA_NOT_EXISTS_MESSAGE = "arena not exists";
    private static final String MAP_ALREADY_EXISTS_MESSAGE = "map already exists";

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private Logger logger;
    @Mock
    private Translator translator;
    @InjectMocks
    private NonexistentMapNameCondition condition;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the execution context does not have an argument for arena id")
    void validateCondition_arenaIdNotSpecified() {
        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(null);
        when(translator.translate(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(new TextTemplate(GENERIC_ERROR_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(GENERIC_ERROR_MESSAGE);

        verify(logger).warning("NonexistentMapNameCondition: argument \"arena-id\" was not resolved; check parameter order/name");
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the given arena id not registered")
    void validateCondition_arenaIdNotRegistered() {
        TextTemplate arenaNotExistsTextTemplate = mock(TextTemplate.class);
        when(arenaNotExistsTextTemplate.replace(anyMap())).thenReturn(ARENA_NOT_EXISTS_MESSAGE);

        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.ARENA_NOT_EXISTS.getPath())).thenReturn(arenaNotExistsTextTemplate);

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ARENA_NOT_EXISTS_MESSAGE);

        verify(arenaNotExistsTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(entry("bg_arena", ARENA_ID));
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the arena already has a map by the same name")
    void validateCondition_arenaAlreadyHasMap() {
        ArenaMap map = mock(ArenaMap.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        TextTemplate mapAlreadyExistsTextTemplate = mock(TextTemplate.class);
        when(mapAlreadyExistsTextTemplate.replace(anyMap())).thenReturn(MAP_ALREADY_EXISTS_MESSAGE);

        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translate(TranslationKey.MAP_ALREADY_EXISTS.getPath())).thenReturn(mapAlreadyExistsTextTemplate);

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(MAP_ALREADY_EXISTS_MESSAGE);

        verify(mapAlreadyExistsTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(
                entry("bg_arena", ARENA_ID),
                entry("bg_map", MAP_NAME)
        );
    }

    @Test
    @DisplayName("validateCondition does nothing when arena does not have a map by the given name")
    void validateCondition_successful() {
        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));

        assertThatCode(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME)).doesNotThrowAnyException();
    }
}
