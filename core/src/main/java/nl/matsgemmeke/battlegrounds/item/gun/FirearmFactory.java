package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.mapper.RangeProfileMapper;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.*;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FirearmFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-gun";

    @NotNull
    private final BattlegroundsConfiguration config;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final FirearmControlsFactory controlsFactory;
    @NotNull
    private final NamespacedKeyCreator keyCreator;
    @NotNull
    private final RangeProfileMapper rangeProfileMapper;
    @NotNull
    private final ReloadSystemFactory reloadSystemFactory;
    @NotNull
    private final ShootHandlerFactory shootHandlerFactory;

    @Inject
    public FirearmFactory(
            @NotNull BattlegroundsConfiguration config,
            @NotNull GameContextProvider contextProvider,
            @NotNull FirearmControlsFactory controlsFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull RangeProfileMapper rangeProfileMapper,
            @NotNull ReloadSystemFactory reloadSystemFactory,
            @NotNull ShootHandlerFactory shootHandlerFactory
    ) {
        this.config = config;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.keyCreator = keyCreator;
        this.rangeProfileMapper = rangeProfileMapper;
        this.reloadSystemFactory = reloadSystemFactory;
        this.shootHandlerFactory = shootHandlerFactory;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        Firearm firearm = this.createInstance(spec, gameKey);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm);

        return firearm;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Firearm firearm = this.createInstance(spec, gameKey);
        firearm.setHolder(gamePlayer);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm, gamePlayer);

        return firearm;
    }

    @NotNull
    private Firearm createInstance(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);
        DamageProcessor damageProcessor = contextProvider.getComponent(gameKey, DamageProcessor.class);
        TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

        DefaultFirearm firearm = new DefaultFirearm(spec.id, audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setName(spec.name);
        firearm.setDescription(spec.description);
        firearm.setHeadshotDamageMultiplier(spec.shooting.projectile.headshotDamageMultiplier);

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

        RangeProfile rangeProfile = rangeProfileMapper.map(spec.shooting.range);
        firearm.setRangeProfile(rangeProfile);

        ReloadSystem reloadSystem = reloadSystemFactory.create(spec.reloading, firearm, audioEmitter);
        firearm.setReloadSystem(reloadSystem);

        ItemControls<GunHolder> controls = controlsFactory.create(spec.controls, firearm);
        firearm.setControls(controls);

        ShootHandler shootHandler = shootHandlerFactory.create(spec.shooting, gameKey, ammunitionStorage, itemRepresentation);
        firearm.setShootHandler(shootHandler);

        ScopeSpec scopeSpec = spec.scope;

        if (scopeSpec != null) {
            List<Float> magnifications = Arrays.asList(scopeSpec.magnifications);
            List<GameSound> useSounds = DefaultGameSound.parseSounds(scopeSpec.useSounds);
            List<GameSound> stopSounds = DefaultGameSound.parseSounds(scopeSpec.stopSounds);
            List<GameSound> changeMagnificationSounds = DefaultGameSound.parseSounds(scopeSpec.changeMagnificationSounds);

            ScopeProperties properties = new ScopeProperties(magnifications, useSounds, stopSounds, changeMagnificationSounds);
            DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);

            firearm.setScopeAttachment(scopeAttachment);
        }

        firearm.update();

        return firearm;
    }

    @NotNull
    private ItemTemplate createItemTemplate(@NotNull ItemSpec spec) {
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        Material material = Material.valueOf(spec.material);
        String displayName = spec.displayName;
        int damage = spec.damage;

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        return itemTemplate;
    }
}
