package nl.matsgemmeke.battlegrounds.item.deploy;

public class DeploymentProperties {

    private boolean activatedOnDestroy;
    private boolean resetOnDestroy;
    private double health;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
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
