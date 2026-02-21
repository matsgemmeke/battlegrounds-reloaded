package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.GunControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.*;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunFactoryTest {

    private static final int RATE_OF_FIRE = 600;

    @Mock
    private GunControlsFactory controlsFactory;
    @Mock
    private GunInfoProvider gunInfoProvider;
    @Mock
    private GunRegistry gunRegistry;
    @Mock
    private ItemTemplateFactory itemTemplateFactory;
    @Mock
    private Provider<DefaultGun> defaultGunProvider;
    @Mock
    private Provider<DefaultScopeAttachment> scopeAttachmentProvider;
    @Mock
    private ReloadSystemFactory reloadSystemFactory;
    @Mock
    private ShootHandlerFactory shootHandlerFactory;

    private GunFactory gunFactory;

    @BeforeEach
    void setUp() {
        when(defaultGunProvider.get()).thenReturn(new DefaultGun());

        gunFactory = new GunFactory(controlsFactory, gunInfoProvider, gunRegistry, itemTemplateFactory, defaultGunProvider, scopeAttachmentProvider, reloadSystemFactory, shootHandlerFactory);
    }

    @Test
    @DisplayName("create returns simple Gun instance")
    void create_simpleGun() {
        GunSpec spec = this.createGunSpec();
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Gun.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(itemTemplateFactory.create(spec.item, "gun")).thenReturn(itemTemplate);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        GunFireSimulationInfo gunFireSimulationInfo = gunFireSimulationInfoCaptor.getValue();
        assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        assertThat(gunFireSimulationInfo.shotSounds()).hasSize(3);

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getName()).isEqualTo("MP5");
        assertThat(gun.getItemStack()).isEqualTo(itemStack);
        assertThat(gun.getResourceContainer().getCapacity()).isEqualTo(30);
        assertThat(gun.getResourceContainer().getLoadedAmount()).isEqualTo(30);
        assertThat(gun.getResourceContainer().getReserveAmount()).isEqualTo(90);
        assertThat(gun.getResourceContainer().getMaxReserveAmount()).isEqualTo(240);

        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("creates returns Gun instance with scope attachment")
    void create_withScopeAttachment() {
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

        Gun gun = gunFactory.create(spec);

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getScopeAttachment()).isNotNull();

        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("create returns Gun and assigns player")
    void create_withAssignedPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GunSpec spec = this.createGunSpec();

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls), any(Gun.class))).thenReturn(controls);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class))).thenReturn(shootHandler);

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
