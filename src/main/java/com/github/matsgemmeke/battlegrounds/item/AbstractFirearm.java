package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class AbstractFirearm extends AbstractWeapon implements Firearm {

    protected double accuracy;
    protected double damageAmplifier;
    protected double longDamage;
    protected double longRange;
    protected double mediumDamage;
    protected double mediumRange;
    protected double recoilAmplifier;
    protected double shortDamage;
    protected double shortRange;

    public AbstractFirearm(
            @NotNull String id,
            @NotNull String name,
            @NotNull String description,
            @NotNull BattleContext context
    ) {
        super(id, name, description, context);
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getDamageAmplifier() {
        return damageAmplifier;
    }

    public void setDamageAmplifier(double damageAmplifier) {
        this.damageAmplifier = damageAmplifier;
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

    public double getRecoilAmplifier() {
        return recoilAmplifier;
    }

    public void setRecoilAmplifier(double recoilAmplifier) {
        this.recoilAmplifier = recoilAmplifier;
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

    @NotNull
    protected Location getShootingDirection(@NotNull Location targetDirection, double relativeAccuracy) {
        Random random = new Random();

        double recoil = (1.0 - accuracy) / relativeAccuracy * recoilAmplifier;

        double pitch = (targetDirection.getPitch() + 90.0 + (random.nextDouble() * recoil - recoil / 2)) * Math.PI / 180.0;
        double yaw = (targetDirection.getYaw() + 90.0 + (random.nextDouble() * recoil - recoil / 2)) * Math.PI / 180.0;

        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Location shootingDirection = targetDirection.clone();
        shootingDirection.setDirection(new Vector(x, z, y));

        return shootingDirection;
    }
}
