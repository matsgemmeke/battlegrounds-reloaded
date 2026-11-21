package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonexistentArenaIdConditionTest {

    private static final int ARENA_ID = 1;
    private static final String ARENA_EXISTS_MESSAGE = "arena exists";

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private Translator translator;
    @InjectMocks
    private NonexistentArenaIdCondition condition;

    @Test
    @DisplayName("validateCondition passes when arena does not exist")
    void validateCondition_arenaNotExists() {
        when(arenaRegistry.exists(ARENA_ID)).thenReturn(false);

        assertThatCode(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = { ARENA_EXISTS_MESSAGE })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when arena exists")
    void validateCondition_arenaExists(String errorMessage) {
        when(arenaRegistry.exists(ARENA_ID)).thenReturn(true);
        when(translator.translateLegacyText(eq(TranslationKey.ARENA_ALREADY_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.ARENA_ALREADY_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });
    }
}
