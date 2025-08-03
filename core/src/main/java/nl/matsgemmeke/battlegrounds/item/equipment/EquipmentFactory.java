package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EquipmentFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-equipment";

    @NotNull
    private final DeploymentHandlerFactory deploymentHandlerFactory;
    @NotNull
    private final EquipmentControlsFactory controlsFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ItemEffectFactory itemEffectFactory;
    @NotNull
    private final NamespacedKeyCreator namespacedKeyCreator;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;

    @Inject
    public EquipmentFactory(
            @NotNull DeploymentHandlerFactory deploymentHandlerFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull EquipmentControlsFactory controlsFactory,
            @NotNull ItemEffectFactory itemEffectFactory,
            @NotNull NamespacedKeyCreator namespacedKeyCreator,
            @NotNull ParticleEffectMapper particleEffectMapper
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.itemEffectFactory = itemEffectFactory;
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.particleEffectMapper = particleEffectMapper;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey) {
        Equipment equipment = this.createInstance(spec, gameKey);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(spec, gameKey);
        equipment.setHolder(gamePlayer);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull EquipmentSpec spec, @NotNull GameKey gameKey) {
        DefaultEquipment equipment = new DefaultEquipment(spec.id);
        equipment.setName(spec.name);
        equipment.setDescription(spec.description);

        // ItemStack creation
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = namespacedKeyCreator.create(NAMESPACED_KEY_NAME);
        Material displayItemMaterial = Material.valueOf(spec.items.displayItem.material);
        int displayItemDamage = spec.items.displayItem.damage;
        String displayItemDisplayName = spec.items.displayItem.displayName;

        ItemTemplate displayItemTemplate = new ItemTemplate(uuid, key, displayItemMaterial);
        displayItemTemplate.setDamage(displayItemDamage);
        displayItemTemplate.setDisplayNameTemplate(new TextTemplate(displayItemDisplayName));

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        Activator activator = null;
        ItemSpec activatorItemSpec = spec.items.activatorItem;
        ItemSpec throwItemSpec = spec.items.throwItem;

        if (activatorItemSpec != null) {
            UUID activatorUUID = UUID.randomUUID();
            NamespacedKey activatorKey = namespacedKeyCreator.create(NAMESPACED_KEY_NAME);
            Material activatorItemMaterial = Material.valueOf(activatorItemSpec.material);

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorUUID, activatorKey, activatorItemMaterial);
            activatorItemTemplate.setDamage(activatorItemSpec.damage);
            activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorItemSpec.displayName));

            activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            UUID throwItemUUID = UUID.randomUUID();
            NamespacedKey throwItemKey = namespacedKeyCreator.create(NAMESPACED_KEY_NAME);
            Material throwItemMaterial = Material.valueOf(throwItemSpec.material);

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemUUID, throwItemKey, throwItemMaterial);
            throwItemTemplate.setDamage(throwItemSpec.damage);
            throwItemTemplate.setDisplayNameTemplate(new TextTemplate(throwItemSpec.displayName));

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        ItemControls<EquipmentHolder> controls = controlsFactory.create(spec, equipment, gameKey);
        equipment.setControls(controls);

        DeploymentHandler deploymentHandler = this.setUpDeploymentHandler(spec.deploy, spec.effect, gameKey, activator);
        equipment.setDeploymentHandler(deploymentHandler);

        return equipment;
    }

    @NotNull
    private DeploymentHandler setUpDeploymentHandler(@NotNull DeploymentSpec deploymentSpec, @NotNull ItemEffectSpec effectSpec, @NotNull GameKey gameKey, @Nullable Activator activator) {
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

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        ItemEffect itemEffect = itemEffectFactory.create(effectSpec, gameKey);

        DeploymentHandler deploymentHandler = deploymentHandlerFactory.create(deploymentProperties, audioEmitter, itemEffect);
        deploymentHandler.setActivator(activator);

        return deploymentHandler;
    }
}
