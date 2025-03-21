package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivationFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

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
    public void shouldCreateSimpleEquipmentItem() {
        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void createEquipmentItemWithDisplayName() {
        when(rootSection.getString("item.display-name")).thenReturn("&f%name%");

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingEquipmentItemWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(EquipmentCreationException.class, () -> factory.create(configuration, gameKey));
    }

    @Test
    public void shouldCreateEquipmentItemWithActivatorItem() {
        int damage = 1;
        String displayName = "&fActivator";

        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.FLINT)).thenReturn(itemMeta);

        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getInt("damage")).thenReturn(damage);
        when(activatorItemSection.getString("display-name")).thenReturn(displayName);
        when(activatorItemSection.getString("material")).thenReturn("FLINT");

        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidActivatorMaterial() {
        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getString("material")).thenReturn("fail");
        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(EquipmentCreationException.class, () -> factory.create(configuration, gameKey));
    }

    @Test
    public void makeEquipmentItemWithEffectActivation() {
        Section effectSection = mock(Section.class);
        when(rootSection.getSection("effect")).thenReturn(effectSection);

        Section effectActivationSection = mock(Section.class);
        when(rootSection.getSection("effect.activation")).thenReturn(effectActivationSection);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.create(gameKey, effectActivationSection, null)).thenReturn(effectActivation);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.create(effectSection, gameKey, effectActivation)).thenReturn(effect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(effect)).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertEquals(effect, equipment.getEffect());
    }

    @Test
    public void makeEquipmentItemWithDeploymentProperties() {
        boolean activateOnDestroy = true;
        boolean resetOnDestroy = true;
        double health = 10.0;
        double resistanceBulletDamage = 0.0;
        double resistanceExplosiveDamage = 0.5;

        Section deploySection = mock(Section.class);
        when(deploySection.contains("resistances.bullet-damage")).thenReturn(true);
        when(deploySection.contains("resistances.explosive-damage")).thenReturn(true);
        when(deploySection.getBoolean("on-destroy.activate")).thenReturn(activateOnDestroy);
        when(deploySection.getBoolean("on-destroy.reset")).thenReturn(resetOnDestroy);
        when(deploySection.getDouble("health")).thenReturn(health);
        when(deploySection.getDouble("resistances.bullet-damage")).thenReturn(resistanceBulletDamage);
        when(deploySection.getDouble("resistances.explosive-damage")).thenReturn(resistanceExplosiveDamage);

        when(rootSection.getSection("deploy")).thenReturn(deploySection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getDeploymentProperties());
        assertNotNull(equipment.getDeploymentProperties().getResistances());
        assertEquals(activateOnDestroy, equipment.getDeploymentProperties().isActivatedOnDestroy());
        assertEquals(health, equipment.getDeploymentProperties().getHealth());
        assertEquals(resetOnDestroy, equipment.getDeploymentProperties().isResetOnDestroy());
        assertEquals(resistanceBulletDamage, equipment.getDeploymentProperties().getResistances().get(DamageType.BULLET_DAMAGE));
        assertEquals(resistanceExplosiveDamage, equipment.getDeploymentProperties().getResistances().get(DamageType.EXPLOSIVE_DAMAGE));
    }

    @Test
    public void makeEquipmentItemWithBounceEffect() {
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

        MockedConstruction<BounceEffect> bounceEffectConstructor = mockConstruction(BounceEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(1));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertEquals(1, bounceEffectConstructor.constructed().size());
        bounceEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(BounceEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithSoundEffect() {
        List<Integer> intervals = List.of(10, 20, 30);

        SoundProperties expectedProperties = new SoundProperties(Collections.emptyList(), intervals);

        Section soundSection = mock(Section.class);
        when(soundSection.getIntList("intervals")).thenReturn(intervals);
        when(soundSection.getString("sound")).thenReturn("");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.sound")).thenReturn(soundSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        MockedConstruction<SoundEffect> soundEffectConstructor = mockConstruction(SoundEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(2));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertEquals(1, soundEffectConstructor.constructed().size());
        soundEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(SoundEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithStickEffect() {
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

        MockedConstruction<StickEffect> stickEffectConstructor = mockConstruction(StickEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(2));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertEquals(1, stickEffectConstructor.constructed().size());
        stickEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(StickEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void makeEquipmentItemWithTrailEffect() {
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

        MockedConstruction<TrailEffect> trailEffectConstructor = mockConstruction(TrailEffect.class, (mock, context) -> {
            assertEquals(expectedProperties, context.arguments().get(1));
        });

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertEquals(1, trailEffectConstructor.constructed().size());
        trailEffectConstructor.close();

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
        assertInstanceOf(TrailEffect.class, equipment.getProjectileProperties().getEffects().get(0));
    }

    @Test
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidTrailEffectType() {
        Section trailSection = mock(Section.class);
        when(trailSection.getString("particle.type")).thenReturn("fail");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.trail")).thenReturn(trailSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(EquipmentCreationException.class, () -> factory.create(configuration, gameKey));
    }

    @Test
    public void makeEquipmentItemWithThrowItemTemplate() {
        int damage = 1;

        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getInt("damage")).thenReturn(damage);
        when(throwItemSection.getString("material")).thenReturn("SHEARS");

        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getThrowItemTemplate());
        assertEquals(damage, equipment.getThrowItemTemplate().getDamage());

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void shouldThrowErrorWhenThrowItemMaterialConfigurationValueIsInvalid() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("fail");

        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(EquipmentCreationException.class, () -> factory.create(configuration, gameKey));
    }

    @Test
    public void createMakesEquipmentItemWithControls() {
        ItemControls<EquipmentHolder> controls = mock();
        when(controlsFactory.create(eq(rootSection), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        Section controlsSection = mock(Section.class);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, contextProvider, controlsFactory, effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.create(configuration, gameKey, gamePlayer);
        equipment.onChangeFrom();

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(controls).cancelAllFunctions();
        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }
}
