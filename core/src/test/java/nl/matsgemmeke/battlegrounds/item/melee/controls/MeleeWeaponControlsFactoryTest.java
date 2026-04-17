package nl.matsgemmeke.battlegrounds.item.melee.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.melee.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponControlsFactoryTest {

    @Spy
    private ActionBindingMapper actionBindingMapper;
    @Mock
    private ItemControls<MeleeWeaponUser> controls;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private Supplier<ItemControls<MeleeWeaponUser>> controlsSupplier;
    @Captor
    private ArgumentCaptor<ActionBinding<MeleeWeaponUser>> bindingCaptor;
    @InjectMocks
    private MeleeWeaponControlsFactory controlsFactory;

    @BeforeEach
    void setUp() {
        when(controlsSupplier.get()).thenReturn(controls);
    }

    @Test
    @DisplayName("creates returns item controls with reload control")
    void create_withReloadControl() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("src/main/resources/items/melee_weapons/ballistic_knife.yml");
        spec.controls.throwing = null;

        ItemControls<MeleeWeaponUser> controls = controlsFactory.create(spec.controls, meleeWeapon);

        verify(controls).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ReloadFunction.class);
        });
    }

    @Test
    @DisplayName("creates returns item controls with throwing control")
    void create_withThrowingControl() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("src/main/resources/items/melee_weapons/ballistic_knife.yml");
        spec.controls.reload = null;

        ItemControls<MeleeWeaponUser> controls = controlsFactory.create(spec.controls, meleeWeapon);

        verify(controls).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ThrowFunction.class);
        });
    }

    private MeleeWeaponSpec createMeleeWeaponSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
