package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.ItemDeploymentObject;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrowDeploymentActionTest {

    private static final String ITEM_NAME = "Test Item";
    private static final double HEALTH = 20.0;
    private static final double VELOCITY = 1.5;
    private static final long COOLDOWN = 10L;
    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);

    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private SoundEmitter soundEmitter;
    @InjectMocks
    private ThrowDeploymentAction deploymentAction;

    @Test
    @DisplayName("perform throws IllegalStateException when no properties are configured")
    void perform_noPropertiesConfigured() {
        Deployer deployer = mock(Deployer.class);
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        assertThatThrownBy(() -> deploymentAction.perform(context))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment without properties configured");
    }

    @Test
    @DisplayName("perform returns optional with new instance of throw deployment object")
    void perform_successful() {
        ItemStack itemStack = mock(ItemStack.class);
        ProjectileEffect projectileEffect = mock(ProjectileEffect.class);
        List<ProjectileEffect> projectileEffects = List.of(projectileEffect);
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        World world = mock(World.class);
        Location deployLocation = new Location(world, 0, 0, 0, 100.0f, 0);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getDeployLocation()).thenReturn(deployLocation);
        when(deployer.getWorld()).thenReturn(world);

        Item item = mock(Item.class);
        when(world.dropItem(deployLocation, itemStack)).thenReturn(item);

        ThrowDeploymentProperties properties = new ThrowDeploymentProperties(itemTemplate, SOUND_SEQUENCE, projectileEffects, resistances, HEALTH, VELOCITY, COOLDOWN);
        DeploymentContext context = new DeploymentContext(ITEM_NAME, deployer, destructionListener);

        deploymentAction.configureProperties(properties);
        Optional<DeploymentResult> deploymentResultOptional = deploymentAction.perform(context);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).isInstanceOfSatisfying(ItemDeploymentObject.class, deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
            });
            assertThat(deploymentResult.actor()).isInstanceOf(ItemActor.class);
            assertThat(deploymentResult.cooldown()).isEqualTo(COOLDOWN);
        });

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == deployLocation));
        verify(deployer).setHeldItem(null);
        verify(item).setPickupDelay(100000);
        verify(item).setVelocity(new Vector(-1.477211629518312,-0.0,-0.26047226650039546));
        verify(projectileEffect).onLaunch(eq(deployer), argThat(projectile -> projectile == deploymentResultOptional.get().deploymentObject()));
    }
}
