package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.PersistentDataEntry;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.*;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FirearmFactory {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String ACTION_EXECUTOR_ID_VALUE = "gun";
    private static final String TEMPLATE_ID_KEY = "template-id";
    private static final double DEFAULT_HEADSHOT_DAMAGE_MULTIPLIER = 1.0;

    @NotNull
    private final BattlegroundsConfiguration config;
    @NotNull
    private final DefaultFirearmFactory defaultGunFactory;
    @NotNull
    private final FirearmControlsFactory controlsFactory;
    @NotNull
    private final GunRegistry gunRegistry;
    @NotNull
    private final NamespacedKeyCreator keyCreator;
    @NotNull
    private final Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    @NotNull
    private final ReloadSystemFactory reloadSystemFactory;
    @NotNull
    private final ShootHandlerFactory shootHandlerFactory;

    @Inject
    public FirearmFactory(
            @NotNull BattlegroundsConfiguration config,
            @NotNull DefaultFirearmFactory defaultGunFactory,
            @NotNull FirearmControlsFactory controlsFactory,
            @NotNull GunRegistry gunRegistry,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull Provider<DefaultScopeAttachment> scopeAttachmentProvider,
            @NotNull ReloadSystemFactory reloadSystemFactory,
            @NotNull ShootHandlerFactory shootHandlerFactory
    ) {
        this.config = config;
        this.defaultGunFactory = defaultGunFactory;
        this.controlsFactory = controlsFactory;
        this.gunRegistry = gunRegistry;
        this.keyCreator = keyCreator;
        this.scopeAttachmentProvider = scopeAttachmentProvider;
        this.reloadSystemFactory = reloadSystemFactory;
        this.shootHandlerFactory = shootHandlerFactory;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        Firearm firearm = this.createInstance(spec, gameKey);

        gunRegistry.register(firearm);

        return firearm;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Firearm firearm = this.createInstance(spec, gameKey);
        firearm.setHolder(gamePlayer);

        gunRegistry.register(firearm, gamePlayer);

        return firearm;
    }

    @NotNull
    private Firearm createInstance(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        double headshotDamageMultiplier = this.getHeadshotDamageMultiplier(spec.shooting.projectile.headshotDamageMultiplier);

        DefaultFirearm firearm = defaultGunFactory.create(spec.id);
        firearm.setName(spec.name);
        firearm.setDescription(spec.description);
        firearm.setHeadshotDamageMultiplier(headshotDamageMultiplier);

        ItemTemplate itemTemplate = this.createItemTemplate(spec.item);
        ItemRepresentation itemRepresentation = new ItemRepresentation(itemTemplate);
        itemRepresentation.setPlaceholder(Placeholder.ITEM_NAME, spec.name);

        firearm.setItemTemplate(itemTemplate);

        double damageAmplifier = config.getGunDamageAmplifier();
        firearm.setDamageAmplifier(damageAmplifier);

        int magazineSize = spec.ammo.magazineSize;
        int reserveAmmo = spec.ammo.defaultMagazineAmount * magazineSize;
        int maxAmmo = spec.ammo.maxMagazineAmount * magazineSize;

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(magazineSize, magazineSize, reserveAmmo, maxAmmo);
        firearm.setAmmunitionStorage(ammunitionStorage);

        ReloadSystem reloadSystem = reloadSystemFactory.create(spec.reloading, firearm);
        firearm.setReloadSystem(reloadSystem);

        ItemControls<GunHolder> controls = controlsFactory.create(spec.controls, firearm);
        firearm.setControls(controls);

        ShootHandler shootHandler = shootHandlerFactory.create(spec.shooting, ammunitionStorage, itemRepresentation);
        firearm.setShootHandler(shootHandler);

        ScopeSpec scopeSpec = spec.scope;

        if (scopeSpec != null) {
            List<Float> magnifications = Arrays.asList(scopeSpec.magnifications);
            List<GameSound> useSounds = DefaultGameSound.parseSounds(scopeSpec.useSounds);
            List<GameSound> stopSounds = DefaultGameSound.parseSounds(scopeSpec.stopSounds);
            List<GameSound> changeMagnificationSounds = DefaultGameSound.parseSounds(scopeSpec.changeMagnificationSounds);

            DefaultScopeAttachment scopeAttachment = scopeAttachmentProvider.get();
            scopeAttachment.configureMagnifications(magnifications);
            scopeAttachment.configureUseSounds(useSounds);
            scopeAttachment.configureStopSounds(stopSounds);
            scopeAttachment.configureChangeMagnificationSounds(changeMagnificationSounds);

            firearm.setScopeAttachment(scopeAttachment);
        }

        firearm.update();

        return firearm;
    }

    private double getHeadshotDamageMultiplier(@Nullable Double specValue) {
        if (specValue != null) {
            return specValue;
        }

        return DEFAULT_HEADSHOT_DAMAGE_MULTIPLIER;
    }

    @NotNull
    private ItemTemplate createItemTemplate(@NotNull ItemSpec spec) {
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(TEMPLATE_ID_KEY);
        Material material = Material.valueOf(spec.material);
        String displayName = spec.displayName;
        int damage = spec.damage;

        NamespacedKey actionExecutorIdKey = keyCreator.create(ACTION_EXECUTOR_ID_KEY);
        PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, ACTION_EXECUTOR_ID_VALUE);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.addPersistentDataEntry(actionExecutorIdDataEntry);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        return itemTemplate;
    }
}
