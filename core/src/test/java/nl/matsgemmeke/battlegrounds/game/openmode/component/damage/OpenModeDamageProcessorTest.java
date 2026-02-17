package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeDamageProcessorTest {

    @Mock
    private DeploymentInfoProvider deploymentInfoProvider;
    @Mock
    private GameKey gameKey;
    @InjectMocks
    private OpenModeDamageProcessor damageProcessor;

    @Test
    void isDamageAllowedReturnsFalseWhenGivenGameKeyIsDifferent() {
        GameKey otherGameKey = GameKey.ofSession(1);

        boolean allowed = damageProcessor.isDamageAllowed(otherGameKey);

        assertFalse(allowed);
    }

    @Test
    void isDamageAllowedReturnsTrueWhenGivenGameKeyIsSame() {
        boolean allowed = damageProcessor.isDamageAllowed(gameKey);

        assertTrue(allowed);
    }

    @Test
    void isDamageAllowedWithoutContextAlwaysReturnTrue() {
        boolean allowed = damageProcessor.isDamageAllowedWithoutContext();

        assertThat(allowed).isTrue();
    }

    @Test
    void processDamageAppliesDamageModifiersAndDamagesTarget() {
        DamageSource source = mock(DamageSource.class);
        DamageTarget target = mock(DamageTarget.class);
        Damage originalDamage = new Damage(10.0, DamageType.BULLET_DAMAGE);
        Damage modifiedDamage = new Damage(20.0, DamageType.BULLET_DAMAGE);

        DamageContext originalDamageContext = new DamageContext(source, target, originalDamage);
        DamageContext modifiedDamageContext = new DamageContext(source, target, modifiedDamage);

        DamageModifier damageModifier = mock(DamageModifier.class);
        when(damageModifier.apply(originalDamageContext)).thenReturn(modifiedDamageContext);

        damageProcessor.addDamageModifier(damageModifier);
        damageProcessor.processDamage(originalDamageContext);

        verify(target).damage(modifiedDamage);
    }

    @Test
    void processDeploymentObjectDamageDoesNotApplyDamageIfDeploymentObjectIsResistantToDamageType() {
        Damage damage = new Damage(10.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(true);

        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject, never()).damage(any(Damage.class));
    }

    @Test
    void processDeploymentObjectDamageOnlyDamagesDeploymentObjectIfRemainingHealthIsAboveZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(10.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deploymentObject, never()).remove();
    }

    @Test
    void processDeploymentObjectDamageDestroysDeploymentIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(null);

        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
    }

    @Test
    void processDeploymentObjectDamageDestroysDeploymentAndNotifiesParentItemIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        DeployableItem deployableItem = mock(DeployableItem.class);
        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(deployableItem);

        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deployableItem).destroyDeployment();
    }
}
