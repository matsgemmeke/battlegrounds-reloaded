package nl.matsgemmeke.battlegrounds.item.deployment;

public interface Deployable {

    boolean isDeployed();

    void onDeploy();
}
