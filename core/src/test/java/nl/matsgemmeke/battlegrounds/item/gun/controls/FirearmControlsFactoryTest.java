package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpecification;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FirearmControlsFactoryTest {

    private Firearm firearm;

    @BeforeEach
    public void setUp() {
        firearm = mock(Firearm.class);
    }

    @Test
    public void createMakesItemControlsWithRequiredControls() {
        ControlsSpecification specification = new ControlsSpecification("LEFT_CLICK", "RIGHT_CLICK", null, null, null);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(specification, firearm);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithUseScopeAndStopScopeControls() {
        ControlsSpecification specification = new ControlsSpecification("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "LEFT_CLICK", null);

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(specification, firearm);

        assertNotNull(controls);
    }

    @Test
    public void createMakesItemControlsWithChangeScopeMagnificationControls() {
        ControlsSpecification specification = new ControlsSpecification("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "LEFT_CLICK", "SWAP_FROM");

        FirearmControlsFactory controlsFactory = new FirearmControlsFactory();
        ItemControls<GunHolder> controls = controlsFactory.create(specification, firearm);

        assertNotNull(controls);
    }
}
