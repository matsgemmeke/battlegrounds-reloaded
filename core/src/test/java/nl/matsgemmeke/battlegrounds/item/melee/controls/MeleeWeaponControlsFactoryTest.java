package nl.matsgemmeke.battlegrounds.item.melee.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponControlsFactoryTest {

    @Mock
    private ItemControls<MeleeWeaponHolder> controls;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private Supplier<ItemControls<MeleeWeaponHolder>> controlsSupplier;
    @InjectMocks
    private MeleeWeaponControlsFactory controlsFactory;

    @BeforeEach
    void setUp() {
        when(controlsSupplier.get()).thenReturn(controls);
    }

    @Test
    void createReturnsEmptyInstanceWhenThrowingActionValueIsNull() {
        ControlsSpec spec = new ControlsSpec();

        ItemControls<MeleeWeaponHolder> result = controlsFactory.create(spec, meleeWeapon);

        assertThat(result).isEqualTo(controls);

        verifyNoInteractions(controls);
    }

    @Test
    void createReturnsItemControlsWithThrowingControl() {
        ControlsSpec spec = new ControlsSpec();
        spec.throwing = "RIGHT_CLICK";

        ItemControls<MeleeWeaponHolder> result = controlsFactory.create(spec, meleeWeapon);

        assertThat(result).isEqualTo(controls);

        verify(controls).addControl(eq(Action.RIGHT_CLICK), any(ThrowFunction.class));
    }
}
