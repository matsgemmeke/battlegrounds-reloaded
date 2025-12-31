package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.ItemTriggerTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThrowDeploymentTest {

    private static final double HEALTH = 20.0;
    private static final double VELOCITY = 1.5;
    private static final long COOLDOWN = 10L;

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DestructionListener destructionListener;
    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private ThrowDeployment deployment;

    @Test
    void performThrowsIllegalStateExceptionWhenNoPropertiesAreConfigured() {
        Deployer deployer = mock(Deployer.class);
        Entity deployerEntity = mock(Entity.class);

        assertThatThrownBy(() -> deployment.perform(deployer, deployerEntity, destructionListener))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot perform deployment without properties configured");
    }

    @Test
    void performReturnsOptionalWithNewInstanceOfThrowDeploymentObject() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        List<GameSound> throwSounds = Collections.emptyList();
        ProjectileEffect projectileEffect = mock(ProjectileEffect.class);
        List<ProjectileEffect> projectileEffects = List.of(projectileEffect);
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        ThrowDeploymentProperties properties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, HEALTH, VELOCITY, COOLDOWN);

        World world = mock(World.class);
        Location deployLocation = new Location(world, 0, 0, 0, 100.0f, 0);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getDeployLocation()).thenReturn(deployLocation);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        Item item = mock(Item.class);
        when(world.dropItem(deployLocation, itemStack)).thenReturn(item);

        deployment.configureProperties(properties);
        Optional<DeploymentResult> deploymentResultOptional = deployment.perform(deployer, entity, destructionListener);

        assertThat(deploymentResultOptional).hasValueSatisfying(deploymentResult -> {
            assertThat(deploymentResult.deployer()).isEqualTo(deployer);
            assertThat(deploymentResult.deploymentObject()).satisfies(deploymentObject -> {
                assertThat(deploymentObject.getHealth()).isEqualTo(HEALTH);
                assertThat(deploymentObject.isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();
            });
            assertThat(deploymentResult.triggerTarget()).isInstanceOf(ItemTriggerTarget.class);
            assertThat(deploymentResult.cooldown()).isEqualTo(COOLDOWN);
        });

        verify(audioEmitter).playSounds(throwSounds, deployLocation);
        verify(deployer).setHeldItem(null);
        verify(item).setPickupDelay(100000);
        verify(item).setVelocity(new Vector(-1.477211629518312,-0.0,-0.26047226650039546));
        verify(projectileEffect).onLaunch(eq(deployer), argThat(projectile -> projectile == deploymentResultOptional.get().deploymentObject()));
    }
}
