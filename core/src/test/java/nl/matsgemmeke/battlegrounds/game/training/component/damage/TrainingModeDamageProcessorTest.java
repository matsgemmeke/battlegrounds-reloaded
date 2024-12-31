package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.info.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TrainingModeDamageProcessorTest {

    private DeploymentInfoProvider deploymentInfoProvider;
    private GameContext trainingModeContext;

    @BeforeEach
    public void setUp() {
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
        trainingModeContext = mock(GameContext.class);
    }

    @Test
    public void shouldNotAllowDamageFromDifferentContext() {
        GameContext otherContext = mock(GameContext.class);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowed(otherContext);

        assertFalse(allowed);
    }

    @Test
    public void shouldAllowDamageInSameContext() {
        GameContext otherContext = trainingModeContext;

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowed(otherContext);

        assertTrue(allowed);
    }

    @Test
    public void shouldAllowDamageFromNullContext() {
        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowed(null);

        assertTrue(allowed);
    }

    @Test
    public void performsAllDamageCheckWhenProcessingDamageEvent() {
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);

        DamageCheck damageCheck = mock(DamageCheck.class);
        DamageEvent damageEvent = new DamageEvent(damager, null, entity, null, DamageType.BULLET_DAMAGE, 10.0);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.addDamageCheck(damageCheck);
        damageProcessor.processDamage(damageEvent);

        verify(damageCheck).process(damageEvent);
    }

    @Test
    public void processDeploymentObjectDamageOnlyDamagesDeploymentObjectIfRemainingHealthIsAboveZero() {
        double damageAmount = 10.0;

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.damage(damageAmount)).thenReturn(20.0);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damageAmount);

        verify(deploymentObject, never()).destroy();
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentIfRemainingHealthEqualsOrIsBelowZero() {
        double damageAmount = 10.0;

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.damage(damageAmount)).thenReturn(0.0);

        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(null);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damageAmount);

        verify(deploymentObject).destroy();
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentAndNotifiesParentItemIfRemainingHealthEqualsOrIsBelowZero() {
        double damageAmount = 10.0;

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.damage(damageAmount)).thenReturn(0.0);

        DeployableItem deployableItem = mock(DeployableItem.class);
        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(deployableItem);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damageAmount);

        verify(deployableItem).onDestroyDeploymentObject(deploymentObject);
        verify(deploymentObject).destroy();
    }
}
