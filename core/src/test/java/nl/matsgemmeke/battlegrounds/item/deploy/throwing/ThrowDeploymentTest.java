package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ThrowDeploymentTest {

    private static final double HEALTH = 20.0;
    private static final double VELOCITY = 1.5;
    private static final long COOLDOWN = 10L;

    @Test
    public void performReturnsNewInstanceOfThrowDeploymentObject() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        List<GameSound> throwSounds = Collections.emptyList();
        ProjectileEffect projectileEffect = mock(ProjectileEffect.class);
        List<ProjectileEffect> projectileEffects = List.of(projectileEffect);
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.0);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(itemStack);

        ThrowDeploymentProperties deploymentProperties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, HEALTH, VELOCITY, COOLDOWN);
        AudioEmitter audioEmitter = mock(AudioEmitter.class);

        World world = mock(World.class);
        Location deployLocation = new Location(world, 0, 0, 0, 100.0f, 0);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getDeployLocation()).thenReturn(deployLocation);

        Entity entity = mock(Entity.class);
        when(entity.getWorld()).thenReturn(world);

        Item item = mock(Item.class);
        when(world.dropItem(deployLocation, itemStack)).thenReturn(item);

        ThrowDeployment deployment = new ThrowDeployment(deploymentProperties, audioEmitter);
        DeploymentResult result = deployment.perform(deployer, entity);

        assertThat(result.object()).isInstanceOf(ThrowDeploymentObject.class);
        assertThat(result.object().getCooldown()).isEqualTo(COOLDOWN);
        assertThat(result.object().getHealth()).isEqualTo(HEALTH);
        assertThat(result.object().isImmuneTo(DamageType.BULLET_DAMAGE)).isTrue();

        verify(audioEmitter).playSounds(throwSounds, deployLocation);
        verify(deployer).setHeldItem(null);
        verify(item).setPickupDelay(100000);
        verify(item).setVelocity(new Vector(-1.477211629518312,-0.0,-0.26047226650039546));
        verify(projectileEffect).onLaunch(any(Projectile.class));
    }
}
