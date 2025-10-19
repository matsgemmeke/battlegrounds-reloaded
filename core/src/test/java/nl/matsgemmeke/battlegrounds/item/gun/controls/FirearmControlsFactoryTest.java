package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GunControlsFactoryTest {

    @Mock
    private Gun gun;
    @InjectMocks
    private GunControlsFactory controlsFactory;

    @Test
    void createMakesItemControlsWithRequiredControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";

        ItemControls<GunHolder> controls = controlsFactory.create(spec, gun);

        assertThat(controls).isNotNull();
    }

    @Test
    void createMakesItemControlsWithUseScopeAndStopScopeControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";
        spec.scopeUse = "RIGHT_CLICK";
        spec.scopeStop = "LEFT_CLICK";

        GunControlsFactory controlsFactory = new GunControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(spec, gun);

        assertThat(controls).isNotNull();
    }

    @Test
    void createMakesItemControlsWithChangeScopeMagnificationControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";
        spec.scopeUse = "RIGHT_CLICK";
        spec.scopeStop = "LEFT_CLICK";
        spec.scopeChangeMagnification = "SWAP_FROM";

        GunControlsFactory controlsFactory = new GunControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(spec, gun);

        assertThat(controls).isNotNull();
    }
}
