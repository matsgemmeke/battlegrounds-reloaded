package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunActionHandler;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemInteractionDispatcherProviderTest {

    @Mock
    private GunActionHandler gunActionHandler;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private ItemInteractionDispatcherProvider provider;

    @Test
    @DisplayName("get returns ItemInteractionDispatcher instance with registered action handlers")
    void get() {
        ItemInteractionDispatcher itemInteractionDispatcher = provider.get();

        assertThat(itemInteractionDispatcher).isNotNull();
    }
}
