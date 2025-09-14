package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ActionExecutorRegistryProviderTest {

    @Test
    public void getReturnsActionExecutorRegistrySeededWithDefaultActionExecutorInstances() {
        NamespacedKeyCreator namespacedKeyCreator = mock(NamespacedKeyCreator.class);
        ActionExecutor equipmentActionExecutor = mock(ActionExecutor.class);
        ActionExecutor gunActionExecutor = mock(ActionExecutor.class);

        ActionExecutorRegistryProvider provider = new ActionExecutorRegistryProvider(namespacedKeyCreator, equipmentActionExecutor, gunActionExecutor);
        ActionExecutorRegistry actionExecutorRegistry = provider.get();

        assertThat(actionExecutorRegistry).isNotNull();
    }
}
