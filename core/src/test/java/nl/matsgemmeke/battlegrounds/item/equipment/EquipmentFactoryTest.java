package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ManualActivationSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivationFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce.BounceProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.sound.SoundProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.stick.StickProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailProperties;
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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EquipmentFactoryTest {

    private AudioEmitter audioEmitter;
    private DeploymentHandlerFactory deploymentHandlerFactory;
    private EquipmentControlsFactory controlsFactory;
    private EquipmentRegistry equipmentRegistry;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ItemConfiguration configuration;
    private ItemEffectActivationFactory effectActivationFactory;
    private ItemEffectFactory effectFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private ParticleEffectMapper particleEffectMapper;
    private Section deploySection;
    private Section effectSection;
    private Section effectActivationSection;
    private Section rootSection;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        deploymentHandlerFactory = mock(DeploymentHandlerFactory.class);
        controlsFactory = mock(EquipmentControlsFactory.class);
        equipmentRegistry = mock(EquipmentRegistry.class);
        gameKey = GameKey.ofTrainingMode();
        configuration = mock(ItemConfiguration.class);
        effectActivationFactory = mock(ItemEffectActivationFactory.class);
        effectFactory = mock(ItemEffectFactory.class);
        itemFactory = mock(ItemFactory.class);
        keyCreator = mock(NamespacedKeyCreator.class);
        particleEffectMapper = new ParticleEffectMapper();
        deploySection = mock(Section.class);
        effectSection = mock(Section.class);
        effectActivationSection = mock(Section.class);
        taskRunner = mock(TaskRunner.class);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, EquipmentRegistry.class)).thenReturn(equipmentRegistry);

        Plugin plugin = mock(Plugin.class);
        Mockito.when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "battlegrounds-equipment");

        keyCreator = mock(NamespacedKeyCreator.class);
        when(keyCreator.create("battlegrounds-equipment")).thenReturn(key);

        rootSection = mock(Section.class);
        when(rootSection.getOptionalSection("deploy")).thenReturn(Optional.of(deploySection));
        when(rootSection.getOptionalSection("effect")).thenReturn(Optional.of(effectSection));
        when(rootSection.getOptionalSection("effect.activation")).thenReturn(Optional.of(effectActivationSection));
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
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey, gamePlayer);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getName()).isEqualTo("name");
        assertThat(equipment.getDescription()).isEqualTo("description");

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void createMakesEquipmentWithActivator() {
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ItemStackSpec activatorItemSpec = new ItemStackSpec("STONE", "&fActivator", 2);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        EquipmentSpec spec = new EquipmentSpec("name", "description", itemSpec, activatorItemSpec, null, controlsSpec, deploymentSpec);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), eq(deploymentSpec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(eq(gameKey), eq(effectActivationSection), any(Activator.class))).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getActivator()).isNotNull();
        assertThat(equipment.getActivator()).isInstanceOf(DefaultActivator.class);

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void createsMakesEquipmentWithThrowItem() {
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ItemStackSpec throwItemSpec = new ItemStackSpec("STONE", "&fThrow item", 2);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        EquipmentSpec spec = new EquipmentSpec("name", "description", itemSpec, null, throwItemSpec, controlsSpec, deploymentSpec);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), eq(deploymentSpec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
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
        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, null, null, controlsSpec, deploymentSpec);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(effectActivation);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, effectActivation)).thenReturn(effect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(effect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        ArgumentCaptor<DeploymentProperties> deploymentPropertiesCaptor = ArgumentCaptor.forClass(DeploymentProperties.class);
        verify(deploymentHandlerFactory).create(deploymentPropertiesCaptor.capture(), eq(audioEmitter), eq(effect));

        assertThat(deploymentPropertiesCaptor.getValue().activationDelay()).isEqualTo(manualActivationSpec.activationDelay());
        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
    }

    @Test
    public void createReturnsEquipmentWithDestroyEffect() {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        ParticleEffectSpec destroyEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", 10, 0.1, 0.2, 0.3, 0.0, "STONE");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, destroyEffectSpec, Map.of(), null, null, null, null);
        EquipmentSpec spec = new EquipmentSpec("name", "description", displayItemSpec, null, null, controlsSpec, deploymentSpec);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(effectActivation);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, effectActivation)).thenReturn(effect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(audioEmitter), eq(effect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
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

    @Test
    public void makeEquipmentItemWithBounceEffect() {
        EquipmentSpec spec = this.createEquipmentSpec();

        int amountOfBounces = 1;
        double horizontalFriction = 2.0;
        double verticalFriction = 2.0;
        long checkDelay = 0;
        long checkPeriod = 1;

        BounceProperties expectedProperties = new BounceProperties(amountOfBounces, horizontalFriction, verticalFriction, checkDelay, checkPeriod);

        Section bounceSection = mock(Section.class);
        when(bounceSection.getInt("amount-of-bounces")).thenReturn(amountOfBounces);
        when(bounceSection.getDouble("horizontal-friction")).thenReturn(horizontalFriction);
        when(bounceSection.getDouble("vertical-friction")).thenReturn(verticalFriction);
        when(bounceSection.getLong("check-delay")).thenReturn(checkDelay);
        when(bounceSection.getLong("check-period")).thenReturn(checkPeriod);

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.bounce")).thenReturn(bounceSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        MockedConstruction<BounceEffect> bounceEffectConstructor = mockConstruction(BounceEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(1));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertEquals(1, bounceEffectConstructor.constructed().size());
        bounceEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(BounceEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithSoundEffect() {
        EquipmentSpec spec = this.createEquipmentSpec();

        List<Integer> intervals = List.of(10, 20, 30);
        SoundProperties expectedProperties = new SoundProperties(Collections.emptyList(), intervals);

        Section soundSection = mock(Section.class);
        when(soundSection.getIntList("intervals")).thenReturn(intervals);
        when(soundSection.getString("sounds")).thenReturn("");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.sound")).thenReturn(soundSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        MockedConstruction<SoundEffect> soundEffectConstructor = mockConstruction(SoundEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(2));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertEquals(1, soundEffectConstructor.constructed().size());
        soundEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(SoundEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithStickEffect() {
        EquipmentSpec spec = this.createEquipmentSpec();

        long checkDelay = 0;
        long checkPeriod = 1;
        StickProperties expectedProperties = new StickProperties(Collections.emptyList(), checkDelay, checkPeriod);

        Section stickSection = mock(Section.class);
        when(stickSection.getLong("check-delay")).thenReturn(checkDelay);
        when(stickSection.getLong("check-period")).thenReturn(checkPeriod);
        when(stickSection.getString("stick-sounds")).thenReturn("");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.stick")).thenReturn(stickSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        MockedConstruction<StickEffect> stickEffectConstructor = mockConstruction(StickEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(2));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertEquals(1, stickEffectConstructor.constructed().size());
        stickEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(StickEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithTrailEffect() {
        EquipmentSpec spec = this.createEquipmentSpec();

        int particleCount = 1;
        double particleOffsetX = 0.1;
        double particleOffsetY = 0.2;
        double particleOffsetZ = 0.3;
        double particleExtra = 0.0;
        long checkDelay = 5;
        long checkPeriod = 2;

        ParticleEffectProperties particleEffect = new ParticleEffectProperties(Particle.FLAME, particleCount, particleOffsetX, particleOffsetY, particleOffsetZ, particleExtra);
        TrailProperties expectedProperties = new TrailProperties(particleEffect, checkDelay, checkPeriod);

        Section trailSection = mock(Section.class);
        when(trailSection.getLong("check-delay")).thenReturn(checkDelay);
        when(trailSection.getLong("check-period")).thenReturn(checkPeriod);
        when(trailSection.getInt("particle.count")).thenReturn(particleCount);
        when(trailSection.getDouble("particle.offset-x")).thenReturn(particleOffsetX);
        when(trailSection.getDouble("particle.offset-y")).thenReturn(particleOffsetY);
        when(trailSection.getDouble("particle.offset-z")).thenReturn(particleOffsetZ);
        when(trailSection.getDouble("particle.extra")).thenReturn(particleExtra);
        when(trailSection.getString("particle.type")).thenReturn("FLAME");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.trail")).thenReturn(trailSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        MockedConstruction<TrailEffect> trailEffectConstructor = mockConstruction(TrailEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(1));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);
        Equipment equipment = factory.create(spec, configuration, gameKey);

        assertEquals(1, trailEffectConstructor.constructed().size());
        trailEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(TrailEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidTrailEffectType() {
        EquipmentSpec spec = this.createEquipmentSpec();

        Section trailSection = mock(Section.class);
        when(trailSection.getString("particle.type")).thenReturn("fail");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.trail")).thenReturn(trailSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec.controls()), eq(spec.deployment()), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffectActivation itemEffectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(itemEffectActivation);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, itemEffectActivation)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), any(AudioEmitter.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, particleEffectMapper, taskRunner);

        assertThrows(EquipmentCreationException.class, () -> factory.create(spec, configuration, gameKey));
    }

    private EquipmentSpec createEquipmentSpec() {
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK", "RIGHT_CLICK");
        DeploymentSpec deploymentSpec = new DeploymentSpec(50.0, true, false, false, null, Map.of(), null, null, null, null);
        return new EquipmentSpec("name", "description", itemSpec, null, null, controlsSpec, deploymentSpec);
    }
}
