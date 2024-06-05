package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExistentWeaponIdConditionTest {

    private Translator translator;
    private WeaponProvider weaponProvider;

    @Before
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

    @Test(expected = ConditionFailedException.class)
    public void shouldNotPassWhenWeaponIdDoesNotExist() {
        String weaponId = "test";

        when(weaponProvider.exists(weaponId)).thenReturn(false);

        ExistentWeaponIdCondition condition = new ExistentWeaponIdCondition(weaponProvider, translator);
        condition.validateCondition(null, null, weaponId);
    }
}
