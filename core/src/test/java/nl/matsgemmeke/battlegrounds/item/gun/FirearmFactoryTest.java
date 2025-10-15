package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.FirearmControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FirearmFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";
    private static final int RATE_OF_FIRE = 600;

    private AudioEmitter audioEmitter;
    private BattlegroundsConfiguration config;
    private GunInfoProvider gunInfoProvider;
    private GunRegistry gunRegistry;
    private FirearmControlsFactory controlsFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private Provider<DefaultFirearm> defaultFirearmProvider;
    private Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    private ReloadSystemFactory reloadSystemFactory;
    private ShootHandlerFactory shootHandlerFactory;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        config = mock(BattlegroundsConfiguration.class);
        gunInfoProvider = mock(GunInfoProvider.class);
        gunRegistry = mock(GunRegistry.class);
        controlsFactory = mock(FirearmControlsFactory.class);
        itemFactory = mock(ItemFactory.class);
        scopeAttachmentProvider = mock();
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        shootHandlerFactory = mock(ShootHandlerFactory.class);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        defaultFirearmProvider = mock();
        when(defaultFirearmProvider.get()).thenReturn(new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder));

        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, TEMPLATE_ID_KEY);

        keyCreator = mock(NamespacedKeyCreator.class);
        when(keyCreator.create(TEMPLATE_ID_KEY)).thenReturn(key);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createSimpleFirearm() {
        GunSpec spec = this.createGunSpec();
        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Firearm.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(Reloadable.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);
        when(shootHandlerFactory.create(eq(spec.shooting), any(AmmunitionStorage.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        FirearmFactory firearmFactory = new FirearmFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultFirearmProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        GunFireSimulationInfo gunFireSimulationInfo = gunFireSimulationInfoCaptor.getValue();
        assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        assertThat(gunFireSimulationInfo.shotSounds()).hasSize(3);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);
        assertThat(firearm.getName()).isEqualTo("MP5");
        assertThat(firearm.getItemStack()).isNotNull();
        assertThat(firearm.getItemStack().getType()).isEqualTo(Material.IRON_HOE);
        assertThat(firearm.getAmmunitionStorage().getMagazineAmmo()).isEqualTo(30);
        assertThat(firearm.getAmmunitionStorage().getMagazineSize()).isEqualTo(30);
        assertThat(firearm.getAmmunitionStorage().getMaxAmmo()).isEqualTo(240);
        assertThat(firearm.getAmmunitionStorage().getReserveAmmo()).isEqualTo(90);

        verify(gunRegistry).register(firearm);
        verify(itemMeta).setDamage(8);
        verify(itemMeta).setDisplayName("§fMP5 30/90");
    }

    @Test
    public void createMakesFirearmInstanceWithScopeAttachmentIfConfigurationIsPresent() {
        DefaultScopeAttachment scopeAttachment = mock(DefaultScopeAttachment.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = new Float[] { 0.1f, 0.2f };

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;
        spec.shooting.projectile.headshotDamageMultiplier = null;

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Firearm.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(Reloadable.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(scopeAttachmentProvider.get()).thenReturn(scopeAttachment);
        when(shootHandlerFactory.create(eq(spec.shooting), any(AmmunitionStorage.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        FirearmFactory firearmFactory = new FirearmFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultFirearmProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);
        assertThat(firearm.getScopeAttachment()).isNotNull();

        verify(gunRegistry).register(firearm);
    }

    @Test
    public void createMakesFirearmAndAssignsPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GunSpec spec = this.createGunSpec();

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Firearm.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(Reloadable.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(shootHandlerFactory.create(eq(spec.shooting), any(AmmunitionStorage.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        FirearmFactory firearmFactory = new FirearmFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultFirearmProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec, gamePlayer);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        GunFireSimulationInfo gunFireSimulationInfo = gunFireSimulationInfoCaptor.getValue();
        assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        assertThat(gunFireSimulationInfo.shotSounds()).hasSize(3);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);
        assertThat(firearm.getHolder()).isEqualTo(gamePlayer);

        verify(gunRegistry).register(firearm, gamePlayer);
    }

    private GunSpec createGunSpec() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
