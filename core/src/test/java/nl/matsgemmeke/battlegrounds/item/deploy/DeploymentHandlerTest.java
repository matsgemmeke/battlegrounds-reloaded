package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DeploymentHandlerTest {

    private static final boolean ACTIVATE_EFFECT_ON_DESTRUCTION = true;
    private static final boolean REMOVE_DEPLOYMENT_ON_DESTRUCTION = true;
    private static final boolean UNDO_EFFECT_ON_DESTRUCTION = true;
    private static final boolean REMOVE_DEPLOYMENT_ON_CLEANUP = true;
    private static final List<GameSound> ACTIVATION_SOUNDS = Collections.emptyList();
    private static final long MANUAL_ACTIVATION_DELAY = 10L;
    private static final ParticleEffect DESTRUCTION_PARTICLE_EFFECT = new ParticleEffect(Particle.ASH, 1, 0, 0, 0, 0, null, null);

    private AudioEmitter audioEmitter;
    private Deployer deployer;
    private Deployment deployment;
    private DeploymentObject deploymentObject;
    private DeploymentProperties deploymentProperties;
    private Entity deployerEntity;
    private ParticleEffectSpawner particleEffectSpawner;
    private ItemEffect effect;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deployer = mock(Deployer.class);
        deployment = mock(Deployment.class);
        deploymentObject = mock(DeploymentObject.class);
        deployerEntity = mock(Entity.class);
        deploymentProperties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, MANUAL_ACTIVATION_DELAY);
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        effect = mock(ItemEffect.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateDeploymentActivatesEffectAfterActivationDelay() {
        Location deployerLocation = new Location(null, 1, 1, 1);

        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.activateDeployment(deployer, deployerEntity);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(MANUAL_ACTIVATION_DELAY));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, deployerLocation);
        verify(deployer).setHeldItem(null);
        verify(effect).activateInstantly();
    }

    @Test
    public void cleanupDeploymentDoesNotRemoveDeploymentObjectWhenDeployedAndRemoveDeploymentOnCleanupIsFalse() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.isDeployed()).thenReturn(true);

        DeploymentProperties deploymentProperties = new DeploymentProperties(List.of(), null, true, true, true, false, 0L);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isDeployed()).isTrue();

        verify(deploymentObject, never()).remove();
    }

    @Test
    public void cleanupDeploymentRemovesDeploymentObjectWhenDeployedAndRemoveDeploymentOnCleanupIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isDeployed()).isFalse();

        verify(deploymentObject).remove();
    }

    @Test
    public void destroyDeploymentDoesNotCancelEffectWhenNoDeploymentsHaveBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.destroyDeployment();

        verifyNoInteractions(effect);
    }

    @Test
    public void destroyDeploymentDoesNotActivateEffectWhenActivateEffectOnDestructionPropertyIsFalse() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        deploymentProperties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect, never()).activateInstantly();
    }

    @Test
    public void destroyDeploymentDoesNotActivateEffectWhenDeploymentObjectLastDamageTypeIsEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.ENVIRONMENTAL_DAMAGE);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect, never()).activateInstantly();
    }

    @Test
    public void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageIsNull() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLastDamage()).thenReturn(null);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect).activateInstantly();
    }

    @Test
    public void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageTypeIsNotEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.BULLET_DAMAGE);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect).activateInstantly();
    }

    @Test
    public void destroyDeploymentDoesNotRemoveDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsFalse() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        deploymentProperties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(deploymentObject, never()).remove();
    }

    @Test
    public void destroyDeploymentRemovesDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(deploymentObject).remove();
    }

    @Test
    public void destroyDeploymentDoesNotResetEffectWhenResetEffectOnDestructionPropertyIsFalse() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        deploymentProperties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect, never()).undo();
    }

    @Test
    public void destroyDeploymentUndoesEffectWhenResetEffectOnDestructionPropertyIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(effect).undo();
    }

    @Test
    public void destroyDeploymentDoesNotDisplayParticleEffectWhenDestructionParticleEffectPropertyIsNull() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        deploymentProperties = new DeploymentProperties(ACTIVATION_SOUNDS, null, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verifyNoInteractions(particleEffectSpawner);
    }

    @Test
    public void destroyDeploymentDisplaysParticleEffectWhenDestructionParticleEffectPropertyIsNotNull() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 1, 1, 1);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLocation()).thenReturn(objectLocation);
        when(deploymentObject.getWorld()).thenReturn(world);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(effect).cancelActivation();
        verify(particleEffectSpawner).spawnParticleEffect(DESTRUCTION_PARTICLE_EFFECT, world, objectLocation);
    }

    @Test
    public void handleDeploymentDoesNotStartEffectWhenDeploymentResultIsNotSuccessful() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.failure());

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        verifyNoInteractions(effect, taskRunner);
    }

    @Test
    public void handleDeploymentDeploysObjectIfEffectIsAlreadyPrimed() {
        long cooldown = 10L;

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(cooldown);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(effect.isPrimed()).thenReturn(true);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(cooldown));

        runnableCaptor.getValue().run();

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
        verify(effect).deploy(deploymentObject);
    }

    @Test
    public void handleDeploymentPrimesEffectIfEffectIsNotPrimed() {
        long cooldown = 10L;

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(cooldown);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(effect.isPrimed()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        ArgumentCaptor<ItemEffectContext> effectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        verify(effect).prime(effectContextCaptor.capture());
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(cooldown));

        ItemEffectContext effectContext = effectContextCaptor.getValue();
        assertThat(effectContext.getDeployer()).isEqualTo(deployer);
        assertThat(effectContext.getEntity()).isEqualTo(deployerEntity);
        assertThat(effectContext.getSource()).isEqualTo(deploymentObject);

        runnableCaptor.getValue().run();

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }

    @Test
    public void handleDeploymentPreparesActivatorItemWhenActivatorIsNotNull() {
        Activator activator = mock(Activator.class);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(effect.isPrimed()).thenReturn(true);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.setActivator(activator);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        verify(activator).prepare(deployer);
        verify(effect).deploy(deploymentObject);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenNoDeploymentHasBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenDeploymentHasBeenPerformedWithObjectThatIsAlreadyDeployed() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.isDeployed()).thenReturn(true);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    public void isAwaitingDeploymentReturnsTrueWhenDeploymentHasBeenPerformedWithObjectThatIsNotDeployed() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.isDeployed()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    public void isDeployedReturnsFalseWhenNoDeploymentIsPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    public void isDeployedReturnsTrueWhenAnyDeploymentIsPerformed() {
        when(deploymentObject.isDeployed()).thenReturn(true);

        DeploymentResult result = DeploymentResult.success(deploymentObject);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(particleEffectSpawner, taskRunner, deploymentProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isTrue();
    }
}
