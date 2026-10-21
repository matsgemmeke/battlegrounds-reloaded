package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.configuration.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.item.gun.controls.GunControllerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.*;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachmentFactory;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.SoundService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunFactoryTest {

    private static final int RATE_OF_FIRE = 600;
    private static final SoundSequence SHOOT_SOUND_SEQUENCE = new SoundSequence(null, null);

    private static final Float[] SCOPE_MAGNIFICATIONS = { 0.1f, 0.2f };
    private static final String SCOPE_USE_SOUND_SEQUENCE_ID = "sniper-rifle-scope-use-1";
    private static final SoundSequence SCOPE_USE_SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final String SCOPE_STOP_SOUND_SEQUENCE_ID = "sniper-rifle-scope-stop-1";
    private static final SoundSequence SCOPE_STOP_SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final String SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE_ID = "sniper-rifle-scope-change-magnification-1";
    private static final SoundSequence SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE = new SoundSequence(null, null);

    @Mock
    private DefaultScopeAttachmentFactory scopeAttachmentFactory;
    @Mock
    private GunControllerFactory controllerFactory;
    @Mock
    private GunInfoProvider gunInfoProvider;
    @Mock
    private GunRegistry gunRegistry;
    @Mock
    private ItemTemplateFactory itemTemplateFactory;
    @Mock
    private ReloadSystemFactory reloadSystemFactory;
    @Mock
    private ShootHandlerFactory shootHandlerFactory;
    @Mock
    private SoundService soundService;
    @InjectMocks
    private GunFactory gunFactory;

    @Test
    @DisplayName("create returns simple Gun instance")
    void create_simpleGun() {
        GunSpec spec = this.createGunSpec();
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(itemTemplateFactory.create(spec.item)).thenReturn(itemTemplate);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class), eq("MP5"))).thenReturn(shootHandler);
        when(soundService.getSoundSequence("submachine-gun-shoot-1")).thenReturn(Optional.of(SHOOT_SOUND_SEQUENCE));

        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        assertThat(gunFireSimulationInfoCaptor.getValue()).satisfies(gunFireSimulationInfo -> {
            assertThat(gunFireSimulationInfo.shotSoundSequence()).isEqualTo(SHOOT_SOUND_SEQUENCE);
            assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        });

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getName()).isEqualTo("MP5");
        assertThat(gun.getItemStack()).isEqualTo(itemStack);
        assertThat(gun.getResourceContainer().getCapacity()).isEqualTo(30);
        assertThat(gun.getResourceContainer().getLoadedAmount()).isEqualTo(30);
        assertThat(gun.getResourceContainer().getReserveAmount()).isEqualTo(90);
        assertThat(gun.getResourceContainer().getMaxReserveAmount()).isEqualTo(240);

        verify(controllerFactory).create(eq(spec.controls), any(Gun.class));
        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("creates throws GunCreationException when scope use sound sequence is defined but with an invalid id")
    void create_invalidScopeUseSoundSequenceId() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = SCOPE_MAGNIFICATIONS;
        scopeSpec.useSoundSequence = SCOPE_USE_SOUND_SEQUENCE_ID;

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(soundService.getSoundSequence(SCOPE_USE_SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gunFactory.create(spec))
                .isInstanceOf(GunCreationException.class)
                .hasMessage("Scope use sound sequence 'sniper-rifle-scope-use-1' does not exist");
    }

    @Test
    @DisplayName("creates throws GunCreationException when scope stop sound sequence is defined but with an invalid id")
    void create_invalidScopeStopSoundSequenceId() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = SCOPE_MAGNIFICATIONS;
        scopeSpec.stopSoundSequence = SCOPE_STOP_SOUND_SEQUENCE_ID;

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(soundService.getSoundSequence(SCOPE_STOP_SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gunFactory.create(spec))
                .isInstanceOf(GunCreationException.class)
                .hasMessage("Scope stop sound sequence 'sniper-rifle-scope-stop-1' does not exist");
    }

    @Test
    @DisplayName("creates throws GunCreationException when scope change magnification sound sequence is defined but with an invalid id")
    void create_invalidScopeChangeMagnificationSoundSequenceId() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = SCOPE_MAGNIFICATIONS;
        scopeSpec.changeMagnificationSoundSequence = SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE_ID;

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(soundService.getSoundSequence(SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gunFactory.create(spec))
                .isInstanceOf(GunCreationException.class)
                .hasMessage("Scope change magnification sound sequence 'sniper-rifle-scope-change-magnification-1' does not exist");
    }

    @Test
    @DisplayName("creates returns Gun instance with scope attachment")
    void create_withScopeAttachment() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = SCOPE_MAGNIFICATIONS;
        scopeSpec.useSoundSequence = SCOPE_USE_SOUND_SEQUENCE_ID;
        scopeSpec.stopSoundSequence = SCOPE_STOP_SOUND_SEQUENCE_ID;
        scopeSpec.changeMagnificationSoundSequence = SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE_ID;

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(scopeAttachmentFactory.create(any(ScopeProperties.class))).thenReturn(scopeAttachment);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class), eq("MP5"))).thenReturn(shootHandler);
        when(soundService.getSoundSequence("submachine-gun-shoot-1")).thenReturn(Optional.of(SHOOT_SOUND_SEQUENCE));
        when(soundService.getSoundSequence(SCOPE_USE_SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SCOPE_USE_SOUND_SEQUENCE));
        when(soundService.getSoundSequence(SCOPE_STOP_SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SCOPE_STOP_SOUND_SEQUENCE));
        when(soundService.getSoundSequence(SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE));

        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<ScopeProperties> scopePropertiesCaptor = ArgumentCaptor.forClass(ScopeProperties.class);
        verify(scopeAttachmentFactory).create(scopePropertiesCaptor.capture());

        assertThat(scopePropertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.magnifications()).containsExactly(SCOPE_MAGNIFICATIONS);
            assertThat(properties.useSoundSequence()).isEqualTo(SCOPE_USE_SOUND_SEQUENCE);
            assertThat(properties.stopSoundSequence()).isEqualTo(SCOPE_STOP_SOUND_SEQUENCE);
            assertThat(properties.changeMagnificationSoundSequence()).isEqualTo(SCOPE_CHANGE_MAGNIFICATION_SOUND_SEQUENCE);
        });

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getScopeAttachment()).isNotNull();

        verify(controllerFactory).create(eq(spec.controls), any(Gun.class));
        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("creates returns Gun instance with scope attachment without sound sequences")
    void create_withScopeAttachmentWithoutSoundSequence() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);

        ScopeSpec scopeSpec = new ScopeSpec();
        scopeSpec.magnifications = SCOPE_MAGNIFICATIONS;

        GunSpec spec = this.createGunSpec();
        spec.scope = scopeSpec;

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(scopeAttachmentFactory.create(any(ScopeProperties.class))).thenReturn(scopeAttachment);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class), eq("MP5"))).thenReturn(shootHandler);
        when(soundService.getSoundSequence("submachine-gun-shoot-1")).thenReturn(Optional.of(SHOOT_SOUND_SEQUENCE));

        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<ScopeProperties> scopePropertiesCaptor = ArgumentCaptor.forClass(ScopeProperties.class);
        verify(scopeAttachmentFactory).create(scopePropertiesCaptor.capture());

        assertThat(scopePropertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.magnifications()).containsExactly(SCOPE_MAGNIFICATIONS);
            assertThat(properties.useSoundSequence()).isNull();
            assertThat(properties.stopSoundSequence()).isNull();
            assertThat(properties.changeMagnificationSoundSequence()).isNull();
        });

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getScopeAttachment()).isNotNull();

        verify(controllerFactory).create(eq(spec.controls), any(Gun.class));
        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("create returns Gun without sound sequence")
    void create_withoutSoundSequence() {
        GunSpec spec = this.createGunSpec("src/main/resources/items/submachine_guns/mp5.yml");
        spec.shooting.projectile.soundSequence = null;

        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class), eq("MP5"))).thenReturn(shootHandler);

        Gun gun = gunFactory.create(spec);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        assertThat(gunFireSimulationInfoCaptor.getValue()).satisfies(gunFireSimulationInfo -> {
            assertThat(gunFireSimulationInfo.shotSoundSequence()).isNull();
            assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        });

        assertThat(gun).isInstanceOf(DefaultGun.class);

        verify(controllerFactory).create(spec.controls, gun);
        verify(gunRegistry).register(gun);
    }

    @Test
    @DisplayName("create throws GunCreationException when launch sound sequence does not exist even after validation")
    void create_launchSoundSequenceInvalid() {
        GunSpec spec = this.createGunSpec();
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(soundService.getSoundSequence("submachine-gun-shoot-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gunFactory.create(spec))
                .isInstanceOf(GunCreationException.class)
                .hasMessage("Launch sound sequence 'submachine-gun-shoot-1' does not exist");

        verifyNoInteractions(gunRegistry);
    }

    @Test
    @DisplayName("create returns Gun and assigns player")
    void create_withAssignedPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GunSpec spec = this.createGunSpec();

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        when(shootHandlerFactory.create(eq(spec.shooting), any(ResourceContainer.class), any(ItemRepresentation.class), eq("MP5"))).thenReturn(shootHandler);
        when(soundService.getSoundSequence("submachine-gun-shoot-1")).thenReturn(Optional.of(SHOOT_SOUND_SEQUENCE));

        Gun gun = gunFactory.create(spec, gamePlayer);

        ArgumentCaptor<GunFireSimulationInfo> gunFireSimulationInfoCaptor = ArgumentCaptor.forClass(GunFireSimulationInfo.class);
        verify(gunInfoProvider).registerGunFireSimulationInfo(any(UUID.class), gunFireSimulationInfoCaptor.capture());

        assertThat(gunFireSimulationInfoCaptor.getValue()).satisfies(gunFireSimulationInfo -> {
            assertThat(gunFireSimulationInfo.shotSoundSequence()).isEqualTo(SHOOT_SOUND_SEQUENCE);
            assertThat(gunFireSimulationInfo.rateOfFire()).isEqualTo(RATE_OF_FIRE);
        });

        assertThat(gun).isInstanceOf(DefaultGun.class);
        assertThat(gun.getUser()).isEqualTo(gamePlayer);

        verify(controllerFactory).create(eq(spec.controls), any(Gun.class));
        verify(gunRegistry).register(gun, gamePlayer);
    }

    private GunSpec createGunSpec() {
        return createGunSpec("src/main/resources/items/submachine_guns/mp5.yml");
    }

    private GunSpec createGunSpec(String path) {
        File file = new File(path);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
