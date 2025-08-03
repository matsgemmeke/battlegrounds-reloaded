package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FirearmControlsFactoryTest {

    private Firearm firearm;

    @BeforeEach
    public void setUp() {
        firearm = mock(Firearm.class);
    }

    @Test
    public void createMakesItemControlsWithRequiredControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(spec, firearm);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithUseScopeAndStopScopeControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";
        spec.scopeUse = "RIGHT_CLICK";
        spec.scopeStop = "LEFT_CLICK";

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(spec, firearm);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithChangeScopeMagnificationControls() {
        ControlsSpec spec = new ControlsSpec();
        spec.reload = "LEFT_CLICK";
        spec.shoot = "RIGHT_CLICK";
        spec.scopeUse = "RIGHT_CLICK";
        spec.scopeStop = "LEFT_CLICK";
        spec.scopeChangeMagnification = "SWAP_FROM";

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(spec, firearm);

        assertThat(controls).isNotNull();
    }
}
