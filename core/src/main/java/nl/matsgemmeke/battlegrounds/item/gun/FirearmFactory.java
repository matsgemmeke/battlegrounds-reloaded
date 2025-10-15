package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FirearmFactory {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String ACTION_EXECUTOR_ID_VALUE = "gun";
    private static final String TEMPLATE_ID_KEY = "template-id";
    private static final double DEFAULT_HEADSHOT_DAMAGE_MULTIPLIER = 1.0;

    private final BattlegroundsConfiguration config;
    private final FirearmControlsFactory controlsFactory;
    private final GunInfoProvider gunInfoProvider;
    private final GunRegistry gunRegistry;
    private final NamespacedKeyCreator keyCreator;
    private final Provider<DefaultFirearm> defaultFirearmProvider;
    private final Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    private final ReloadSystemFactory reloadSystemFactory;
    private final ShootHandlerFactory shootHandlerFactory;

    @Inject
    public FirearmFactory(
            BattlegroundsConfiguration config,
            FirearmControlsFactory controlsFactory,
            GunInfoProvider gunInfoProvider,
            GunRegistry gunRegistry,
            NamespacedKeyCreator keyCreator,
            Provider<DefaultFirearm> defaultFirearmProvider,
            Provider<DefaultScopeAttachment> scopeAttachmentProvider,
            ReloadSystemFactory reloadSystemFactory,
            ShootHandlerFactory shootHandlerFactory
    ) {
        this.config = config;
        this.controlsFactory = controlsFactory;
        this.gunInfoProvider = gunInfoProvider;
        this.gunRegistry = gunRegistry;
        this.keyCreator = keyCreator;
        this.defaultFirearmProvider = defaultFirearmProvider;
        this.scopeAttachmentProvider = scopeAttachmentProvider;
        this.reloadSystemFactory = reloadSystemFactory;
        this.shootHandlerFactory = shootHandlerFactory;
    }

    public Firearm create(GunSpec spec) {
        Firearm gun = this.createInstance(spec);

        gunRegistry.register(gun);

        this.registerGunFireSimulationInfo(gun, spec);

        return gun;
    }

    public Firearm create(GunSpec spec, GamePlayer gamePlayer) {
        Firearm gun = this.createInstance(spec);
        gun.setHolder(gamePlayer);

        gunRegistry.register(gun, gamePlayer);

        this.registerGunFireSimulationInfo(gun, spec);

        return gun;
    }

    private Firearm createInstance(GunSpec spec) {
        double headshotDamageMultiplier = this.getHeadshotDamageMultiplier(spec.shooting.projectile.headshotDamageMultiplier);

        DefaultFirearm firearm = defaultFirearmProvider.get();
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

    private double getHeadshotDamageMultiplier(Double specValue) {
        if (specValue != null) {
            return specValue;
        }

        return DEFAULT_HEADSHOT_DAMAGE_MULTIPLIER;
    }

    private ItemTemplate createItemTemplate(ItemSpec spec) {
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

    private void registerGunFireSimulationInfo(Gun gun, GunSpec spec) {
        UUID gunId = gun.getId();

        List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shooting.projectile.shotSounds);
        int rateOfFire = gun.getRateOfFire();
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        gunInfoProvider.registerGunFireSimulationInfo(gunId, gunFireSimulationInfo);
    }
}
