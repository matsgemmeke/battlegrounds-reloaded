package nl.matsgemmeke.battlegrounds.item.trigger.enemy;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EnemyProximityTriggerTest {

    private static final double CHECKING_RANGE = 2.5;

    private Entity entity;
    private TargetFinder targetFinder;
    private TriggerContext context;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        targetFinder = mock(TargetFinder.class);
        target = mock(TriggerTarget.class);

        context = new TriggerContext(entity, target);
    }

    @Test
    public void activatesReturnsFalseWhenTriggerTargetDoesNotExist() {
        when(target.exists()).thenReturn(false);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder);
        trigger.setCheckingRange(CHECKING_RANGE);
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenThereAreNoNearbyEnemyTargets() {
        Location targetLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        when(entity.getUniqueId()).thenReturn(entityId);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(entityId, targetLocation, CHECKING_RANGE)).thenReturn(Collections.emptyList());

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder);
        trigger.setCheckingRange(CHECKING_RANGE);
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsTrueWhenThereAreNearbyEnemyTargets() {
        Location targetLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        when(entity.getUniqueId()).thenReturn(entityId);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(targetFinder.findEnemyTargets(entityId, targetLocation, CHECKING_RANGE)).thenReturn(List.of(mock(GameEntity.class)));

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder);
        trigger.setCheckingRange(CHECKING_RANGE);
        boolean activates = trigger.activates(context);

        assertThat(activates).isTrue();
    }
}
