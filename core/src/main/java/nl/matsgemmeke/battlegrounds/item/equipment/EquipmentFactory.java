package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
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
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EquipmentFactory {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String ACTION_EXECUTOR_ID_VALUE = "equipment";
    private static final String TEMPLATE_ID_KEY = "template-id";

    @NotNull
    private final DeploymentHandlerFactory deploymentHandlerFactory;
    @NotNull
    private final EquipmentControlsFactory controlsFactory;
    @NotNull
    private final EquipmentRegistry equipmentRegistry;
    @NotNull
    private final ItemEffectFactory itemEffectFactory;
    @NotNull
    private final NamespacedKeyCreator namespacedKeyCreator;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public EquipmentFactory(
            @NotNull DeploymentHandlerFactory deploymentHandlerFactory,
            @NotNull EquipmentControlsFactory controlsFactory,
            @NotNull EquipmentRegistry equipmentRegistry,
            @NotNull ItemEffectFactory itemEffectFactory,
            @NotNull NamespacedKeyCreator namespacedKeyCreator,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.controlsFactory = controlsFactory;
        this.equipmentRegistry = equipmentRegistry;
        this.itemEffectFactory = itemEffectFactory;
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.particleEffectMapper = particleEffectMapper;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey) {
        Equipment equipment = this.createInstance(spec, gameKey);

        equipmentRegistry.register(equipment);

        return equipment;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(spec, gameKey);
        equipment.setHolder(gamePlayer);

        equipmentRegistry.register(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey) {
        DefaultEquipment equipment = new DefaultEquipment(spec.id);
        equipment.setName(spec.name);
        equipment.setDescription(spec.description);

        ItemTemplate displayItemTemplate = this.createDisplayItemTemplate(spec.items.displayItem);

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        Activator activator = null;
        ItemSpec activatorItemSpec = spec.items.activatorItem;
        ItemSpec throwItemSpec = spec.items.throwItem;

        if (activatorItemSpec != null) {
            UUID activatorUUID = UUID.randomUUID();
            NamespacedKey activatorKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
            Material activatorItemMaterial = Material.valueOf(activatorItemSpec.material);

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorUUID, activatorKey, activatorItemMaterial);
            activatorItemTemplate.setDamage(activatorItemSpec.damage);
            activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorItemSpec.displayName));

            activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            UUID throwItemUUID = UUID.randomUUID();
            NamespacedKey throwItemKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
            Material throwItemMaterial = Material.valueOf(throwItemSpec.material);

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemUUID, throwItemKey, throwItemMaterial);
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

    private ItemTemplate createDisplayItemTemplate(@NotNull ItemSpec spec) {
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        Material material = Material.valueOf(spec.material);
        String displayName = spec.displayName;
        int damage = spec.damage;

        NamespacedKey actionExecutorIdKey = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
        PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, ACTION_EXECUTOR_ID_VALUE);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.addPersistentDataEntry(actionExecutorIdDataEntry);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        return itemTemplate;
    }

    @NotNull
    private DeploymentHandler setUpDeploymentHandler(@NotNull DeploymentSpec deploymentSpec, @NotNull ItemEffectSpec effectSpec, @Nullable Activator activator) {
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
        ItemEffectNew itemEffect = itemEffectFactory.create(effectSpec);

        DeploymentHandler deploymentHandler = deploymentHandlerFactory.create(deploymentProperties, itemEffect);
        deploymentHandler.setActivator(activator);

        return deploymentHandler;
    }
}
