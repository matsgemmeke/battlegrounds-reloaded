package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.*;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;
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
class GunControlsFactoryTest {

    @Spy
    private ActionBindingMapper actionBindingMapper;
    @Mock
    private Gun gun;
    @Mock
    private ItemControls<GunUser> controls;
    @Mock
    private Supplier<ItemControls<GunUser>> controlsSupplier;
    @Captor
    private ArgumentCaptor<ActionBinding<GunUser>> bindingCaptor;
    @InjectMocks
    private GunControlsFactory controlsFactory;

    @BeforeEach
    void setUp() {
        when(controlsSupplier.get()).thenReturn(controls);
    }

    @Test
    @DisplayName("create returns ItemControls with only required controls")
    void create_requiredControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/assault_rifles/ak-47.yml");

        ItemControls<GunUser> controls = controlsFactory.create(spec.controls, gun);

        verify(controls).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controls).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );
    }

    @Test
    @DisplayName("create returns ItemControls with use scope and stop scope controls")
    void create_useScopeAndStopScopeControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/sniper_rifles/sv-98.yml");
        spec.controls.scopeChangeMagnification = null;

        ItemControls<GunUser> controls = controlsFactory.create(spec.controls, gun);

        verify(controls, times(2)).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controls, times(2)).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(UseScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(StopScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );
    }

    @Test
    @DisplayName("create returns ItemControls wth change scope magnification controls")
    void create_changeMagnificationControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/sniper_rifles/sv-98.yml");

        ItemControls<GunUser> controls = controlsFactory.create(spec.controls, gun);

        verify(controls, times(2)).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controls, times(2)).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());
        verify(controls).bind(eq(Action.SWAP_FROM), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(ChangeScopeMagnificationFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(UseScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(StopScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );
    }

    private GunSpec createGunSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
