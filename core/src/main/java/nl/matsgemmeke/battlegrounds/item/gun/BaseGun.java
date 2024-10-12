package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.inventory.meta.ItemMeta;
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
    protected RecoilProducer recoilProducer;
    @Nullable
    protected ReloadSystem reloadSystem;
    @Nullable
    protected ScopeAttachment scopeAttachment;
    @Nullable
    protected TextTemplate displayNameTemplate;

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

    public double getDamageAmplifier() {
        return damageAmplifier;
    }

    public void setDamageAmplifier(double damageAmplifier) {
        this.damageAmplifier = damageAmplifier;
    }

    @Nullable
    public TextTemplate getDisplayNameTemplate() {
        return displayNameTemplate;
    }

    public void setDisplayNameTemplate(@Nullable TextTemplate displayNameTemplate) {
        this.displayNameTemplate = displayNameTemplate;
    }

    @Nullable
    public GunHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable GunHolder holder) {
        this.holder = holder;
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
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayNameTemplate != null) {
            Map<String, Object> values = Map.of(
                    "name", name,
                    "magazine_ammo", this.getMagazineAmmo(),
                    "reserve_ammo", this.getReserveAmmo()
            );
            String displayName = displayNameTemplate.replace(values);

            itemMeta.setDisplayName(displayName);
        }

        itemStack.setItemMeta(itemMeta);

        if (holder != null) {
            holder.setHeldItem(itemStack);
            return true;
        }

        return false;
    }

    public void updateAmmoDisplay() {
        this.update();
    }
}
