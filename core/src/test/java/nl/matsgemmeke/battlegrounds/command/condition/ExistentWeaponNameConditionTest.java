package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistentWeaponNameConditionTest {

    @Mock
    private Translator translator;
    @Mock
    private WeaponCreator weaponCreator;
    @InjectMocks
    private ExistentWeaponNameCondition condition;

    @Test
    void validateConditionDoesNothingWhenWeaponNameExists() {
        String weaponId = "test";

        when(weaponCreator.exists(weaponId)).thenReturn(true);

        condition.validateCondition(null, null, weaponId);
    }

    @Test
    void validateConditionThrowsConditionFailedExceptionWhenWeaponNameDoesNotExist() {
        String weaponId = "test";

        when(translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("message"));
        when(weaponCreator.exists(weaponId)).thenReturn(false);

        assertThatThrownBy(() -> condition.validateCondition(null, null, weaponId))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("message");
    }
}
