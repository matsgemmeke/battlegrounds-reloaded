package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DeploymentProperties {

    private boolean activatedOnDestroy;
    private boolean resetOnDestroy;
    private double health;
    @Nullable
    private Map<DamageType, Double> resistances;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Nullable
    public Map<DamageType, Double> getResistances() {
        return resistances;
    }

    public void setResistances(@Nullable Map<DamageType, Double> resistances) {
        this.resistances = resistances;
    }

    public boolean isActivatedOnDestroy() {
        return activatedOnDestroy;
    }

    public void setActivatedOnDestroy(boolean activatedOnDestroy) {
        this.activatedOnDestroy = activatedOnDestroy;
    }

    public boolean isResetOnDestroy() {
        return resetOnDestroy;
    }

    public void setResetOnDestroy(boolean resetOnDestroy) {
        this.resetOnDestroy = resetOnDestroy;
    }
}
