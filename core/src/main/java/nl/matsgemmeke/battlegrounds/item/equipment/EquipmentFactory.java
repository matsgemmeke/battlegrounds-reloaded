package nl.matsgemmeke.battlegrounds.item.equipment;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
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
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
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
    private final ItemEffectFactory effectFactory;
    @NotNull
    private final NamespacedKeyCreator keyCreator;
    @NotNull
    private final ParticleEffectMapper particleEffectMapper;
    @NotNull
    private final ProjectileEffectFactory projectileEffectFactory;

    @Inject
    public EquipmentFactory(
            @NotNull DeploymentHandlerFactory deploymentHandlerFactory,
            @NotNull GameContextProvider contextProvider,
            @NotNull EquipmentControlsFactory controlsFactory,
            @NotNull ItemEffectFactory effectFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull ParticleEffectMapper particleEffectMapper,
            @NotNull ProjectileEffectFactory projectileEffectFactory
    ) {
        this.deploymentHandlerFactory = deploymentHandlerFactory;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.effectFactory = effectFactory;
        this.keyCreator = keyCreator;
        this.particleEffectMapper = particleEffectMapper;
        this.projectileEffectFactory = projectileEffectFactory;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Equipment equipment = this.createInstance(spec, configuration, gameKey);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment create(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(spec, configuration, gameKey);
        equipment.setHolder(gamePlayer);

        EquipmentRegistry equipmentRegistry = contextProvider.getComponent(gameKey, EquipmentRegistry.class);
        equipmentRegistry.registerItem(equipment, gamePlayer);

        return equipment;
    }

    @NotNull
    private Equipment createInstance(@NotNull EquipmentSpec spec, @NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        DefaultEquipment equipment = new DefaultEquipment();
        equipment.setName(spec.name());
        equipment.setDescription(spec.description());

        // ItemStack creation
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        Material displayItemMaterial = Material.valueOf(spec.displayItem().material());
        int displayItemDamage = spec.displayItem().damage();
        String displayItemDisplayName = spec.displayItem().displayName();

        ItemTemplate displayItemTemplate = new ItemTemplate(uuid, key, displayItemMaterial);
        displayItemTemplate.setDamage(displayItemDamage);
        displayItemTemplate.setDisplayNameTemplate(new TextTemplate(displayItemDisplayName));

        equipment.setDisplayItemTemplate(displayItemTemplate);
        equipment.update();

        Activator activator = null;
        ItemStackSpec activatorItemSpec = spec.activatorItem();
        ItemStackSpec throwItemSpec = spec.throwItem();

        if (activatorItemSpec != null) {
            UUID activatorUUID = UUID.randomUUID();
            NamespacedKey activatorKey = keyCreator.create(NAMESPACED_KEY_NAME);
            Material activatorItemMaterial = Material.valueOf(activatorItemSpec.material());

            ItemTemplate activatorItemTemplate = new ItemTemplate(activatorUUID, activatorKey, activatorItemMaterial);
            activatorItemTemplate.setDamage(activatorItemSpec.damage());
            activatorItemTemplate.setDisplayNameTemplate(new TextTemplate(activatorItemSpec.displayName()));

            activator = new DefaultActivator(activatorItemTemplate);
            equipment.setActivator(activator);
        }

        if (throwItemSpec != null) {
            UUID throwItemUUID = UUID.randomUUID();
            NamespacedKey throwItemKey = keyCreator.create(NAMESPACED_KEY_NAME);
            Material throwItemMaterial = Material.valueOf(throwItemSpec.material());

            ItemTemplate throwItemTemplate = new ItemTemplate(throwItemUUID, throwItemKey, throwItemMaterial);
            throwItemTemplate.setDamage(throwItemSpec.damage());
            throwItemTemplate.setDisplayNameTemplate(new TextTemplate(throwItemSpec.displayName()));

            equipment.setThrowItemTemplate(throwItemTemplate);
        }

        ItemControls<EquipmentHolder> controls = controlsFactory.create(spec.controls(), spec.deployment(), equipment, gameKey);
        equipment.setControls(controls);

        DeploymentHandler deploymentHandler = this.setUpDeploymentHandler(spec.deployment(), spec.effect(), gameKey, activator);
        equipment.setDeploymentHandler(deploymentHandler);

        return equipment;
    }

    @NotNull
    private DeploymentHandler setUpDeploymentHandler(@NotNull DeploymentSpec deploymentSpec, @NotNull ItemEffectSpec effectSpec, @NotNull GameKey gameKey, @Nullable Activator activator) {
        boolean activateEffectOnDestroy = deploymentSpec.activateEffectOnDestroy();
        boolean removeOnDestroy = deploymentSpec.removeOnDestroy();
        boolean resetEffectOnDestroy = deploymentSpec.resetEffectOnDestroy();

        List<GameSound> manualActivationSounds = Collections.emptyList();
        long manualActivationDelay = 0L;

        if (deploymentSpec.manualActivation() != null) {
            manualActivationSounds = DefaultGameSound.parseSounds(deploymentSpec.manualActivation().sounds());
            manualActivationDelay = deploymentSpec.manualActivation().delay();
        }

        ParticleEffect destroyEffect = null;

        if (deploymentSpec.destroyEffect() != null) {
            destroyEffect = particleEffectMapper.map(deploymentSpec.destroyEffect());
        }

        DeploymentProperties deploymentProperties = new DeploymentProperties(manualActivationSounds, destroyEffect, activateEffectOnDestroy, removeOnDestroy, resetEffectOnDestroy, manualActivationDelay);

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        ItemEffect effect = effectFactory.create(effectSpec, gameKey);

        DeploymentHandler deploymentHandler = deploymentHandlerFactory.create(deploymentProperties, audioEmitter, effect);
        deploymentHandler.setActivator(activator);

        return deploymentHandler;
    }
}
