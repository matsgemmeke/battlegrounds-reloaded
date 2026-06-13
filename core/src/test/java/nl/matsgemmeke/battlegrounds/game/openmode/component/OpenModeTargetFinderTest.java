package nl.matsgemmeke.battlegrounds.game.openmode.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.PotionEffectReceiver;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenModeTargetFinderTest {

    private static final double RANGE = 0.1;
    private static final UUID ENTITY_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private DeploymentObjectRegistry deploymentObjectRegistry;
    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private MobRegistry mobRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private OpenModeTargetFinder targetFinder;

    @Test
    void findEnemyTargetsReturnsEmptyListWhenGivenLocationWorldIsNull() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityId, location, 0.1);

        assertThat(targets).isEmpty();
    }

    @Test
    void findEnemyTargetsReturnsNearbyTargetsButWithoutEntitiesContainingTheGivenEntityId() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        double range = 0.1;

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(entityUniqueId);

        Player targetPlayer = mock(Player.class);
        when(targetPlayer.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(entityUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.getUniqueId()).thenReturn(targetUniqueId);

        World world = mock(World.class);
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(player, targetPlayer));

        when(playerRegistry.findByUniqueId(entityUniqueId)).thenReturn(Optional.of(gamePlayer));
        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));

        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertTrue(targets.contains(target));
        assertFalse(targets.contains(gamePlayer));
    }

    @Test
    @DisplayName("findPotionEffectReceivers returns list of PotionEffectReceiver instance inside the given range")
    void findPotionEffectReceivers_returnsListOfPotionEffectReceiver() {
        World world = mock(World.class);
        Location givenLocation = new Location(world, 1, 1, 1);
        Location playerLocation = new Location(world, 1.05, 1.05, 1.05);
        Location mobLocation = new Location(world, 0.95, 0.95, 0.95);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);

        LivingEntity entity = mock(LivingEntity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        GameMob gameMob = mock(GameMob.class);
        when(gameMob.getLocation()).thenReturn(mobLocation);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(mobRegistry.register(entity)).thenReturn(gameMob);
        when(world.getNearbyEntities(givenLocation, RANGE, RANGE, RANGE)).thenReturn(List.of(entity));

        List<PotionEffectReceiver> targets = targetFinder.findPotionEffectReceivers(givenLocation, RANGE);

        assertThat(targets).containsExactly(gamePlayer, gameMob);
    }

    @Test
    @DisplayName("findTargets throws IllegalArgumentException when given query has no location")
    void findTargets_queryWithoutLocation() {
        TargetQuery targetQuery = new TargetQuery();

        assertThatThrownBy(() -> targetFinder.findTargets(targetQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No location provided");
    }

    @Test
    @DisplayName("findTargets returns list without entities with matching unique id's where only finding enemies")
    void findTargets_enemiesOnly() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        UUID playerUniqueId = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(playerUniqueId);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));

        TargetQuery targetQuery = new TargetQuery()
                .uniqueId(playerUniqueId)
                .location(findingLocation)
                .enemiesOnly(true);

        List<DamageTarget> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    @DisplayName("findTargets returns list without deployment objects with matching unique id's when only finding enemies")
    void findTargets_enemyDeploymentObjectsOnly() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        UUID deploymentObjectUniqueId = UUID.randomUUID();

        DamageTarget deploymentObject = mock(DamageTarget.class);
        when(deploymentObject.getUniqueId()).thenReturn(deploymentObjectUniqueId);

        when(deploymentObjectRegistry.getDamageableDeploymentObjects()).thenReturn(Set.of(deploymentObject));

        TargetQuery targetQuery = new TargetQuery()
                .uniqueId(deploymentObjectUniqueId)
                .location(findingLocation)
                .enemiesOnly(true);

        List<DamageTarget> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Collection<GameEntity> targets = targetFinder.findTargets(entityId, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    void findTargetsReturnsListOfNearbyPlayers() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(false);

        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertEquals(target, targets.get(0));
    }

    @Test
    void findTargetsReturnsEmptyListIfFoundPlayersArePassive() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(true);

        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertTrue(targets.isEmpty());
    }

    @Test
    void findTargetsReturnsEntitiesThatAreNotPlayers() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Zombie.class);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        List<GameEntity> targets = targetFinder.findTargets(ENTITY_UNIQUE_ID, location, range);

        assertThat(targets).satisfiesExactly(target -> {
            assertThat(target.getUniqueId()).isEqualTo(ENTITY_UNIQUE_ID);
        });
    }
}
