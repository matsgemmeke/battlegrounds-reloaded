package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.trigger.TriggerSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.deploy.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.deploy.state.IdleState;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControllerFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;

import java.util.Collections;
import java.util.List;

public class EquipmentFactory {

    private final DeploymentFactory deploymentFactory;
    private final EquipmentControllerFactory controllerFactory;
    private final EquipmentRegistry equipmentRegistry;
    private final ItemEffectFactory itemEffectFactory;
    private final ItemTemplateFactory itemTemplateFactory;
    private final ParticleEffectMapper particleEffectMapper;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public EquipmentFactory(
            DeploymentFactory deploymentFactory,
            EquipmentControllerFactory controllerFactory,
            EquipmentRegistry equipmentRegistry,
            ItemEffectFactory itemEffectFactory,
            ItemTemplateFactory itemTemplateFactory,
            ParticleEffectMapper particleEffectMapper,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.deploymentFactory = deploymentFactory;
        this.controllerFactory = controllerFactory;
        this.equipmentRegistry = equipmentRegistry;
        this.itemEffectFactory = itemEffectFactory;
        this.itemTemplateFactory = itemTemplateFactory;
        this.particleEffectMapper = particleEffectMapper;
        this.triggerExecutorFactory = triggerExecutorFactory;
    }

    public Equipment create(EquipmentSpec spec) {
        Equipment equipment = this.createInstance(spec);

        equipmentRegistry.register(equipment);

        return equipment;
    }

    public Equipment create(EquipmentSpec spec, GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(spec);
        equipment.setUser(gamePlayer);

        equipmentRegistry.register(equipment, gamePlayer);

        return equipment;
    }

    private Equipment createInstance(EquipmentSpec spec) {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setName(spec.name);
        equipment.setDescription(spec.description);

        ItemTemplate displayItemTemplate = itemTemplateFactory.create(spec.items.displayItem);

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        Activator activator = null;
        ItemSpec activatorItemSpec = spec.items.activatorItem;
        ItemSpec throwItemSpec = spec.items.throwItem;

        if (activatorItemSpec != null) {
            ItemTemplate activatorItemTemplate = itemTemplateFactory.create(activatorItemSpec);

            activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            ItemTemplate throwItemTemplate = itemTemplateFactory.create(throwItemSpec);

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        ItemController<EquipmentUser> controller = controllerFactory.create(spec, equipment);
        equipment.setController(controller);

        Deployment deployment = this.createDeployment(spec.deploy, spec.effect);
        equipment.setDeployment(deployment);

        return equipment;
    }

    private Deployment createDeployment(DeploymentSpec deploymentSpec, ItemEffectSpec effectSpec) {
        boolean activateEffectOnDestruction = deploymentSpec.onDestruction.activateEffect;
        boolean removeDeploymentOnDestruction = deploymentSpec.onDestruction.removeDeployment;
        boolean undoEffectOnDestruction = deploymentSpec.onDestruction.undoEffect;
        boolean removeDeploymentOnCleanup = deploymentSpec.onCleanup.removeDeployment;

        List<GameSound> manualActivationSounds = Collections.emptyList();
        long manualActivationDelay = 0L;

        if (deploymentSpec.manualActivation != null) {
            manualActivationSounds = DefaultGameSound.parseSounds(deploymentSpec.manualActivation.activationSounds);
            manualActivationDelay = deploymentSpec.manualActivation.delay;
        }

        ParticleEffect destructionParticleEffect = null;
        ParticleEffectSpec destructionParticleEffectSpec = deploymentSpec.onDestruction.particleEffect;

        if (destructionParticleEffectSpec != null) {
            destructionParticleEffect = particleEffectMapper.map(destructionParticleEffectSpec);
        }

        DeploymentProperties properties = new DeploymentProperties(manualActivationSounds, destructionParticleEffect, activateEffectOnDestruction, removeDeploymentOnDestruction, undoEffectOnDestruction, removeDeploymentOnCleanup, manualActivationDelay);
        DeploymentState state = new IdleState();
        ItemEffect itemEffect = itemEffectFactory.create(effectSpec);

        Deployment deployment = deploymentFactory.create(properties, state, itemEffect);

        for (TriggerSpec triggerSpec : deploymentSpec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            deployment.addTriggerExecutor(triggerExecutor);
        }

        return deployment;
    }
}
