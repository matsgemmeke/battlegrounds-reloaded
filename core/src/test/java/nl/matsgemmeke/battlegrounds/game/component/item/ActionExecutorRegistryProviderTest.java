package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ActionExecutorRegistryProviderTest {

    @Mock
    private ActionExecutor equipmentActionExecutor;
    @Mock
    private ActionExecutor gunActionExecutor;
    @Mock
    private ActionExecutor meleeWeaponActionExecutor;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private ActionExecutorRegistryProvider provider;

    @Test
    void getReturnsActionExecutorRegistrySeededWithDefaultActionExecutorInstances() {
        ActionExecutorRegistry actionExecutorRegistry = provider.get();

        assertThat(actionExecutorRegistry).isNotNull();
    }
}
