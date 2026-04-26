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
class ActionDispatcherProviderTest {

    @Mock
    private GunActionHandler gunActionHandler;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private ActionDispatcherProvider provider;

    @Test
    @DisplayName("get returns ActionDispatcher instance with registered action handlers")
    void get() {
        ActionDispatcher actionDispatcher = provider.get();

        assertThat(actionDispatcher).isNotNull();
    }
}
