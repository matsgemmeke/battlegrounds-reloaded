package nl.matsgemmeke.battlegrounds.item.deploy;

/**
 * An item that can be deployed.
 */
public interface DeployableItem {

    /**
     * Destroys the deployment object and cancels the deployment process. This method does not have any effects if no
     * deployments have taken place before invoking.
     */
    void destroyDeployment();
}
