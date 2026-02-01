package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.PersistentDataEntry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EquipmentFactory {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String ACTION_EXECUTOR_ID_VALUE = "equipment";
    private static final String TEMPLATE_ID_KEY = "template-id";

    private final DeploymentHandlerFactory deploymentHandlerFactory;
    private final EquipmentControlsFactory controlsFactory;
    private final EquipmentRegistry equipmentRegistry;
    private final ItemEffectFactory itemEffectFactory;
    private final NamespacedKeyCreator namespacedKeyCreator;
    private final ParticleEffectMapper particleEffectMapper;
    private final TriggerExecutorFactory triggerExecutorFactory;

    @Inject
    public EquipmentFactory(
            DeploymentHandlerFactory deploymentHandlerFactory,
            EquipmentControlsFactory controlsFactory,
            EquipmentRegistry equipmentRegistry,
            ItemEffectFactory itemEffectFactory,
            NamespacedKeyCreator namespacedKeyCreator,
            ParticleEffectMapper particleEffectMapper,
            TriggerExecutorFactory triggerExecutorFactory
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.controlsFactory = controlsFactory;
        this.equipmentRegistry = equipmentRegistry;
        this.itemEffectFactory = itemEffectFactory;
        this.namespacedKeyCreator = namespacedKeyCreator;
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
        equipment.setHolder(gamePlayer);

        equipmentRegistry.register(equipment, gamePlayer);

        return equipment;
    }

    private Equipment createInstance(EquipmentSpec spec) {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setName(spec.name);
        equipment.setDescription(spec.description);

        ItemTemplate displayItemTemplate = this.createDisplayItemTemplate(spec.items.displayItem);

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        Activator activator = null;
        ItemSpec activatorItemSpec = spec.items.activatorItem;
        ItemSpec throwItemSpec = spec.items.throwItem;

        if (activatorItemSpec != null) {
            NamespacedKey activatorTemplateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
            UUID activatorTemplateId = UUID.randomUUID();
            Material activatorItemMaterial = Material.valueOf(activatorItemSpec.material);

            NamespacedKey actionExecutorIdKey = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
            PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, ACTION_EXECUTOR_ID_VALUE);

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorTemplateKey, activatorTemplateId, activatorItemMaterial);
            activatorItemTemplate.addPersistentDataEntry(actionExecutorIdDataEntry);
            activatorItemTemplate.setDamage(activatorItemSpec.damage);
            activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorItemSpec.displayName));

            activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            NamespacedKey throwItemTemplateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
            UUID throwItemTemplateId = UUID.randomUUID();
            Material throwItemMaterial = Material.valueOf(throwItemSpec.material);

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemTemplateKey, throwItemTemplateId, throwItemMaterial);
            throwItemTemplate.setDamage(throwItemSpec.damage);
            throwItemTemplate.setDisplayNameTemplate(new TextTemplate(throwItemSpec.displayName));

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        ItemControls<EquipmentHolder> controls = controlsFactory.create(spec, equipment);
        equipment.setControls(controls);

        DeploymentHandler deploymentHandler = this.setUpDeploymentHandler(spec.deploy, spec.effect, activator);
        equipment.setDeploymentHandler(deploymentHandler);

        return equipment;
    }

    private ItemTemplate createDisplayItemTemplate(ItemSpec spec) {
        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(spec.material);
        String displayName = spec.displayName;
        int damage = spec.damage;

        NamespacedKey actionExecutorIdKey = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
        PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, ACTION_EXECUTOR_ID_VALUE);

        ItemTemplate itemTemplate = new ItemTemplate(templateKey, templateId, material);
        itemTemplate.addPersistentDataEntry(actionExecutorIdDataEntry);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        return itemTemplate;
    }

    private DeploymentHandler setUpDeploymentHandler(DeploymentSpec deploymentSpec, ItemEffectSpec effectSpec, @Nullable Activator activator) {
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

        DeploymentProperties deploymentProperties = new DeploymentProperties(manualActivationSounds, destructionParticleEffect, activateEffectOnDestruction, removeDeploymentOnDestruction, undoEffectOnDestruction, removeDeploymentOnCleanup, manualActivationDelay);
        ItemEffect itemEffect = itemEffectFactory.create(effectSpec);

        DeploymentHandler deploymentHandler = deploymentHandlerFactory.create(deploymentProperties, itemEffect);
        deploymentHandler.setActivator(activator);

        for (TriggerSpec triggerSpec : deploymentSpec.triggers.values()) {
            TriggerExecutor triggerExecutor = triggerExecutorFactory.create(triggerSpec);

            deploymentHandler.addTriggerExecutor(triggerExecutor);
        }

        return deploymentHandler;
    }
}
