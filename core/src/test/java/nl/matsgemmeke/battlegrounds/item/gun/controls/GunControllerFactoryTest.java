package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
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
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunControllerFactoryTest {

    private static final UUID GUN_ID = UUID.randomUUID();

    @Spy
    private ActionBindingMapper actionBindingMapper;
    @Mock
    private Gun gun;
    @Mock
    private ItemController<GunUser> controller;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @Mock
    private Supplier<ItemController<GunUser>> controllerSupplier;
    @Captor
    private ArgumentCaptor<ActionBinding<GunUser>> bindingCaptor;
    @InjectMocks
    private GunControllerFactory controllerFactory;

    @BeforeEach
    void setUp() {
        when(controllerSupplier.get()).thenReturn(controller);
        when(gun.getId()).thenReturn(GUN_ID);
    }

    @Test
    @DisplayName("create returns ItemController with only required controls")
    void create_requiredControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/assault_rifles/ak-47.yml");

        ItemController<GunUser> controller = controllerFactory.create(spec.controls, gun);

        verify(controller).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controller).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );

        verify(itemControllerRegistry).registerGunController(GUN_ID, controller);
    }

    @Test
    @DisplayName("create returns ItemController with use scope and stop scope controls")
    void create_useScopeAndStopScopeControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/sniper_rifles/sv-98.yml");
        spec.controls.scopeChangeMagnification = null;

        ItemController<GunUser> controller = controllerFactory.create(spec.controls, gun);

        verify(controller, times(2)).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controller, times(2)).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(UseScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(StopScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );

        verify(itemControllerRegistry).registerGunController(GUN_ID, controller);
    }

    @Test
    @DisplayName("create returns ItemController wth change scope magnification controls")
    void create_changeMagnificationControls() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/sniper_rifles/sv-98.yml");

        ItemController<GunUser> controller = controllerFactory.create(spec.controls, gun);

        verify(controller, times(2)).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());
        verify(controller, times(2)).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());
        verify(controller).bind(eq(Action.SWAP_FROM), bindingCaptor.capture());

        assertThat(bindingCaptor.getAllValues()).satisfiesExactlyInAnyOrder(
                binding -> assertThat(binding.function()).isInstanceOf(ChangeScopeMagnificationFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(UseScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(StopScopeFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ReloadFunction.class),
                binding -> assertThat(binding.function()).isInstanceOf(ShootFunction.class)
        );

        verify(itemControllerRegistry).registerGunController(GUN_ID, controller);
    }

    private GunSpec createGunSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
