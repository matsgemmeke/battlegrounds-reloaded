package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.FirearmControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
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
import org.mockito.MockedStatic;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FirearmFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";

    private AudioEmitter audioEmitter;
    private BattlegroundsConfiguration config;
    private DefaultFirearmFactory defaultGunFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunRegistry gunRegistry;
    private FirearmControlsFactory controlsFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private ReloadSystemFactory reloadSystemFactory;
    private ShootHandlerFactory shootHandlerFactory;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        config = mock(BattlegroundsConfiguration.class);
        gameKey = GameKey.ofOpenMode();
        gunRegistry = mock(GunRegistry.class);
        controlsFactory = mock(FirearmControlsFactory.class);
        itemFactory = mock(ItemFactory.class);
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        shootHandlerFactory = mock(ShootHandlerFactory.class);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        defaultGunFactory = mock(DefaultFirearmFactory.class);
        when(defaultGunFactory.create("MP5")).thenReturn(new DefaultFirearm("MP5", audioEmitter, collisionDetector, damageProcessor, targetFinder));

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
        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        GunSpec spec = this.createGunSpec();

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Firearm.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(Reloadable.class))).thenReturn(reloadSystem);

        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);

        FirearmFactory firearmFactory = new FirearmFactory(config, defaultGunFactory, contextProvider, controlsFactory, gunRegistry, keyCreator, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec, gameKey);

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
        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = new Float[] { 0.1f, 0.2f };

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;
        spec.shooting.projectile.headshotDamageMultiplier = null;

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Firearm.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(Reloadable.class))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, defaultGunFactory, contextProvider, controlsFactory, gunRegistry, keyCreator, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertNotNull(firearm.getScopeAttachment());

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

        FirearmFactory firearmFactory = new FirearmFactory(config, defaultGunFactory, contextProvider, controlsFactory, gunRegistry, keyCreator, reloadSystemFactory, shootHandlerFactory);
        Firearm firearm = firearmFactory.create(spec, gameKey, gamePlayer);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals(gamePlayer, firearm.getHolder());

        verify(gunRegistry).register(firearm, gamePlayer);
    }

    private GunSpec createGunSpec() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
