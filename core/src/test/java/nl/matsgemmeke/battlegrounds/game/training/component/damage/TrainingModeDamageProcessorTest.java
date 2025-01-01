package nl.matsgemmeke.battlegrounds.game.training.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.info.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
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
    public void processDeploymentObjectDamageDoesNotApplyDamageIfDeploymentObjectIsResistantToDamageType() {
        Damage damage = new Damage(10.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(true);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject, never()).damage(any(Damage.class));
    }

    @Test
    public void processDeploymentObjectDamageOnlyDamagesDeploymentObjectIfRemainingHealthIsAboveZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(10.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deploymentObject, never()).destroy();
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(null);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deploymentObject).destroy();
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentAndNotifiesParentItemIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        DeployableItem deployableItem = mock(DeployableItem.class);
        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(deployableItem);

        TrainingModeDamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeContext, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deploymentObject).destroy();
        verify(deployableItem).onDestroyDeploymentObject(deploymentObject);
    }
}
