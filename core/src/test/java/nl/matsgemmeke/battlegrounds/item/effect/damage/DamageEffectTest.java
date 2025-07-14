package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DamageEffectTest {

    private DamageProcessor damageProcessor;
    private DamageProperties properties;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        damageProcessor = mock(DamageProcessor.class);
        targetFinder = mock(TargetFinder.class);

        RangeProfile rangeProfile = new RangeProfile(30.0, 1.0, 20.0, 2.0, 10.0, 3.0);
        DamageType damageType = DamageType.BULLET_DAMAGE;

        properties = new DamageProperties(rangeProfile, damageType);
    }

    @Test
    public void performCausesDamageToNearbyEntitiesAndObjects() {
        World world = mock(World.class);
        Location initiationLocation = new Location(world, 1, 0, 0);
        Location sourceLocation = new Location(world, 2, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);
        Location deploymentObjectLocation = new Location(world, 2, 0, 0);

        Deployer deployer = mock(Deployer.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, initiationLocation, source);

        GamePlayer target = mock(GamePlayer.class, Mockito.RETURNS_DEEP_STUBS);
        when(target.getEntity().getLocation()).thenReturn(targetLocation);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);

        when(targetFinder.findEnemyTargets(entityId, sourceLocation, 0.1)).thenReturn(List.of(target));
        when(targetFinder.findDeploymentObjects(entityId, sourceLocation, 0.3)).thenReturn(List.of(deploymentObject));

        DamageEffect effect = new DamageEffect(properties, damageProcessor, targetFinder);
        effect.perform(context);

        ArgumentCaptor<Damage> targetDamageCaptor = ArgumentCaptor.forClass(Damage.class);
        ArgumentCaptor<Damage> deploymentObjectDamageCaptor = ArgumentCaptor.forClass(Damage.class);
        verify(target).damage(targetDamageCaptor.capture());
        verify(damageProcessor).processDeploymentObjectDamage(eq(deploymentObject), deploymentObjectDamageCaptor.capture());

        Damage targetDamage = targetDamageCaptor.getValue();
        assertThat(targetDamage.amount()).isEqualTo(30.0);
        assertThat(targetDamage.type()).isEqualTo(DamageType.BULLET_DAMAGE);

        Damage deploymentObjectDamage = deploymentObjectDamageCaptor.getValue();
        assertThat(deploymentObjectDamage.amount()).isEqualTo(30.0);
        assertThat(deploymentObjectDamage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
    }
}
