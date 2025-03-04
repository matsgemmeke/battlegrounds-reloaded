package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BaseGun extends BaseWeapon implements Gun {

    protected double damageAmplifier;
    protected AmmunitionStorage ammunitionStorage;
    protected FireMode fireMode;
    @Nullable
    protected GunHolder holder;
    @NotNull
    protected ItemControls<GunHolder> controls;
    @Nullable
    protected ItemTemplate itemTemplate;
    protected RangeProfile rangeProfile;
    @Nullable
    protected RecoilProducer recoilProducer;
    protected ReloadSystem reloadSystem;
    @Nullable
    protected ScopeAttachment scopeAttachment;

    public BaseGun() {
        this.controls = new ItemControls<>();
    }

    @NotNull
    public AmmunitionStorage getAmmunitionStorage() {
        return ammunitionStorage;
    }

    public void setAmmunitionStorage(@NotNull AmmunitionStorage ammunitionStorage) {
        this.ammunitionStorage = ammunitionStorage;
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
    public RecoilProducer getRecoilProducer() {
        return recoilProducer;
    }

    public void setRecoilProducer(@Nullable RecoilProducer recoilProducer) {
        this.recoilProducer = recoilProducer;
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

    public boolean cancelReload() {
        return reloadSystem.cancelReload();
    }

    public boolean cancelShootingCycle() {
        return fireMode.cancelCycle();
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack);
    }

    public boolean isReloadAvailable() {
        return !reloadSystem.isPerforming()
                && ammunitionStorage.getMagazineAmmo() < ammunitionStorage.getMagazineSize()
                && ammunitionStorage.getReserveAmmo() > 0;
    }

    public boolean isReloading() {
        return reloadSystem.isPerforming();
    }

    public boolean isShooting() {
        return fireMode.isCycling();
    }

    public void onDrop() {
        if (holder == null) {
            return;
        }

        controls.cancelAllFunctions();
        controls.performAction(Action.DROP_ITEM, holder);
        holder = null;
    }

    public void onPickUp(@NotNull GunHolder holder) {
        this.holder = holder;

        controls.performAction(Action.PICKUP_ITEM, holder);
    }

    public void onSwapTo() {
        System.out.println("swap to");
    }

    public void reload(@NotNull ReloadPerformer performer) {
        reloadSystem.performReload(performer, this::update);
    }

    public boolean update() {
        if (itemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = itemTemplate.createItemStack(values);

        if (holder != null) {
            holder.setHeldItem(itemStack);
        }

        return true;
    }

    @NotNull
    private Map<String, Object> getTemplateValues() {
        return Map.of(
                "name", name,
                "magazine_ammo", ammunitionStorage.getMagazineAmmo(),
                "reserve_ammo", ammunitionStorage.getReserveAmmo()
        );
    }

    public void updateAmmoDisplay() {
        this.update();
    }
}
