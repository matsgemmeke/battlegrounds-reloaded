package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnemyHitTriggerTest {

    private static final double TARGET_FINDING_RANGE = 0.1;
    private static final UUID SOURCE_ID = UUID.randomUUID();

    @Mock
    private TargetFinder targetFinder;
    @Mock
    private TriggerTarget triggerTarget;
    @InjectMocks
    private EnemyHitTrigger trigger;

    @Test
    void activatesReturnsFalseWhenTriggerTargetInContextDoesNotExist() {
        TriggerContext context = new TriggerContext(null, triggerTarget);

        when(triggerTarget.exists()).thenReturn(false);

        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void activatesReturnsFalseWhenTriggerTargetDoesIntersectsAnyNearbyEnemyHitboxes(boolean intersects, boolean expectedActivatesResult) {
        Location triggerTargetLocation = new Location(null, 1, 1, 1);
        TriggerContext context = new TriggerContext(SOURCE_ID, triggerTarget);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.intersects(triggerTargetLocation)).thenReturn(intersects);

        GameEntity gameEntity = mock(GameEntity.class);
        when(gameEntity.getHitbox()).thenReturn(hitbox);

        when(targetFinder.findEnemyTargets(SOURCE_ID, triggerTargetLocation, TARGET_FINDING_RANGE)).thenReturn(List.of(gameEntity));
        when(triggerTarget.exists()).thenReturn(true);
        when(triggerTarget.getLocation()).thenReturn(triggerTargetLocation);

        boolean activates = trigger.activates(context);

        assertThat(activates).isEqualTo(expectedActivatesResult);
    }
}
