package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DefaultGun extends BaseWeapon implements Gun {

    private double damageAmplifier;
    @Nullable
    private GunHolder holder;
    private ItemControls<GunHolder> controls;
    @Nullable
    private ItemTemplate itemTemplate;
    private RangeProfile rangeProfile;
    @Nullable
    private Recoil recoil;
    private ReloadSystem reloadSystem;
    private ResourceContainer resourceContainer;
    @Nullable
    private ScopeAttachment scopeAttachment;
    private ShootHandler shootHandler;

    public DefaultGun() {
        this.controls = new ItemControls<>();
    }

    @Override
    public ResourceContainer getResourceContainer() {
        return resourceContainer;
    }

    @Override
    public void setResourceContainer(ResourceContainer resourceContainer) {
        this.resourceContainer = resourceContainer;
    }

    @NotNull
    public ItemControls<GunHolder> getControls() {
        return controls;
    }

    public void setControls(@NotNull ItemControls<GunHolder> controls) {
        this.controls = controls;
    }

    public double getDamageAmplifier() {
        return damageAmplifier;
    }

    public void setDamageAmplifier(double damageAmplifier) {
        this.damageAmplifier = damageAmplifier;
    }

    @Nullable
    public GunHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable GunHolder holder) {
        this.holder = holder;
    }

    @Nullable
    public ItemTemplate getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(@Nullable ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
    }

    @NotNull
    public RangeProfile getRangeProfile() {
        return rangeProfile;
    }

    public void setRangeProfile(@NotNull RangeProfile rangeProfile) {
        this.rangeProfile = rangeProfile;
    }

    @Nullable
    public Recoil getRecoil() {
        return recoil;
    }

    public void setRecoil(@Nullable Recoil recoil) {
        this.recoil = recoil;
    }

    @NotNull
    public ReloadSystem getReloadSystem() {
        return reloadSystem;
    }

    public void setReloadSystem(@NotNull ReloadSystem reloadSystem) {
        this.reloadSystem = reloadSystem;
    }

    @Nullable
    public ScopeAttachment getScopeAttachment() {
        return scopeAttachment;
    }

    public void setScopeAttachment(@Nullable ScopeAttachment scopeAttachment) {
        this.scopeAttachment = scopeAttachment;
    }

    public ShootHandler getShootHandler() {
        return shootHandler;
    }

    public void setShootHandler(ShootHandler shootHandler) {
        this.shootHandler = shootHandler;
    }

    public boolean applyScope(@NotNull ScopeUser scopeUser) {
        return scopeAttachment != null && scopeAttachment.applyEffect(scopeUser);
    }

    public boolean canShoot() {
        return resourceContainer.getLoadedAmount() > 0;
    }

    public boolean cancelReload() {
        return reloadSystem.cancelReload();
    }

    public boolean cancelScope() {
        return scopeAttachment != null && scopeAttachment.removeEffect();
    }

    public void cancelShooting() {
        shootHandler.cancel();
    }

    public boolean changeScopeMagnification() {
        return scopeAttachment != null && scopeAttachment.nextMagnification();
    }

    public int getRateOfFire() {
        return shootHandler.getRateOfFire();
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack);
    }

    public boolean isReloadAvailable() {
        return !reloadSystem.isPerforming()
                && resourceContainer.getLoadedAmount() < resourceContainer.getCapacity()
                && resourceContainer.getReserveAmount() > 0;
    }

    public boolean isReloading() {
        return reloadSystem.isPerforming();
    }

    public boolean isShooting() {
        return shootHandler.isShooting();
    }

    public boolean isUsingScope() {
        return scopeAttachment != null && scopeAttachment.isScoped();
    }

    public void onChangeFrom() {
        controls.cancelAllFunctions();

        if (holder == null) {
            return;
        }

        controls.performAction(Action.CHANGE_FROM, holder);
    }

    public void onChangeTo() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.CHANGE_TO, holder);
    }

    public void onDrop() {
        if (holder == null) {
            return;
        }

        controls.cancelAllFunctions();
        controls.performAction(Action.DROP_ITEM, holder);
        holder = null;
    }

    public void onLeftClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.LEFT_CLICK, holder);
    }

    public void onRightClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.RIGHT_CLICK, holder);
    }

    public void onPickUp(@NotNull GunHolder holder) {
        this.holder = holder;

        controls.performAction(Action.PICKUP_ITEM, holder);
    }

    public void onSwapFrom() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.SWAP_FROM, holder);
    }

    public void onSwapTo() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.SWAP_TO, holder);
    }

    public void reload(@NotNull ReloadPerformer performer) {
        reloadSystem.performReload(performer, () -> {
            this.update();
            performer.setHeldItem(itemStack);
        });
    }

    public void shoot(@NotNull ShotPerformer performer) {
        shootHandler.shoot(performer);
    }

    public boolean update() {
        if (itemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = itemTemplate.createItemStack(values);
        return true;
    }

    private Map<String, Object> getTemplateValues() {
        return Map.of(
                "name", name,
                "magazine_ammo", resourceContainer.getLoadedAmount(),
                "reserve_ammo", resourceContainer.getReserveAmount()
        );
    }
}
