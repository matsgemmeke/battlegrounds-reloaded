package nl.matsgemmeke.battlegrounds.item.deploy.activator;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultActivator implements Activator {

    private boolean ready;
    @Nullable
    private Deployer currentDeployer;
    @Nullable
    private ItemStack heldItemStack;
    @NotNull
    private ItemTemplate itemTemplate;

    public DefaultActivator(@NotNull ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
        this.ready = false;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate.matchesTemplate(itemStack);
    }

    public void prepare(@NotNull Deployer deployer) {
        // Do not prepare the activator again if already performed
        if (currentDeployer != null || heldItemStack != null) {
            return;
        }

        ItemStack itemStack = itemTemplate.createItemStack();
        deployer.setHeldItem(itemStack);

        currentDeployer = deployer;
        heldItemStack = itemStack;
        ready = true;
    }

    public boolean remove() {
        if (currentDeployer == null || heldItemStack == null) {
            return false;
        }

        currentDeployer.removeItem(heldItemStack);
        currentDeployer = null;
        heldItemStack = null;
        return true;
    }
}
