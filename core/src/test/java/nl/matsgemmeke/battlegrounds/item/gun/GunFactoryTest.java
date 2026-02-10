package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.GunControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.*;
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

public class GunFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";
    private static final int RATE_OF_FIRE = 600;

    private BattlegroundsConfiguration config;
    private GunControlsFactory controlsFactory;
    private GunInfoProvider gunInfoProvider;
    private GunRegistry gunRegistry;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private Provider<DefaultGun> defaultGunProvider;
    private Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    private ReloadSystemFactory reloadSystemFactory;
    private ShootHandlerFactory shootHandlerFactory;

    @BeforeEach
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        controlsFactory = mock(GunControlsFactory.class);
        gunInfoProvider = mock(GunInfoProvider.class);
        gunRegistry = mock(GunRegistry.class);
        itemFactory = mock(ItemFactory.class);
        scopeAttachmentProvider = mock();
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        shootHandlerFactory = mock(ShootHandlerFactory.class);

        defaultGunProvider = mock();
        when(defaultGunProvider.get()).thenReturn(new DefaultGun());

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
    public void createReturnsSimpleGun() {
        GunSpec spec = this.createGunSpec();
        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Gun.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        GunFactory gunFactory = new GunFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultGunProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        GunFireSimulationInfo gunFireSimulationInfo = gunFireSimulationInfoCaptor.getValue();
        assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        assertThat(gunFireSimulationInfo.shotSounds()).hasSize(3);

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getName()).isEqualTo("MP5");
        assertThat(gun.getItemStack()).isNotNull();
        assertThat(gun.getItemStack().getType()).isEqualTo(Material.IRON_HOE);
        assertThat(gun.getAmmunitionStorage().getMagazineAmmo()).isEqualTo(30);
        assertThat(gun.getAmmunitionStorage().getMagazineSize()).isEqualTo(30);
        assertThat(gun.getAmmunitionStorage().getMaxAmmo()).isEqualTo(240);
        assertThat(gun.getAmmunitionStorage().getReserveAmmo()).isEqualTo(90);

        verify(gunRegistry).register(gun);
        verify(itemMeta).setDamage(8);
        verify(itemMeta).setDisplayName("§fMP5 30/90");
    }

    @Test
    public void createMakesGunInstanceWithScopeAttachmentWhenConfigurationIsPresent() {
        DefaultScopeAttachment scopeAttachment = mock(DefaultScopeAttachment.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = new Float[] { 0.1f, 0.2f };

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Gun.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(scopeAttachmentProvider.get()).thenReturn(scopeAttachment);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        GunFactory gunFactory = new GunFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultGunProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Gun gun = gunFactory.create(spec);

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getScopeAttachment()).isNotNull();

        verify(gunRegistry).register(gun);
    }

    @Test
    public void createMakesGunAndAssignsPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GunSpec spec = this.createGunSpec();

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Gun.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        GunFactory gunFactory = new GunFactory(config, controlsFactory, gunInfoProvider, gunRegistry, keyCreator, defaultGunProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
        Gun gun = gunFactory.create(spec, gamePlayer);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        GunFireSimulationInfo gunFireSimulationInfo = gunFireSimulationInfoCaptor.getValue();
        assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        assertThat(gunFireSimulationInfo.shotSounds()).hasSize(3);

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getHolder()).isEqualTo(gamePlayer);

        verify(gunRegistry).register(gun, gamePlayer);
    }

    private GunSpec createGunSpec() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
