package nl.matsgemmeke.battlegrounds.item.deploy;

public record DeploymentResult(DeploymentObject object, boolean success) {

    public static DeploymentResult success(DeploymentObject object) {
        return new DeploymentResult(object, true);
    }

    public static DeploymentResult failure() {
        return new DeploymentResult(null, false);
    }
}
