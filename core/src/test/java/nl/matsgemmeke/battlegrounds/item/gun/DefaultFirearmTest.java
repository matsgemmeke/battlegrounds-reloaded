package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    public void applyScopeReturnsFalseIfGunHasNoScopeAttachment() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        boolean applied = firearm.applyScope(scopeUser);

        assertFalse(applied);
    }

    @Test
    public void applyScopeAppliesScopeAttachmentEffectToGivenScopeUser() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.applyEffect(scopeUser)).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setScopeAttachment(scopeAttachment);
        boolean applied = firearm.applyScope(scopeUser);

        assertTrue(applied);
    }

    @Test
    public void cancelReloadCancelsReloadSystemPerformance() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.cancelReload()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setReloadSystem(reloadSystem);
        boolean cancelled = firearm.cancelReload();

        assertTrue(cancelled);
    }

    @Test
    public void cancelScopeReturnsFalseIfGunHasNoScopeAttachment() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        boolean cancelled = firearm.cancelScope();

        assertFalse(cancelled);
    }

    @Test
    public void cancelScopeRemovesScopeAttachmentEffect() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.removeEffect()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setScopeAttachment(scopeAttachment);
        boolean cancelled = firearm.cancelScope();

        assertTrue(cancelled);
    }

    @Test
    public void cancelShootingCycleCancelsFireModeCycle() {
        FireMode fireMode = mock(FireMode.class);
        when(fireMode.cancelCycle()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setFireMode(fireMode);
        boolean cancelled = firearm.cancelShootingCycle();

        assertTrue(cancelled);
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
    public void isReloadAvailableReturnsFalseIfReloadSystemIsPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setReloadSystem(reloadSystem);
        boolean reloadAvailable = firearm.isReloadAvailable();

        assertFalse(reloadAvailable);
    }

    @Test
    public void isReloadAvailableReturnsFalseIfMagazineAmmoEqualsMagazineSize() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setReloadSystem(reloadSystem);
        boolean reloadAvailable = firearm.isReloadAvailable();

        assertFalse(reloadAvailable);
    }

    @Test
    public void isReloadAvailableReturnsFalseIfNoReserveAmmoIsAvailable() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 30, 0, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setReloadSystem(reloadSystem);
        boolean reloadAvailable = firearm.isReloadAvailable();

        assertFalse(reloadAvailable);
    }

    @Test
    public void isReloadingReturnsTrueIfReloadSystemIsPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setReloadSystem(reloadSystem);
        boolean reloading = firearm.isReloading();

        assertTrue(reloading);
    }

    @Test
    public void isReloadingReturnsTrueIfReloadSystemIsNotPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setReloadSystem(reloadSystem);
        boolean reloading = firearm.isReloading();

        assertFalse(reloading);
    }

    @Test
    public void isShootingReturnsTrueIfFireModeIsCycling() {
        FireMode fireMode = mock(FireMode.class);
        when(fireMode.isCycling()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setFireMode(fireMode);
        boolean shooting = firearm.isShooting();

        assertTrue(shooting);
    }

    @Test
    public void isShootingReturnsFalseIfFireModeIsNotCycling() {
        FireMode fireMode = mock(FireMode.class);
        when(fireMode.isCycling()).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setFireMode(fireMode);
        boolean shooting = firearm.isShooting();

        assertFalse(shooting);
    }

    @Test
    public void isUsingScopeReturnsFalseIfGunHasNoScopeAttachment() {
        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        boolean usingScope = firearm.isUsingScope();

        assertFalse(usingScope);
    }

    @Test
    public void isUsingScopeReturnsFalseIfScopeAttachmentIsNotScoped() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.isScoped()).thenReturn(false);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setScopeAttachment(scopeAttachment);
        boolean usingScope = firearm.isUsingScope();

        assertFalse(usingScope);
    }

    @Test
    public void isUsingScopeReturnsTrueIfScopeAttachmentIsScoped() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.isScoped()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setScopeAttachment(scopeAttachment);
        boolean usingScope = firearm.isUsingScope();

        assertTrue(usingScope);
    }

    @Test
    public void shouldCancelOngoingFunctionsWhenHolderIsChangingHeldItems() {
        ItemFunction<GunHolder> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        ItemFunction<GunHolder> function2 = mock();

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function1);
        firearm.getControls().addControl(Action.CHANGE_FROM, function2);
        firearm.onChangeFrom();

        verify(function1).cancel();
        verify(function2, never()).perform(any());
    }

    @Test
    public void executesCorrespondingFunctionWhenChangingItem() {
        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.CHANGE_FROM, function);
        firearm.setHolder(holder);
        firearm.onChangeFrom();

        verify(function).perform(holder);
    }

    @Test
    public void shouldNotInteractWithControlsWhenLeftClickedIfHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function);
        firearm.onLeftClick();

        verifyNoInteractions(function);
    }

    @Test
    public void executesCorrespondingFunctionWhenLeftClicked() {
        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.LEFT_CLICK, function);
        firearm.setHolder(holder);
        firearm.onLeftClick();

        verify(function).perform(holder);
    }

    @Test
    public void shouldNotInteractWithControlsWhenRightClickedIfHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.RIGHT_CLICK, function);
        firearm.onRightClick();

        verifyNoInteractions(function);
    }

    @Test
    public void executesCorrespondingFunctionWhenRightClicked() {
        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.RIGHT_CLICK, function);
        firearm.setHolder(holder);
        firearm.onRightClick();

        verify(function).perform(holder);
    }

    @Test
    public void shouldNotInteractWithControlsWhenSwappedFromIfHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.SWAP_FROM, function);
        firearm.onSwapFrom();

        verifyNoInteractions(function);
    }

    @Test
    public void executesCorrespondingFunctionWhenSwappedFrom() {
        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.getControls().addControl(Action.SWAP_FROM, function);
        firearm.setHolder(holder);
        firearm.onSwapFrom();

        verify(function).perform(holder);
    }

    @Test
    public void reloadActivatesReloadSystemWithGivenPerformerAndUpdatesItem() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setItemTemplate(itemTemplate);
        firearm.setName("test");
        firearm.setReloadSystem(reloadSystem);
        firearm.reload(performer);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(reloadSystem).performReload(eq(performer), procedureCaptor.capture());

        Procedure onReload = procedureCaptor.getValue();
        onReload.apply();

        verify(holder).setHeldItem(itemStack);
        verify(reloadSystem).performReload(performer, onReload);
    }

    @Test
    public void canShootReturnsTrueIfMagazineHasAmmo() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);

        boolean canShoot = firearm.canShoot();

        assertTrue(canShoot);
    }

    @Test
    public void canShootReturnsFalseIfMagazineHasNoAmmo() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 30, 90, 300);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);

        boolean canShoot = firearm.canShoot();

        assertFalse(canShoot);
    }

    @Test
    public void shouldProduceRecoilWhenShooting() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(0, 0, 0, 0, 0, 0);
        RecoilProducer recoilProducer = mock(RecoilProducer.class);
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0);

        when(holder.getShootingDirection()).thenReturn(startingLocation);

        when(recoilProducer.produceRecoil(eq(holder), any(Location.class))).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setRecoilProducer(recoilProducer);
        firearm.shoot();

        verify(recoilProducer).produceRecoil(eq(holder), any(Location.class));
    }

    @Test
    public void shouldNeverInflictDamageOnGunHolder() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(1, 1, 1, 1, 1, 1);
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        when(targetFinder.findTargets(eq(holder), any(), eq(0.1))).thenReturn(List.of(holder));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(Collections.emptyList());
        firearm.shoot();

        verify(holder, never()).damage(any(Damage.class));
    }

    @Test
    public void canShootProjectilesAtShortDistanceTarget() {
        double shortRangeDamage = 100.0;
        double shortRangeDistance = 10.0;
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 10.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(0, 0, 0, 0, shortRangeDamage, shortRangeDistance);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);
        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(List.of(target));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(100.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtMediumDistanceTarget() {
        double mediumRangeDamage = 50.0;
        double mediumRangeDistance = 50.0;
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 50.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(0, 0, mediumRangeDamage, mediumRangeDistance, 0, 0);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);
        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(List.of(target));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(50.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtLongDistanceTarget() {
        double longRangeDamage = 10.0;
        double longRangeDistance = 100.0;
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 100.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, 0, 0, 0, 0);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);
        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(List.of(target));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(10.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void shootInflictsDamageOnDeploymentObject() {
        double shortRangeDamage = 10.0;
        double shortRangeDistance = 5.0;
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location deploymentObjectLocation = new Location(world, 1.0, 1.0, 2.0, 0.0F, 0.0F);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(0, 0, 0, 0, shortRangeDamage, shortRangeDistance);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);
        when(targetFinder.findDeploymentObjects(eq(holder), any(), eq(0.3))).thenReturn(List.of(deploymentObject));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(shotSounds, startingLocation);
        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(shortRangeDamage, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void shootsMultipleProjectilesBasedOnSpreadPattern() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(1, 1, 1, 1, 1, 1);
        Location shootingDirection = new Location(null, 10.0, 10.0, 10.0);

        when(holder.getShootingDirection()).thenReturn(shootingDirection);

        SpreadPattern pattern = mock(SpreadPattern.class);
        when(pattern.getProjectileDirections(shootingDirection)).thenReturn(List.of(shootingDirection, shootingDirection));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setSpreadPattern(pattern);
        firearm.shoot();

        verify(collisionDetector, times(2)).producesBlockCollisionAt(any());
    }

    @Test
    public void canShootProjectilesAtTargetWithHeadshotDamageMultiplier() {
        double shortRangeDamage = 100.0;
        double shortRangeDistance = 10.0;
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.5, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 0.0, 1.0, 0.0F, 0.0F);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(0, 0, 0, 0, shortRangeDamage, shortRangeDistance);

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);
        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(List.of(target));

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHeadshotDamageMultiplier(1.5);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(new Damage(150.0, DamageType.BULLET_DAMAGE));
    }

    @Test
    public void canShootProjectilesAtSolidBlock() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(1, 1, 1, 1, 1, 1);
        List<GameSound> shotSounds = Collections.emptyList();

        Block block = mock(Block.class);
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(block.getWorld()).thenReturn(world);
        when(block.getType()).thenReturn(Material.STONE);
        when(world.getBlockAt(any())).thenReturn(block);

        when(collisionDetector.producesBlockCollisionAt(any())).thenReturn(true);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(audioEmitter).playSounds(eq(shotSounds), any(Location.class));
        verify(world).playEffect(any(), eq(Effect.STEP_SOUND), eq(Material.STONE));
    }

    @Test
    public void displaysParticlesAtProjectileTrajectory() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        RangeProfile rangeProfile = new RangeProfile(1, 1, 1, 1, 1, 1);
        List<GameSound> shotSounds = Collections.emptyList();
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        when(targetFinder.findTargets(any(), any(), anyDouble())).thenReturn(Collections.emptyList());

        when(holder.getRelativeAccuracy()).thenReturn(2.0f);
        when(holder.getShootingDirection()).thenReturn(startingLocation);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setRangeProfile(rangeProfile);
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
    public void shouldCancelFunctionsAndResetHolderWhenDropped() {
        ItemFunction<GunHolder> function = mock();
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

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setAmmunitionStorage(ammunitionStorage);
        firearm.setHolder(holder);
        firearm.setItemTemplate(itemTemplate);
        firearm.setName("name");

        firearm.updateAmmoDisplay();

        verify(holder).setHeldItem(itemStack);
    }
}
