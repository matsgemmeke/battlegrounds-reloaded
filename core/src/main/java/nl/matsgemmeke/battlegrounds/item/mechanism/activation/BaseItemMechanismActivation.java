package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemMechanismActivation implements ItemMechanismActivation {

    @NotNull
    protected ItemMechanism mechanism;
    @NotNull
    protected List<BukkitTask> tasks;
    @NotNull
    protected List<Deployable> deployedObjects;

    public BaseItemMechanismActivation(@NotNull ItemMechanism mechanism) {
        this.mechanism = mechanism;
        this.deployedObjects = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void activateDeployedObjects(@NotNull ItemHolder holder) {
        for (Deployable object : deployedObjects) {
            if (object != null) {
                mechanism.activate(holder, object);
            }
        }

        for (BukkitTask task : tasks) {
            task.cancel();
        }

        deployedObjects.clear();
        tasks.clear();
    }

    public void deploy(@NotNull Deployable object) {
        for (int i = 0; i < deployedObjects.size(); i++) {
            if (deployedObjects.get(i) == null) {
                deployedObjects.set(i, object);
            }
        }
    }

    public boolean isPrimed() {
        return !deployedObjects.isEmpty() && deployedObjects.get(deployedObjects.size() - 1) == null;
    }
}
