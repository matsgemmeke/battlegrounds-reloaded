package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.ItemDeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DropDeploymentActionTest {

    private static final String ITEM_NAME = "Test Item";
    private static final double HEALTH = 20.0;
    private static final double VELOCITY = 1.0;
    private static final long COOLDOWN = 10L;
    private static final ItemStack ITEM_STACK = new ItemStack(Material.SHEARS);

    @Mock
    private Deployer deployer;
    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private World world;
    @InjectMocks
    private DropDeploymentAction deploymentAction;

    @Test
    @DisplayName("perform throws IllegalStateException when no properties are configured")
    void perform_withoutProperties() {
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        assertThatThrownBy(() -> deploymentAction.perform(context))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment drop action without properties configured");
    }

    @Test
    @DisplayName("perform returns optional with new instance of ItemDeploymentObject")
    void perform_withProperties() {
        Item item = mock(Item.class);
        Location deployLocation = new Location(world, 0, 0, 0, 100.0f, 0);
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        DropDeploymentProperties properties = new DropDeploymentProperties(itemTemplate, resistances, HEALTH, VELOCITY, COOLDOWN);
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        when(deployer.getDeployLocation()).thenReturn(deployLocation);
        when(deployer.getWorld()).thenReturn(world);
        when(world.dropItem(deployLocation, ITEM_STACK)).thenReturn(item);

        deploymentAction.configureProperties(properties);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOfSatisfying(ItemDeploymentObject.class, deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
            });
            assertThat(deploymentResult.actor()).isInstanceOf(ItemActor.class);
            assertThat(deploymentResult.cooldown()).isZero();
        });

        verify(deployer).setHeldItem(null);
        verify(item).setPickupDelay(100000);
        verify(item).setVelocity(new Vector(-0.984807753012208,-0.0, -0.1736481776669303));
    }
}
