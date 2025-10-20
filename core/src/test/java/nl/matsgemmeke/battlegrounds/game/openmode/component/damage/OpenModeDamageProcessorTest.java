package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OpenModeDamageProcessorTest {

    private DeploymentInfoProvider deploymentInfoProvider;
    private GameKey openModeGameKey;

    @BeforeEach
    public void setUp() {
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
        openModeGameKey = GameKey.ofOpenMode();
    }

    @Test
    public void shouldNotAllowDamageFromDifferentContext() {
        GameKey otherGameKey = GameKey.ofSession(1);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowed(otherGameKey);

        assertFalse(allowed);
    }

    @Test
    public void shouldAllowDamageInSameContext() {
        GameKey otherGameKey = openModeGameKey;

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowed(otherGameKey);

        assertTrue(allowed);
    }

    @Test
    public void isDamageAllowedWithoutContextAlwaysReturnTrue() {
        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        boolean allowed = damageProcessor.isDamageAllowedWithoutContext();

        assertThat(allowed).isTrue();
    }

    @Test
    public void appliesAllDamageModifiersWhenProcessingDamageEvent() {
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);

        DamageModifier damageModifier = mock(DamageModifier.class);
        DamageEvent damageEvent = new DamageEvent(damager, null, entity, null, DamageType.BULLET_DAMAGE, 10.0);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        damageProcessor.addDamageModifier(damageModifier);
        damageProcessor.processDamage(damageEvent);

        verify(damageModifier).apply(damageEvent);
    }

    @Test
    public void processDeploymentObjectDamageDoesNotApplyDamageIfDeploymentObjectIsResistantToDamageType() {
        Damage damage = new Damage(10.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(true);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject, never()).damage(any(Damage.class));
    }

    @Test
    public void processDeploymentObjectDamageOnlyDamagesDeploymentObjectIfRemainingHealthIsAboveZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(10.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deploymentObject, never()).remove();
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(null);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
    }

    @Test
    public void processDeploymentObjectDamageDestroysDeploymentAndNotifiesParentItemIfRemainingHealthEqualsOrIsBelowZero() {
        Damage damage = new Damage(100.0, DamageType.EXPLOSIVE_DAMAGE);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getHealth()).thenReturn(0.0);
        when(deploymentObject.isImmuneTo(DamageType.EXPLOSIVE_DAMAGE)).thenReturn(false);

        DeployableItem deployableItem = mock(DeployableItem.class);
        when(deploymentInfoProvider.getDeployableItem(deploymentObject)).thenReturn(deployableItem);

        OpenModeDamageProcessor damageProcessor = new OpenModeDamageProcessor(openModeGameKey, deploymentInfoProvider);
        damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);

        verify(deploymentObject).damage(damage);
        verify(deployableItem).destroyDeployment();
    }
}
