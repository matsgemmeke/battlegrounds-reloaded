package nl.matsgemmeke.battlegrounds.item.deploy;

public record DeploymentContext(String itemName, Deployer deployer, DestructionListener destructionListener) {
}
