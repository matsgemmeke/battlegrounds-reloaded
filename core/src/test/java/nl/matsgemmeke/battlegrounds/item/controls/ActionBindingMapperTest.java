package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ActionBindingMapperTest {

    private final ActionBindingMapper mapper = new ActionBindingMapper();

    @Test
    @DisplayName("toBinding returns ActionBinding with data from given control specification and given function")
    void toBinding() {
        ControlSpec spec = new ControlSpec();
        spec.action = "LEFT_CLICK";
        spec.priority = 1;
        spec.stopsChain = true;
        spec.blocking = true;
        spec.cancelsEvent = true;

        Function<GunUser> function = mock();

        ActionBinding<GunUser> binding = mapper.toBinding(spec, function);

        assertThat(binding.function()).isEqualTo(function);
        assertThat(binding.priority()).isEqualTo(1);
        assertThat(binding.stopsChain()).isTrue();
        assertThat(binding.blocking()).isTrue();
        assertThat(binding.cancelsEvent()).isTrue();
    }
}
