package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BaseGun extends BaseWeapon implements Gun {

    protected double accuracy;
    protected double damageAmplifier;
    protected double longDamage;
    protected double longRange;
    protected double mediumDamage;
    protected double mediumRange;
    protected double shortDamage;
    protected double shortRange;
    @Nullable
    protected GunHolder holder;
    protected int maxAmmo;
    @NotNull
    protected ItemControls<GunHolder> controls;
    @Nullable
    protected ItemTemplate itemTemplate;
    @Nullable
    protected RecoilProducer recoilProducer;
    @Nullable
    protected ReloadSystem reloadSystem;
    @Nullable
    protected ScopeAttachment scopeAttachment;

    public BaseGun() {
        this.controls = new ItemControls<>();
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
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

    public double getLongDamage() {
        return longDamage;
    }

    public void setLongDamage(double longDamage) {
        this.longDamage = longDamage;
    }

    public double getLongRange() {
        return longRange;
    }

    public void setLongRange(double longRange) {
        this.longRange = longRange;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public double getMediumDamage() {
        return mediumDamage;
    }

    public void setMediumDamage(double mediumDamage) {
        this.mediumDamage = mediumDamage;
    }

    public double getMediumRange() {
        return mediumRange;
    }

    public void setMediumRange(double mediumRange) {
        this.mediumRange = mediumRange;
    }

    @Nullable
    public RecoilProducer getRecoilProducer() {
        return recoilProducer;
    }

    public void setRecoilProducer(@Nullable RecoilProducer recoilProducer) {
        this.recoilProducer = recoilProducer;
    }

    @Nullable
    public ReloadSystem getReloadSystem() {
        return reloadSystem;
    }

    public void setReloadSystem(@Nullable ReloadSystem reloadSystem) {
        this.reloadSystem = reloadSystem;
    }

    @Nullable
    public ScopeAttachment getScopeAttachment() {
        return scopeAttachment;
    }

    public void setScopeAttachment(@Nullable ScopeAttachment scopeAttachment) {
        this.scopeAttachment = scopeAttachment;
    }

    public double getShortDamage() {
        return shortDamage;
    }

    public void setShortDamage(double shortDamage) {
        this.shortDamage = shortDamage;
    }

    public double getShortRange() {
        return shortRange;
    }

    public void setShortRange(double shortRange) {
        this.shortRange = shortRange;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack);
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
                "magazine_ammo", this.getMagazineAmmo(),
                "reserve_ammo", this.getReserveAmmo()
        );
    }

    public void updateAmmoDisplay() {
        this.update();
    }
}
