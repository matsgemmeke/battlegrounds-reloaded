package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;
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
class ExistentWeaponNameConditionTest {

    private static final String WEAPON_NAME = "test weapon";
    private static final String WEAPON_NOT_EXISTS_MESSAGE = "weapon not exists";

    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private ItemSpecRegistry itemSpecRegistry;
    @Mock
    private Translator translator;
    @InjectMocks
    private ExistentWeaponCondition existentWeaponCondition;

    @ParameterizedTest
    @ValueSource(strings = { WEAPON_NOT_EXISTS_MESSAGE })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when given weapon name does not exist in registry")
    void validateCondition_notExists(String errorMessage) {
        when(itemSpecRegistry.exists(WEAPON_NAME)).thenReturn(false);
        when(translator.translateLegacyText(eq(TranslationKey.WEAPON_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> existentWeaponCondition.validateCondition(conditionContext, execContext, WEAPON_NAME))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.WEAPON_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("weapon");
            assertThat(replacement.value()).isEqualTo(WEAPON_NAME);
        });
    }

    @Test
    @DisplayName("validateCondition does nothing when given weapon name exists in registry")
    void validateCondition_successful() {
        when(itemSpecRegistry.exists(WEAPON_NAME)).thenReturn(true);

        assertThatNoException().isThrownBy(() -> existentWeaponCondition.validateCondition(conditionContext, execContext, WEAPON_NAME));
    }
}
