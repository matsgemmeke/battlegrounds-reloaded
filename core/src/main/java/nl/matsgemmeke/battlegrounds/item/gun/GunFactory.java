package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.*;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GunFactory {

    private static final String ACTION_EXECUTOR_ID_VALUE = "gun";

    private final GunControlsFactory controlsFactory;
    private final GunInfoProvider gunInfoProvider;
    private final GunRegistry gunRegistry;
    private final ItemTemplateFactory itemTemplateFactory;
    private final Provider<DefaultGun> defaultGunProvider;
    private final Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    private final ReloadSystemFactory reloadSystemFactory;
    private final ShootHandlerFactory shootHandlerFactory;

    @Inject
    public GunFactory(
            GunControlsFactory controlsFactory,
            GunInfoProvider gunInfoProvider,
            GunRegistry gunRegistry,
            ItemTemplateFactory itemTemplateFactory,
            Provider<DefaultGun> defaultGunProvider,
            Provider<DefaultScopeAttachment> scopeAttachmentProvider,
            ReloadSystemFactory reloadSystemFactory,
            ShootHandlerFactory shootHandlerFactory
    ) {
        this.controlsFactory = controlsFactory;
        this.gunInfoProvider = gunInfoProvider;
        this.gunRegistry = gunRegistry;
        this.itemTemplateFactory = itemTemplateFactory;
        this.defaultGunProvider = defaultGunProvider;
        this.scopeAttachmentProvider = scopeAttachmentProvider;
        this.reloadSystemFactory = reloadSystemFactory;
        this.shootHandlerFactory = shootHandlerFactory;
    }

    public Gun create(GunSpec spec) {
        Gun gun = this.createInstance(spec);

        gunRegistry.register(gun);

        this.registerGunFireSimulationInfo(gun, spec);

        return gun;
    }

    public Gun create(GunSpec spec, GamePlayer gamePlayer) {
        Gun gun = this.createInstance(spec);
        gun.setHolder(gamePlayer);

        gunRegistry.register(gun, gamePlayer);

        this.registerGunFireSimulationInfo(gun, spec);

        return gun;
    }

    private Gun createInstance(GunSpec spec) {
        DefaultGun gun = defaultGunProvider.get();
        gun.setName(spec.name);
        gun.setDescription(spec.description);

        ItemTemplate itemTemplate = itemTemplateFactory.create(spec.item, ACTION_EXECUTOR_ID_VALUE);
        ItemRepresentation itemRepresentation = new ItemRepresentation(itemTemplate);
        itemRepresentation.setPlaceholder(Placeholder.ITEM_NAME, spec.name);

        gun.setItemTemplate(itemTemplate);

        int magazineSize = spec.ammo.magazineSize;
        int reserveAmmo = spec.ammo.defaultMagazineAmount * magazineSize;
        int maxAmmo = spec.ammo.maxMagazineAmount * magazineSize;

        ResourceContainer resourceContainer = new ResourceContainer(magazineSize, magazineSize, reserveAmmo, maxAmmo);
        gun.setResourceContainer(resourceContainer);

        ReloadSystem reloadSystem = reloadSystemFactory.create(spec.reloading, resourceContainer);
        gun.setReloadSystem(reloadSystem);

        ItemControls<GunHolder> controls = controlsFactory.create(spec.controls, gun);
        gun.setControls(controls);

        ShootHandler shootHandler = shootHandlerFactory.create(spec.shooting, resourceContainer, itemRepresentation);
        gun.setShootHandler(shootHandler);

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

            gun.setScopeAttachment(scopeAttachment);
        }

        gun.update();

        return gun;
    }

    private void registerGunFireSimulationInfo(Gun gun, GunSpec spec) {
        UUID gunId = gun.getId();

        List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shooting.projectile.launchSounds);
        int rateOfFire = gun.getRateOfFire();
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        gunInfoProvider.registerGunFireSimulationInfo(gunId, gunFireSimulationInfo);
    }
}
