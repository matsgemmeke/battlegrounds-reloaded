package nl.matsgemmeke.battlegrounds.item.deploy;

public class DeploymentProperties {

    private boolean activatedOnDestroy;
    private boolean cancelledOnDestroy;
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

    public boolean isCancelledOnDestroy() {
        return cancelledOnDestroy;
    }

    public void setCancelledOnDestroy(boolean cancelledOnDestroy) {
        this.cancelledOnDestroy = cancelledOnDestroy;
    }
}
