package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TrainingModeDamageProcessorTest {

    private GameContext trainingModeContext;

    @BeforeEach
    public void setUp() {
        trainingModeContext = mock(GameContext.class);
    }

    @Test
    public void shouldNotAllowDamageFromDifferentContext() {
        GameContext otherContext = mock(GameContext.class);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext);
        boolean allowed = damageProcessor.isDamageAllowed(otherContext);

        assertFalse(allowed);
    }

    @Test
    public void shouldAllowDamageInSameContext() {
        GameContext otherContext = trainingModeContext;

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext);
        boolean allowed = damageProcessor.isDamageAllowed(otherContext);

        assertTrue(allowed);
    }

    @Test
    public void shouldAllowDamageFromNullContext() {
        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext);
        boolean allowed = damageProcessor.isDamageAllowed(null);

        assertTrue(allowed);
    }

    @Test
    public void performsAllDamageCheckWhenProcessingDamageEvent() {
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);

        DamageCheck damageCheck = mock(DamageCheck.class);
        DamageEvent damageEvent = new DamageEvent(damager, null, entity, null, DamageCause.GUN_PROJECTILE, 10.0);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext);
        damageProcessor.addDamageCheck(damageCheck);
        damageProcessor.processDamage(damageEvent);

        verify(damageCheck).process(damageEvent);
    }
}
