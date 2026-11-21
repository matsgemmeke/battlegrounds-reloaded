package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistentMapNameConditionTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";
    private static final String GENERIC_ERROR_MESSAGE = "generic error";
    private static final String ARENA_NOT_EXISTS_MESSAGE = "arena not exists";
    private static final String MAP_NOT_EXISTS_MESSAGE = "map not exists";

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
    private ExistentMapNameCondition condition;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @ParameterizedTest
    @ValueSource(strings = { GENERIC_ERROR_MESSAGE })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when the execution context does not have an argument for arena id")
    void validateCondition_arenaIdNotSpecified(String errorMessage) {
        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(null);
        when(translator.translateLegacyText(TranslationKey.GENERIC_ERROR.getPath())).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        verify(logger).warning("ExistentMapNameCondition: argument \"arena-id\" was not resolved; check parameter order/name");
    }

    @ParameterizedTest
    @ValueSource(strings = { ARENA_NOT_EXISTS_MESSAGE })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when the given arena id not registered")
    void validateCondition_arenaIdNotRegistered(String errorMessage) {
        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(translator.translateLegacyText(eq(TranslationKey.ARENA_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.ARENA_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("bg-arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { MAP_NOT_EXISTS_MESSAGE })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when the arena does not have a map by the same name")
    void validateCondition_arenaDoesNotHaveMap(String errorMessage) {
        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.empty());

        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(translator.translateLegacyText(eq(TranslationKey.MAP_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.MAP_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("bg-arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("bg-map-name");
            assertThat(replacement.value()).isEqualTo(MAP_NAME);
        });
    }

    @Test
    @DisplayName("validateCondition does nothing when arena has a map by the given name")
    void validateCondition_successful() {
        ArenaMap map = mock(ArenaMap.class);

        Arena arena = mock(Arena.class);
        when(arena.getMap(MAP_NAME)).thenReturn(Optional.of(map));

        when(execContext.getResolvedArg("arena-id", Integer.class)).thenReturn(ARENA_ID);
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));

        assertThatCode(() -> condition.validateCondition(conditionContext, execContext, MAP_NAME)).doesNotThrowAnyException();
    }
}
