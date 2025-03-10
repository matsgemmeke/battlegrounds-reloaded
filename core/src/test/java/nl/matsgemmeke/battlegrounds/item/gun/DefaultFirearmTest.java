package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultFirearmTest {

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private DamageProcessor damageProcessor;
    private GunHolder holder;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        damageProcessor = mock(DamageProcessor.class);
        holder = mock(GunHolder.class);
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void matchesIfItemTemplateMatchesWithGivenItemStack() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(other)).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setItemTemplate(itemTemplate);

        boolean matches = firearm.isMatching(other);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchIfItemTemplateIsNull() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setItemTemplate(null);

        boolean matches = firearm.isMatching(other);

        assertFalse(matches);
    }

    @Test
    public void doesNotMatchIfItemTemplateDoesNotMatchWithGivenItemStack() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(other)).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setItemTemplate(itemTemplate);

        boolean matches = firearm.isMatching(other);

        assertFalse(matches);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCancelOngoingFunctionsWhenHolderIsChangingHeldItems() {
        ItemFunction<GunHolder> function1 = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function1.isPerforming()).thenReturn(true);

        ItemFunction<GunHolder> function2 = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function1);
        firearm.getControls().addControl(Action.CHANGE_FROM, function2);
        firearm.onChangeFrom();

        verify(function1).cancel();
        verify(function2, never()).perform(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executesCorrespondingFunctionWhenChangingItem() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.CHANGE_FROM, function);
        firearm.setHolder(holder);
        firearm.onChangeFrom();

        verify(function).perform(holder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotInteractWithControlsWhenLeftClickedIfHolderIsNull() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function);
        firearm.onLeftClick();

        verifyNoInteractions(function);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executesCorrespondingFunctionWhenLeftClicked() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function);
        firearm.setHolder(holder);
        firearm.onLeftClick();

        verify(function).perform(holder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotInteractWithControlsWhenRightClickedIfHolderIsNull() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.RIGHT_CLICK, function);
        firearm.onRightClick();

        verifyNoInteractions(function);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executesCorrespondingFunctionWhenRightClicked() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.RIGHT_CLICK, function);
        firearm.setHolder(holder);
        firearm.onRightClick();

        verify(function).perform(holder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotInteractWithControlsWhenSwappedFromIfHolderIsNull() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.SWAP_FROM, function);
        firearm.onSwapFrom();

        verifyNoInteractions(function);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executesCorrespondingFunctionWhenSwappedFrom() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.SWAP_FROM, function);
        firearm.setHolder(holder);
        firearm.onSwapFrom();

        verify(function).perform(holder);
    }

    @Test
    public void shouldOnlyBeAbleToShootIfMagazineHasAmmo() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setMagazineAmmo(1);

        boolean canShoot = firearm.canShoot();

        assertTrue(canShoot);
    }

    @Test
    public void shouldProduceRecoilWhenShooting() {
        RecoilProducer recoilProducer = mock(RecoilProducer.class);
        World world = mock(World.class);

        Location startingLocation = new Location(world, 1.0, 1.0, 1.0);

        when(holder.getShootingDirection()).thenReturn(startingLocation);

        when(recoilProducer.produceRecoil(eq(holder), any(Location.class))).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setRecoilProducer(recoilProducer);

        firearm.shoot();

        verify(recoilProducer).produceRecoil(eq(holder), any(Location.class));
    }

    @Test
    public void shouldNeverInflictDamageOnGunHolder() {
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        when(targetFinder.findTargets(eq(holder), any(), eq(0.1))).thenReturn(List.of(holder));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setShotSounds(Collections.emptyList());
        firearm.shoot();

        verify(holder, never()).damage(any(Damage.class));
    }

    @Test
    public void canShootProjectilesAtShortDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 10.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setShortDamage(100.0);
        firearm.setShortRange(10.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(100.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtMediumDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 50.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setMediumDamage(50.0);
        firearm.setMediumRange(50.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(50.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtLongDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 100.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setLongDamage(10.0);
        firearm.setLongRange(100.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(10.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void shootInflictsDamageOnDeploymentObject() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location deploymentObjectLocation = new Location(world, 1.0, 1.0, 2.0, 0.0F, 0.0F);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);

        List<DeploymentObject> deploymentObjects = Collections.singletonList(deploymentObject);

        when(targetFinder.findDeploymentObjects(eq(holder), any(), eq(0.3))).thenReturn(deploymentObjects);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setShortDamage(10.0);
        firearm.setShortRange(5.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(shotSounds, startingLocation);
        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(10.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void shootsMultipleProjectilesBasedOnSpreadPattern() {
        Location shootingDirection = new Location(null, 10.0, 10.0, 10.0);

        when(holder.getShootingDirection()).thenReturn(shootingDirection);

        SpreadPattern pattern = mock(SpreadPattern.class);
        when(pattern.getProjectileDirections(shootingDirection)).thenReturn(List.of(shootingDirection, shootingDirection));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setSpreadPattern(pattern);
        firearm.shoot();

        verify(collisionDetector, times(2)).producesBlockCollisionAt(any());
    }

    @Test
    public void canShootProjectilesAtTargetWithHeadshotDamageMultiplier() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.5, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 0.0, 1.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHeadshotDamageMultiplier(1.5);
        firearm.setHolder(holder);
        firearm.setShortDamage(100.0);
        firearm.setShortRange(10.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(150.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtSolidBlock() {
        List<GameSound> shotSounds = Collections.emptyList();

        Block block = mock(Block.class);
        World world = mock(World.class);

        when(block.getWorld()).thenReturn(world);
        when(block.getType()).thenReturn(Material.STONE);
        when(world.getBlockAt(any())).thenReturn(block);

        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(collisionDetector.producesBlockCollisionAt(any())).thenReturn(true);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(world).playEffect(any(), eq(Effect.STEP_SOUND), eq(Material.STONE));
    }

    @Test
    public void displaysParticlesAtProjectileTrajectory() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);

        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(Collections.emptyList());

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(world).spawnParticle(eq(Particle.REDSTONE), any(), eq(1), eq(0.0), eq(0.0), eq(0.0), eq(0.0), any());
    }

    @Test
    public void canNotShootProjectilesWithoutHolder() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.shoot();

        verify(audioEmitter, never()).playSounds(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCancelFunctionsAndResetHolderWhenDropped() {
        ItemFunction<GunHolder> function = (ItemFunction<GunHolder>) mock(ItemFunction.class);
        when(function.isPerforming()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function);
        firearm.setHolder(holder);
        firearm.onDrop();

        assertNull(firearm.getHolder());

        verify(function).cancel();
    }

    @Test
    public void shouldSetHolderWhenPickedUp() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.onPickUp(holder);

        assertEquals(holder, firearm.getHolder());
    }

    @Test
    public void doesNotUpdateIfFirearmHasNoItemStack() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        boolean updated = firearm.update();

        assertFalse(updated);
    }

    @Test
    public void shouldChangeDisplayNameWhenUpdatingAmmo() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setHolder(holder);
        firearm.setItemTemplate(itemTemplate);
        firearm.setMagazineAmmo(10);
        firearm.setName("name");
        firearm.setReserveAmmo(20);

        firearm.updateAmmoDisplay();

        verify(holder).setHeldItem(itemStack);
    }
}
