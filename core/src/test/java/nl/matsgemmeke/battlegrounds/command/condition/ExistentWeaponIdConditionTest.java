package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExistentWeaponIdConditionTest {

    private Translator translator;
    private WeaponProvider weaponProvider;

    @BeforeEach
    public void setUp() {
        this.translator = mock(Translator.class);
        this.weaponProvider = mock(WeaponProvider.class);
    }

    @Test
    public void shouldPassWhenWeaponIdExists() {
        String weaponId = "test";

        when(weaponProvider.exists(weaponId)).thenReturn(true);

        ExistentWeaponIdCondition condition = new ExistentWeaponIdCondition(weaponProvider, translator);
        condition.validateCondition(null, null, weaponId);
    }

    @Test
    public void shouldNotPassWhenWeaponIdDoesNotExist() {
        String weaponId = "test";

        when(translator.translate(TranslationKey.WEAPON_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("message"));
        when(weaponProvider.exists(weaponId)).thenReturn(false);

        ExistentWeaponIdCondition condition = new ExistentWeaponIdCondition(weaponProvider, translator);

        assertThrows(ConditionFailedException.class, () -> condition.validateCondition(null, null, weaponId));
    }
}
