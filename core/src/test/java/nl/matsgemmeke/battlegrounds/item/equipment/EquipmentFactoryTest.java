package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ManualActivationSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EquipmentFactoryTest {

    private AudioEmitter audioEmitter;
    private DeploymentHandlerFactory deploymentHandlerFactory;
    private EquipmentControlsFactory controlsFactory;
    private EquipmentRegistry equipmentRegistry;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ItemConfiguration configuration;
    private ItemEffectFactory effectFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private ParticleEffectMapper particleEffectMapper;
    private Section rootSection;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deploymentHandlerFactory = mock(DeploymentHandlerFactory.class);
        controlsFactory = mock(EquipmentControlsFactory.class);
        equipmentRegistry = mock(EquipmentRegistry.class);
        gameKey = GameKey.ofTrainingMode();
        configuration = mock(ItemConfiguration.class);
        effectFactory = mock(ItemEffectFactory.class);
        itemFactory = mock(ItemFactory.class);
        keyCreator = mock(NamespacedKeyCreator.class);
        particleEffectMapper = new ParticleEffectMapper();

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, EquipmentRegistry.class)).thenReturn(equipmentRegistry);

        Plugin plugin = mock(Plugin.class);
        Mockito.when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "battlegrounds-equipment");

        keyCreator = mock(NamespacedKeyCreator.class);
        when(keyCreator.create("battlegrounds-equipment")).thenReturn(key);

        rootSection = mock(Section.class);
        when(rootSection.getString("name")).thenReturn("name");
        when(rootSection.getString("description")).thenReturn("description");
        when(rootSection.getInt("item.damage")).thenReturn(1);
        when(rootSection.getString("item.material")).thenReturn("SHEARS");

        when(configuration.getRoot()).thenReturn(rootSection);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createReturnsEquipmentWithPlayerHolder() {
        EquipmentSpec spec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(spec.effect(), gameKey)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, configuration, gameKey, gamePlayer);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getName()).isEqualTo("name");
        assertThat(equipment.getDescription()).isEqualTo("description");

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void createMakesEquipmentWithActivator() {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "name", 1);
        ItemStackSpec activatorItemSpec = new ItemStackSpec("STONE", "&fActivator", 2);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, activatorItemSpec, null, controlsSpec, deploymentSpec, effectSpec, List.of());

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSpec, gameKey)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getActivator()).isNotNull();
        assertThat(equipment.getActivator()).isInstanceOf(DefaultActivator.class);

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void createsMakesEquipmentWithThrowItem() {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "name", 1);
        ItemStackSpec throwItemSpec = new ItemStackSpec("STONE", "&fThrow item", 2);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, null, throwItemSpec, controlsSpec, deploymentSpec, effectSpec, List.of());

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSpec, gameKey)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getThrowItemTemplate()).isNotNull();
        assertThat(equipment.getThrowItemTemplate().getDamage()).isEqualTo(2);
        assertThat(equipment.getThrowItemTemplate().getDisplayNameTemplate()).isNotNull();
        assertThat(equipment.getThrowItemTemplate().getDisplayNameTemplate().getText()).isEqualTo("&fThrow item");

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void makeEquipmentItemWithManualActivation() {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        ManualActivationSpec manualActivationSpec = new ManualActivationSpec(10L, null);
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, manualActivationSpec);
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, null, null, controlsSpec, deploymentSpec, effectSpec, List.of());

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.create(effectSpec, gameKey)).thenReturn(effect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(effect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        ArgumentCaptor<DeploymentProperties> deploymentPropertiesCaptor = ArgumentCaptor.forClass(DeploymentProperties.class);
        verify(deploymentHandlerFactory).create(deploymentPropertiesCaptor.capture(), eq(audioEmitter), eq(effect));

        assertThat(deploymentPropertiesCaptor.getValue().manualActivationDelay()).isEqualTo(manualActivationSpec.delay());
        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
    }

    @Test
    public void createReturnsEquipmentWithDestroyEffect() {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        ParticleEffectSpec destroyEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", 10, 0.1, 0.2, 0.3, 0.0, "STONE");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, destroyEffectSpec, Map.of(), null, null, null, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, null, null, controlsSpec, deploymentSpec, effectSpec, List.of());

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.create(effectSpec, gameKey)).thenReturn(effect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(effect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        ArgumentCaptor<DeploymentProperties> deploymentPropertiesCaptor = ArgumentCaptor.forClass(DeploymentProperties.class);
        verify(deploymentHandlerFactory).create(deploymentPropertiesCaptor.capture(), eq(audioEmitter), eq(effect));

        DeploymentProperties deploymentProperties = deploymentPropertiesCaptor.getValue();

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(deploymentProperties.destroyParticleEffect()).isNotNull();
        assertThat(deploymentProperties.destroyParticleEffect().particle()).isEqualTo(Particle.BLOCK_CRACK);
        assertThat(deploymentProperties.destroyParticleEffect().count()).isEqualTo(destroyEffectSpec.count());
        assertThat(deploymentProperties.destroyParticleEffect().offsetX()).isEqualTo(destroyEffectSpec.offsetX());
        assertThat(deploymentProperties.destroyParticleEffect().offsetY()).isEqualTo(destroyEffectSpec.offsetY());
        assertThat(deploymentProperties.destroyParticleEffect().offsetZ()).isEqualTo(destroyEffectSpec.offsetZ());
        assertThat(deploymentProperties.destroyParticleEffect().extra()).isEqualTo(destroyEffectSpec.extra());
        assertThat(deploymentProperties.destroyParticleEffect().blockDataMaterial()).isEqualTo(Material.STONE);
    }

    private EquipmentSpec createEquipmentSpec() {
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        ItemEffectSpec effectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        return new EquipmentSpec("name", "description", itemSpec, null, null, controlsSpec, deploymentSpec, effectSpec, List.of());
    }
}
