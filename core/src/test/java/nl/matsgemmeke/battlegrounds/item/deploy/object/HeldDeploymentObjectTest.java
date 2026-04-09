package nl.matsgemmeke.battlegrounds.item.deploy.object;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeldDeploymentObjectTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.SHEARS);

    @Mock
    private Deployer deployer;

    private HeldDeploymentObject deploymentObject;

    @BeforeEach
    void setUp() {
        deploymentObject = new HeldDeploymentObject(deployer, ITEM_STACK);
    }

    @Test
    @DisplayName("remove removes the item stack from the user")
    void remove_removesItemStackFromUser() {
        deploymentObject.remove();

        verify(deployer).removeItem(ITEM_STACK);
    }
}
